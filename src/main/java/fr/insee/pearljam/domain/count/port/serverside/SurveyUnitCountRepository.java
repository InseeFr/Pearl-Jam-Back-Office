package fr.insee.pearljam.domain.count.port.serverside;

import fr.insee.pearljam.api.domain.SurveyUnit;

import java.util.List;

/**
 * Server-side port for survey unit repository operations.
 * Defines the interface for accessing survey unit data.
 */
public interface SurveyUnitCountRepository {

    /**
     * Finds all survey units by campaign ID.
     *
     * @param campaignId the campaign ID
     * @return list of survey units
     */
    List<SurveyUnit> findByCampaignId(String campaignId);

    /**
     * Finds survey units by campaign ID, state, and organization unit IDs.
     *
     * @param campaignId the campaign ID
     * @param organizationUnitIds the organization unit IDs for filtering
     * @param state the state type to filter
     * @return list of survey units
     */
    List<SurveyUnit> findByCampaignIdAndStateAndOrganizationUnitIdIn(String campaignId, List<String> organizationUnitIds, String state);

    /**
     * Counts unallocated survey units (those without an interviewer) by campaign ID and organization units.
     *
     * @param campaignId the campaign ID
     * @param organizationUnitIds the organization unit IDs for filtering
     * @return the count of unallocated survey units
     */
    int countUnallocatedSurveyUnitsByCampaignId(String campaignId, List<String> organizationUnitIds);
}