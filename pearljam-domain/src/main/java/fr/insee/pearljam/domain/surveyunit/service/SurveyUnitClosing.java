package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.PaginatedSurveyUnitClosingPresenter;
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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        // closing cause can be udpate in temporary clo
        if (toClose) {
            validateNoExistingClosingCause(surveyUnitIds);
        }
        validateClosableStates(surveyUnitIds);
        applyClosingCause(surveyUnitIds, type);

        if (toClose) {
            closeSurveyUnits(surveyUnitIds);
        }
    }

    @Override
    public <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter) {
        return getSurveyUnitsToClose(userId, presenter, Pageable.unpaged());
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter, Pageable pageable) {
        List<String> lstOuIds = userService.getUserOUsModel(userId, true).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        long now = dateService.getCurrentTimestamp();

        // 1. Retrieve all eligible survey unit IDs (lightweight: only IDs)
        List<String> allEligibleIds = surveyUnitRepository.findEligibleSurveyUnitIds(now, lstOuIds);

        if (allEligibleIds.isEmpty()) {
            return presenter.empty();
        }

        // 2. Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allEligibleIds.size());

        if (start >= allEligibleIds.size()) {
            return presenter.empty();
        }

        List<String> pagedIds = allEligibleIds.subList(start, end);

        // 3. Retrieve candidates for this page
        List<ClosableSurveyUnitCandidateView> candidates =
                surveyUnitRepository.findClosableCandidatesByIds(pagedIds, now);

        if (candidates.isEmpty()) {
            return presenter.empty();
        }

        Map<String, ClosableSurveyUnitCandidateView> candidatesById = candidates.stream()
                .collect(Collectors.toMap(
                        ClosableSurveyUnitCandidateView::getId,
                        Function.identity()
                ));

        // 4. Retrieve questionnaire states for this page
        Map<String, String> states = questionnaireStatePort.getStates(candidatesById.keySet());

        // 5. Filter eligible survey units based on policy
        Set<String> eligibleIds = candidates.stream()
                .filter(candidate -> surveyUnitClosablePolicy.isClosable(candidate, states.get(candidate.getId())))
                .map(ClosableSurveyUnitCandidateView::getId)
                .collect(Collectors.toSet());

        if (eligibleIds.isEmpty()) {
            return presenter.empty();
        }

        // 6. Retrieve full projections for eligible survey units
        List<ClosableSurveyUnitView> closableSurveyUnitProjections =
                surveyUnitRepository.findClosableSurveyUnits(eligibleIds);

        // 7. Present results - handle both paginated and non-paginated presenters
        if (presenter instanceof PaginatedSurveyUnitClosingPresenter<?>) {
            PaginatedSurveyUnitClosingPresenter<T> paginatedPresenter = (PaginatedSurveyUnitClosingPresenter<T>) presenter;
            return paginatedPresenter.present(
                    closableSurveyUnitProjections,
                    candidatesById,
                    states,
                    allEligibleIds.size(),
                    pageable.getPageNumber(),
                    pageable.getPageSize()
            );
        }

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

    private void applyClosingCause(List<String> surveyUnitIds, ClosingCauseType type) {
        closingCauseRepository.addClosingCauseToSurveyUnits(surveyUnitIds, type);
    }

    private void closeSurveyUnits(List<String> surveyUnitIds) {
        stateRepository.saveStateForSurveyUnits(
            surveyUnitIds,
            StateType.CLO,
            Instant.now()
        );
    }


}