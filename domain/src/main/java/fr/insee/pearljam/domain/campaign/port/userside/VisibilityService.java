package fr.insee.pearljam.domain.campaign.port.userside;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface VisibilityService {

    /**
     *
     * @param campaignId campaign id
     * @return the visibilities for the campaign
     * @throws CampaignNotFoundException if campaign not found
     */
    List<Visibility> findVisibilities(String campaignId) throws CampaignNotFoundException;

    /**
     * update dates for a visibility
     * @param visibilityToUpdate visibility to update
     * @throws VisibilityNotFoundException if visibility not found
     * @throws VisibilityHasInvalidDatesException if problems when updating
     */
    void updateVisibility(Visibility visibilityToUpdate) throws VisibilityNotFoundException, VisibilityHasInvalidDatesException;

    /**
     * Return a global visibility for a campaign, taking into account all visibilities for the campaign
     * and define the period the campaign is opened by checking all the visibilities of the organisational units for
     * this campaign
     * @param idCampaign campaign id
     * @param ouIds organisational unit ids
     * @return the campaign visibility
     */
    CampaignVisibility getCampaignVisibility(String idCampaign, List<String> ouIds);

    /**
     *
     * @param campaignId campaign id
     * @param organizationalUnitId ou id
     * @return the visibility for the campaign/ou
     */
    Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId);
}
