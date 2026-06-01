package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.SurveyUnitToReviewRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToReviewPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToReviewPresenter;
import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitToReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service implementation for retrieving survey units to review.
 * This service implements the {@link SurveyUnitToReviewPort} interface and provides
 * the business logic for getting paginated survey units that need review.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitToReviewService implements SurveyUnitToReviewPort {

    private final SurveyUnitToReviewRepositoryPort surveyUnitToReviewRepository;
    private final UserService userService;
    private final CampaignVisibilityPort campaignVisibilityPort;
    private final DateService dateService;

    @Override
    public <T> T getSurveyUnitsToReview(String userId, String campaignId, String search, Boolean viewed, Pageable pageable, SurveyUnitToReviewPresenter<T> presenter) {
        log.info("Retrieving survey units to review for user: {}", userId);

        long currentTimestamp = dateService.getCurrentTimestamp();

        List<String> ouIds = userService.getUserOUsModel(userId, true).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        List<CampaignVisibility> campaigns = campaignVisibilityPort
                .findCampaignsWithVisibilityByUserAndManagementVisibility(ouIds, userId, currentTimestamp);

        // Extract campaign IDs for the repository query
        List<String> campaignIds = Stream.ofNullable(campaignId)
                .filter(id -> !id.isBlank())
                .map(List::of)
                .findFirst()
                .orElseGet(() -> campaigns.stream()
                        .map(CampaignVisibility::id)
                        .toList());

        // Use native pagination via repository
        Page<SurveyUnitToReview> page = surveyUnitToReviewRepository.findSurveyUnitsToReview(
                campaignIds, ouIds, search, viewed,  pageable);

        if (page.isEmpty()) {
            log.warn("No survey units to review found for user: {} with search parameter: {}", userId, search);
        } else {
            log.info("Found {} survey units to review for user: {} with search parameter: {}", page.getTotalElements(), userId, search);
        }

        return presenter.present(page);
    }

}