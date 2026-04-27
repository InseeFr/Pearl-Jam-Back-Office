package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitToReviewPort;
import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitToReviewPresenter;
import fr.insee.pearljam.domain.reporting.port.out.SurveyUnitToReviewRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitToReview;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitService;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCampaignDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for retrieving survey units to review.
 * This service implements the {@link SurveyUnitToReviewPort} interface and provides
 * the business logic for getting paginated survey units that need review.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitToReviewService implements SurveyUnitToReviewPort {

    private final SurveyUnitService surveyUnitService;
    private final SurveyUnitToReviewRepositoryPort surveyUnitToReviewRepository;

    @Override
    public <T> T getSurveyUnitsToReview(String userId, String search, Pageable pageable, SurveyUnitToReviewPresenter<T> presenter) {
        log.info("Retrieving survey units to review for user: {}", userId);

        // Get all survey units in TBR state for the user using existing service
        Set<SurveyUnitCampaignDto> tbrSurveyUnits = surveyUnitService.getSurveyUnitByCampaign(userId, null, StateType.TBR);

        if (tbrSurveyUnits.isEmpty()) {
            log.warn("No survey units to review found for user: {}", userId);
            Page<SurveyUnitToReview> emptyPage = Page.empty(pageable);
            return presenter.present(emptyPage);
        }

        // Convert to SurveyUnitToReview read model
        List<SurveyUnitToReview> surveyUnitsToReview = tbrSurveyUnits.stream()
                .map(this::toSurveyUnitToReview)
                .collect(Collectors.toList());

        // Apply pagination manually since we're working with an existing non-paginated result
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), surveyUnitsToReview.size());

        Page<SurveyUnitToReview> page = new PageImpl<>(
                surveyUnitsToReview.subList(start, end),
                pageable,
                surveyUnitsToReview.size()
        );

        log.info("Found {} survey units to review for user: {}", surveyUnitsToReview.size(), userId);
        return presenter.present(page);
    }

    /**
     * Converts a SurveyUnitCampaignDto to a SurveyUnitToReview read model.
     *
     * @param dto the source DTO
     * @return the converted read model
     */
    private SurveyUnitToReview toSurveyUnitToReview(SurveyUnitCampaignDto dto) {
        return new SurveyUnitToReview(
                dto.getId(),
                dto.getContactOutcome() != null ? dto.getContactOutcome().toString() : "",
                dto.getInterviewer() != null ? dto.getInterviewer().getId() : "",
                dto.getInterviewer() != null ?
                    (dto.getInterviewer().getFirstName() + " " + dto.getInterviewer().getLastName()).trim() : "",
                dto.getViewed() != null ? dto.getViewed() : false,
                dto.getComments() != null && !dto.getComments().isEmpty() ?
                    dto.getComments().get(0).getValue() : ""
        );
    }
}