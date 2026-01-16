package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface VisibilityRepository {

    /**
     *
     * @param idCampaign campaign id
     * @param ouIds organisational unit ids
     * @return the campaign visibility
     */
    CampaignVisibility getCampaignVisibility(String idCampaign, List<String> ouIds);

    /**
     *
     * @param campaignId campaign id
     * @param organizationalUnitId organizational unit id
     * @return the visibility for a campaign and an organizational unit
     */
    Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId);

    /**
     *
     * @param campaignId campaign id
     * @return the visibilities of a campaign
     */
    List<Visibility> findVisibilities(String campaignId);

    /**
     * Update dates for a visibility
     * @param visibilityToUpdate the visibility to update
     * @throws VisibilityNotFoundException if visibility not found
     */
    void updateDates(Visibility visibilityToUpdate) throws VisibilityNotFoundException;

    /**
     *
     * @param surveyUnitId survey unit id
     * @return the visibility for a survey unit
     */
    Visibility getVisibilityBySurveyUnitId(String surveyUnitId);
}
