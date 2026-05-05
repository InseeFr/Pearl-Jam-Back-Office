package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.surveyunit.model.QuestionnaireState;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPresenter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitExistencePort;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStatePort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import fr.insee.pearljam.domain.surveyunit.service.exception.ClosingCauseAlreadyExistsException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SurveyUnitClosing implements SurveyUnitClosingPort {

    private final ClosingCauseRepository closingCauseRepository;
    private final SurveyUnitExistencePort surveyUnitExistencePort;
    private final UserService userService;
    private final DateService dateService;
    private final SurveyUnitRepository surveyUnitRepository;
    private final QuestionnaireStatePort questionnaireStatePort;
    private final SurveyUnitClosablePolicy surveyUnitClosablePolicy;

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
    public <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter) {


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

        Map<String, QuestionnaireState> states =
                questionnaireStatePort.getStates(candidatesById.keySet());

        Map<String, ClosableSurveyUnitCandidateView> eligibleSurveyUnitsById =
                candidates.parallelStream()
                        .filter(candidate -> surveyUnitClosablePolicy.isClosable(candidate, states.get(candidate.getId())))
                        .collect(Collectors.toMap(
                                ClosableSurveyUnitCandidateView::getId,
                                Function.identity()
                        ));

        List<ClosableSurveyUnitView> closableSurveyUnitProjections =
                surveyUnitRepository.findClosableSurveyUnits(eligibleSurveyUnitsById.keySet());

        return presenter.present(
                closableSurveyUnitProjections,
                candidatesById,
                states
        );
    }

}