package fr.insee.pearljam.domain.count.port.userside;

import fr.insee.pearljam.domain.campaign.model.SurveyUnitCounts;

import java.util.List;

/**
 * User-side port for survey unit count operations.
 * Defines the interface for counting survey units in a campaign.
 */
public interface SurveyUnitCountService {

    /**
     * Retrieves survey unit counts for a campaign, filtered by organization units.
     *
     * @param campaignId the campaign ID
     * @param organizationUnitIds the organization unit IDs for filtering
     * @return SurveyUnitCounts containing abandoned, unallocated, and total counts
     */
    SurveyUnitCounts getSurveyUnitCounts(String campaignId, List<String> organizationUnitIds);
}