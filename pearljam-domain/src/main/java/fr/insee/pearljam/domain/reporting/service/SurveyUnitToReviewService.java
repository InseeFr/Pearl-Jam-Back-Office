package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.SurveyUnitCampaignDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitToReviewPort;
import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitToReviewStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitToReview;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitService;
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
    private final UserService userService;
    private final CampaignVisibilityPort campaignVisibilityPort;
    private final DateService dateService;

    @Override
    public <T> T getSurveyUnitsToReview(String userId, String search, Pageable pageable, SurveyUnitToReviewStatsPresenter<T> presenter) {
        log.info("Retrieving survey units to review for user: {}", userId);

        long currentTimestamp = dateService.getCurrentTimestamp();

        List<String> ouIds = userService.getUserOUsModel(userId, true).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        List<CampaignVisibility> campaigns = campaignVisibilityPort
                .findCampaignsWithVisibilityByUserAndManagementVisibility(ouIds, userId, currentTimestamp);
        // Get all survey units in TBR state for the user's campaigns
        Set<SurveyUnitCampaignDto> tbrSurveyUnits = campaigns.stream()
                .flatMap(campaign -> surveyUnitService.getSurveyUnitByCampaign(campaign.id(), userId, StateType.TBR).stream())
                .collect(Collectors.toSet());

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
                    (dto.getInterviewer().getInterviewerFirstName() + " " + dto.getInterviewer().getInterviewerLastName()).trim() : "",
                dto.getViewed() != null ? dto.getViewed() : false,
                dto.getComments() != null && !dto.getComments().isEmpty() ?
                    dto.getComments().getFirst().value() : ""
        );
    }
}