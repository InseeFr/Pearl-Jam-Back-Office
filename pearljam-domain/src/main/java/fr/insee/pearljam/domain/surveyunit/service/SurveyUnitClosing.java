package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.constants.Constants;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Transactional
    public <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter, Pageable pageable) {
        List<String> lstOuIds = userService.getUserOUsModel(userId, true).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        long now = dateService.getCurrentTimestamp();

        // 1. Retrieve all eligible survey unit IDs based on SQL criteria (state + contactOutcome)
        List<String> allEligibleIds = surveyUnitRepository.findEligibleSurveyUnitIds(now, lstOuIds);

        if (allEligibleIds.isEmpty()) {
            return presenter.empty();
        }

        // 2. Load ALL candidates for filtering (not just the page)
        List<ClosableSurveyUnitCandidateView> allCandidates =
                surveyUnitRepository.findClosableCandidatesByIds(allEligibleIds, now);

        if (allCandidates.isEmpty()) {
            return presenter.empty();
        }

        // 3. Load ALL questionnaire states for filtering
        Map<String, ClosableSurveyUnitCandidateView> allCandidatesById = allCandidates.stream()
                .collect(Collectors.toMap(
                        ClosableSurveyUnitCandidateView::getId,
                        Function.identity()
                ));
        Map<String, String> allStates = questionnaireStatePort.getStates(allCandidatesById.keySet());

        // 4. Filter ALL candidates based on full policy (including questionnaireState)
        List<String> trulyEligibleIds = allCandidates.stream()
                .filter(candidate -> surveyUnitClosablePolicy.isClosable(candidate, allStates.get(candidate.getId())))
                .map(ClosableSurveyUnitCandidateView::getId)
                .toList();

        long totalEligible = trulyEligibleIds.size();

        if (trulyEligibleIds.isEmpty()) {
            return presenter.empty();
        }

        // 5. Apply pagination on the truly eligible list
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), (int) totalEligible);

        if (start >= totalEligible) {
            return presenter.empty();
        }

        List<String> pagedEligibleIds = trulyEligibleIds.subList(start, end);

        // 6. Load projections for the paginated eligible survey units
        List<ClosableSurveyUnitView> closableSurveyUnitProjections =
                surveyUnitRepository.findClosableSurveyUnits(new HashSet<>(pagedEligibleIds));

        // 7. Get candidates and states for the paginated results
        Map<String, ClosableSurveyUnitCandidateView> pagedCandidatesById = pagedEligibleIds.stream()
                .map(allCandidatesById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        ClosableSurveyUnitCandidateView::getId,
                        Function.identity()
                ));
        Map<String, String> pagedStates = pagedEligibleIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        id -> allStates.getOrDefault(id, Constants.QUESTIONNAIRE_STATE_UNAVAILABLE)
                ));

        // 8. Present results
        if (presenter instanceof PaginatedSurveyUnitClosingPresenter<?>) {
            PaginatedSurveyUnitClosingPresenter<T> paginatedPresenter = (PaginatedSurveyUnitClosingPresenter<T>) presenter;
            return paginatedPresenter.present(
                    closableSurveyUnitProjections,
                    pagedCandidatesById,
                    pagedStates,
                    totalEligible,
                    pageable.getPageNumber(),
                    pageable.getPageSize()
            );
        }

        return presenter.present(
                closableSurveyUnitProjections,
                pagedCandidatesById,
                pagedStates
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