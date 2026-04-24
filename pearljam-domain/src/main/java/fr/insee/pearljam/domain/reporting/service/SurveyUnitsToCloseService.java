package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToCloseStatsPresenter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitsToClosePort;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitsToCloseService implements SurveyUnitsToClosePort {

    private final UserService userService;
    private final DateService dateService;
    private final SurveyUnitRepository surveyUnitRepository;
    private final QuestionnaireStateClient questionnaireStateClient;

    private static final String QUESTIONNAIRE_STATE_UNAVAILABLE = "UNAVAILABLE";


    @Override
    public <T> T getSurveyUnitsToClose(String userId, SurveyUnitToCloseStatsPresenter<T> presenter) {


        List<String> lstOuIds = userService.getUserOUs(userId, true).stream()
                .map(OrganizationUnitDto::getId)
                .toList();

        long now = dateService.getCurrentTimestamp();

        List<ClosableSurveyUnitCandidateView> candidates =
                surveyUnitRepository.findClosableCandidates(now, lstOuIds);

        if (candidates.isEmpty()) {
            return presenter.empty();
        }

        Map<String, ClosableSurveyUnitCandidateView> candidatesById =
                candidates.parallelStream()
                        .collect(Collectors.toMap(
                                ClosableSurveyUnitCandidateView::getId,
                                Function.identity()
                        ));

        final Map<String, String> questionnaireStates = getQuestionnaireStatesFromDataCollection(candidatesById.keySet());

        Map<String, ClosableSurveyUnitCandidateView> eligibleSurveyUnitsById =
                candidates.parallelStream()
                        .filter(candidate -> isClosable(candidate, questionnaireStates.get(candidate.getId())))
                        .collect(Collectors.toMap(
                                ClosableSurveyUnitCandidateView::getId,
                                Function.identity()
                        ));

        List<ClosableSurveyUnitView> closableSurveyUnitProjections =
                surveyUnitRepository.findClosableSurveyUnits(eligibleSurveyUnitsById.keySet());

        return presenter.present(
                closableSurveyUnitProjections,
                candidatesById,
                questionnaireStates
        );
    }

    private boolean isClosable(ClosableSurveyUnitCandidateView candidate, String questionnaireState) {
        StateType currentState = candidate.getCurrentStateType();
        ContactOutcomeType outcomeType = candidate.getContactOutcomeType();

        boolean neverTransmitted =
                currentState != null
                && !Set.of(StateType.TBR, StateType.FIN, StateType.CLO).contains(currentState);

        boolean inaWithoutQuestionnaire =
                outcomeType == ContactOutcomeType.INA
                && (questionnaireState == null || QUESTIONNAIRE_STATE_UNAVAILABLE.equals(questionnaireState));


        if (neverTransmitted) {
            return true;
        }

        return currentState != null && !currentState.equals(StateType.CLO) && inaWithoutQuestionnaire;
    }


    private Map<String, String> getQuestionnaireStatesFromDataCollection(
            Set<String> lstSu) {
        Map<String, String> mapResult = new HashMap<>();
        try {
            ResponseEntity<InterrogationOkNokDto> result = questionnaireStateClient.getQuestionnairesStateFromDataCollection(
                    lstSu);
            log.info("GET state from data collection service call resulting in {}", result.getStatusCode());
            InterrogationOkNokDto object = result.getBody();
            HttpStatusCode responseCode = result.getStatusCode();

            if (!responseCode.equals(HttpStatus.OK)) {
                String code = responseCode.toString();
                log.error("Data collection API responded with error code {}", code);
            }
            if (object == null) {
                log.error("Could not get response from data collection API");
                throw new IllegalStateException("Could not get response from data collection API");
            }
            object.interrogationNOK().forEach(su -> mapResult.put(su.id(), QUESTIONNAIRE_STATE_UNAVAILABLE));
            object.interrogationOK().forEach(su -> mapResult.put(su.id(), su.stateData().getState()));
        } catch (Exception e) {
            log.error("Could not get data collection API : {}", e.getMessage());
            log.error("All questionnaire states will be considered null");
            lstSu.forEach(id -> mapResult.put(id, QUESTIONNAIRE_STATE_UNAVAILABLE));
        }
        return mapResult;
    }
}


