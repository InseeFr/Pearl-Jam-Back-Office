package fr.insee.pearljam.domain.campaign.port.in;

import fr.insee.pearljam.domain.campaign.service.model.Visibility;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.domain.campaign.service.exception.VisibilityNotFoundException;

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
     *
     * @param campaignId campaign id
     * @param organizationalUnitId ou id
     * @return the visibility for the campaign/ou
     */
    Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId);
}
