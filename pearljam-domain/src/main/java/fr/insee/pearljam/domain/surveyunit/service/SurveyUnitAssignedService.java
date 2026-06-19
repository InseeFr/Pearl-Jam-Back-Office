package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitAssignedRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import fr.insee.pearljam.domain.surveyunit.service.exception.CampaignNotVisibleForUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

/**
 * Service implementation for retrieving survey units assigned.
 * This service implements the {@link SurveyUnitAssignedPort} interface and provides
 * the business logic for getting paginated survey units that need review.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitAssignedService implements SurveyUnitAssignedPort {

    private final SurveyUnitAssignedRepositoryPort surveyUnitAssignedRepository;
    private final UserService userService;
    private final CampaignVisibilityPort campaignVisibilityPort;
    private final DateService dateService;

    @Override
    public <T> T getSurveyUnitsAssigned(String userId, String campaignId, String search, Pageable pageable, SurveyUnitAssignedPresenter<T> presenter) {
        log.info("Retrieving survey units assigned for user: {}", userId);


        List<String> campaignIds = getCampaignIdsForUser(userId, campaignId);

        Page<SurveyUnitAssigned> page = surveyUnitAssignedRepository.findSurveyUnitsAssigned(
            campaignIds, search, pageable);

        if (page.isEmpty()) {
            log.warn("No survey units assigned found for user: {} with search parameter: {}", userId, search);
        } else {
            log.info("Found {} survey units assigned for user: {} with search parameter: {}", page.getTotalElements(), userId, search);
        }

        return presenter.present(page);
    }

    private List<String> getCampaignIdsForUser(String userId, String campaignId) {

        long currentTimestamp = dateService.getCurrentTimestamp();

        List<String> ouIds = userService.getUserOUsModel(userId, true).stream()
            .map(OrganizationUnitSummary::getId)
            .toList();

        List<String> visibleCampaignIds =
            campaignVisibilityPort
                .findPreferredCampaignsWithVisibilityByUserAndManagementVisibility(
                    ouIds, userId, currentTimestamp
                )
                .stream()
                .map(CampaignVisibility::id)
                .toList();

        Set<String> visibleSet = new HashSet<>(visibleCampaignIds);

        if (hasText(campaignId)) {
            if (!visibleSet.contains(campaignId)) {
                throw new CampaignNotVisibleForUserException(campaignId, userId);
            }
            return List.of(campaignId);
        }
        return visibleCampaignIds;
    }

}