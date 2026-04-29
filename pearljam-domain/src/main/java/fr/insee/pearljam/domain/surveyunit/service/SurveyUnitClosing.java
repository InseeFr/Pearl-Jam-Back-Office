package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitExistencePort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToCloseStatsPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import fr.insee.pearljam.domain.surveyunit.service.exception.ClosingCauseAlreadyExistsException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

import static fr.insee.pearljam.contracts.constants.Constants.QUESTIONNAIRE_STATE_UNAVAILABLE;

@Service
@AllArgsConstructor
@Slf4j
public class SurveyUnitClosing implements SurveyUnitClosingPort {

    private final ClosingCauseRepository closingCauseRepository;
    private final SurveyUnitExistencePort surveyUnitExistencePort;
    private final UserService userService;
    private final DateService dateService;
    private final SurveyUnitRepository surveyUnitRepository;
    private final QuestionnaireStateClient questionnaireStateClient;
    private final SurveyUnitToClosePolicy surveyUnitToClosePolicy;

    @Override
    @Transactional
    public void addClosingCauseToMultipleSurveyUnits(List<String> surveyUnitIds, ClosingCauseType type) {

        List<String> existingSurveyUnits = surveyUnitExistencePort.findExistingIds(surveyUnitIds);
        List<String> missingSurveyUnits = surveyUnitIds.stream()
                .filter(id -> !existingSurveyUnits.contains(id))
                .toList();

        if (!missingSurveyUnits.isEmpty()) {
            log.info("Missing survey units to close {}", missingSurveyUnits);
            throw new SurveyUnitNotFoundException(String.join(", ", missingSurveyUnits));
        }

        List<String> surveyUnitsWithClosingCause =
                closingCauseRepository.findSurveyUnitIdsWithClosingCause(surveyUnitIds);

        if (!surveyUnitsWithClosingCause.isEmpty()) {
            log.info("Closing cause already exist on survey units {}", surveyUnitsWithClosingCause);
            throw new ClosingCauseAlreadyExistsException(String.join(", ", surveyUnitsWithClosingCause));
        }

        closingCauseRepository.addClosingCauseToSurveyUnits(surveyUnitIds, type);
    }

    @Override
    public <T> T getSurveyUnitsToClose(String userId, SurveyUnitToCloseStatsPresenter<T> presenter) {


        List<String> lstOuIds = userService.getUserOUsModel(userId, true).stream()
                .map(OrganizationUnitSummary::getId)
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
                        .filter(candidate -> surveyUnitToClosePolicy.isClosable(candidate, questionnaireStates.get(candidate.getId())))
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