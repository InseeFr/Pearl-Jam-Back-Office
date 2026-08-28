package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPresenter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitExistencePort;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStatePort;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import fr.insee.pearljam.domain.surveyunit.service.exception.ClosingCauseAlreadyExistsException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotClosableException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final StateRepository stateRepository;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepositoryPort;

    @Override
    @Transactional
    public void addClosingCauseToMultipleSurveyUnits(
        List<String> surveyUnitIds,
        ClosingCauseType type,
        boolean toClose
    ) {
        if (surveyUnitIds == null || surveyUnitIds.isEmpty()) {
            return;
        }

        validateSurveyUnitsExist(surveyUnitIds);
        validateClosableStates(surveyUnitIds);

        if (toClose) {
            handleCloseFlow(surveyUnitIds, type);
        } else {
            handleUpdateFlow(surveyUnitIds, type);
        }
    }

    private void handleCloseFlow(List<String> ids, ClosingCauseType type) {
        // Survey units with existing provisional closing causes should be allowed to be closed
        // (validateClosableStates already ensures they're not in CLO/TBR/FIN states,
        // so any existing closing causes must be provisional)
        closingCauseRepository.updateExistingClosingCauseToSurveyUnits(ids, type);
        closingCauseRepository.addClosingCauseToSurveyUnits(ids, type);

        closeSurveyUnits(ids);

        campaignDailyStatsRepositoryPort.updateDailyStatsForSurveyUnits(ids, StateType.CLO, type);
    }

    private void handleUpdateFlow(List<String> ids, ClosingCauseType type) {
        closingCauseRepository.updateExistingClosingCauseToSurveyUnits(ids, type);
        //insert missing
        closingCauseRepository.addClosingCauseToSurveyUnits(ids, type);

        campaignDailyStatsRepositoryPort.updateDailyStatsForSurveyUnits(ids, null, type);
    }

    @Override
    public <T> T getSurveyUnitsToClose(String userId, @Nullable String campaignId, SurveyUnitClosingPresenter<T> presenter) {


        List<String> lstOuIds = userService.getUserOUsModel(userId, true).stream()
            .map(OrganizationUnitSummary::getId)
            .toList();

        long now = dateService.getCurrentTimestamp();

        List<ClosableSurveyUnitCandidateView> candidates =
            surveyUnitRepository.findClosableCandidates(now, campaignId, lstOuIds);

        if (candidates.isEmpty()) {
            return presenter.empty();
        }

        Map<String, ClosableSurveyUnitCandidateView> candidatesById =
            candidates.parallelStream()
                .collect(Collectors.toMap(
                    ClosableSurveyUnitCandidateView::getId,
                    Function.identity()
                ));

        Map<String, String> states =
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

    @Override
    public void deleteClosingCauseBySurveyUnitId(String surveyUnitId) {
        closingCauseRepository.deleteBySurveyUnitId(surveyUnitId);
    }

    void validateSurveyUnitsExist(List<String> surveyUnitIds) {
        List<String> existingSurveyUnits = surveyUnitExistencePort.findExistingIds(surveyUnitIds);

        List<String> missingSurveyUnits = surveyUnitIds.stream()
            .filter(id -> !existingSurveyUnits.contains(id))
            .toList();

        if (!missingSurveyUnits.isEmpty()) {
            log.info("Missing survey units to close {}", missingSurveyUnits);
            throw new SurveyUnitNotFoundException(String.join(", ", missingSurveyUnits));
        }
    }

    void validateNoExistingClosingCause(List<String> surveyUnitIds) {
        List<String> alreadyWithClosingCause =
            closingCauseRepository.findSurveyUnitIdsWithClosingCause(surveyUnitIds);

        if (!alreadyWithClosingCause.isEmpty()) {
            log.info("Closing cause already exist on survey units {}", alreadyWithClosingCause);
            throw new ClosingCauseAlreadyExistsException(String.join(", ", alreadyWithClosingCause));
        }
    }

    private void validateClosableStates(List<String> surveyUnitIds) {

        List<StateType> forbiddenStates = List.of(
            StateType.CLO,
            StateType.TBR,
            StateType.FIN
        );

        List<String> invalidStateUnits =
            stateRepository.findSurveyUnitsInStates(surveyUnitIds, forbiddenStates);

        if (!invalidStateUnits.isEmpty()) {
            log.info("Survey units not closable (invalid state CLO/TBR/FIN) {}", invalidStateUnits);
            throw new SurveyUnitNotClosableException(String.join(", ", invalidStateUnits));
        }

    }

    private void closeSurveyUnits(List<String> surveyUnitIds) {
        stateRepository.saveStateForSurveyUnits(
            surveyUnitIds,
            StateType.CLO,
            Instant.now()
        );
    }


}