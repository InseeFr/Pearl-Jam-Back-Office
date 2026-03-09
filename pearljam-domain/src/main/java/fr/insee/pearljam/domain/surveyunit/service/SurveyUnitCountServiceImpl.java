package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.model.SurveyUnitCounts;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitCountService;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of SurveyUnitCountService for counting survey units in a campaign.
 * Follows hexagonal architecture principles by encapsulating count logic in the domain layer.
 */
@Service
@RequiredArgsConstructor
public class SurveyUnitCountServiceImpl implements SurveyUnitCountService {

    private final SurveyUnitCountRepository surveyUnitCountRepository;

    /**
     * Retrieves survey unit counts for a campaign, filtered by organization units.
     *
     * @param campaignId the campaign ID
     * @param organizationUnitIds the organization unit IDs for filtering
     * @return SurveyUnitCounts containing abandoned, unallocated, and total counts
     */
    @Override
    public SurveyUnitCounts getSurveyUnitCounts(String campaignId, List<String> organizationUnitIds) {
        // Get total survey units for the campaign
        int total = surveyUnitCountRepository.findByCampaignId(campaignId).size();

        // Count abandoned survey units (state = ABN)
        int abandoned = countByCampaignIdAndState(campaignId, organizationUnitIds, "ABN");

        // Count unallocated survey units (interviewer_id is null)
        int unallocated = countByCampaignIdAndInterviewerNull(campaignId, organizationUnitIds);

        return new SurveyUnitCounts(abandoned, unallocated, total);
    }

    /**
     * Counts survey units by campaign ID and state, filtered by organization units.
     *
     * @param campaignId the campaign ID
     * @param organizationUnitIds the organization unit IDs for filtering
     * @param state the state type to count
     * @return the count of survey units
     */
    private int countByCampaignIdAndState(String campaignId, List<String> organizationUnitIds, String state) {
        return surveyUnitCountRepository.findByCampaignIdAndStateAndOrganizationUnitIdIn(campaignId, organizationUnitIds, state).size();
    }

    /**
     * Counts unallocated survey units (those without an interviewer) for a campaign.
     *
     * @param campaignId the campaign ID
     * @param organizationUnitIds the organization unit IDs for filtering
     * @return the count of unallocated survey units
     */
    private int countByCampaignIdAndInterviewerNull(String campaignId, List<String> organizationUnitIds) {
        return surveyUnitCountRepository.countUnallocatedSurveyUnitsByCampaignId(campaignId, organizationUnitIds);
    }
}