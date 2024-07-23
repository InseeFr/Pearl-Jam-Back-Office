package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface VisibilityRepository {

    CampaignVisibility findCampaignVisibility(String idCampaign, List<String> lstOuId);

    Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId);

    List<Visibility> findVisibilities(String campaignId);

    void create(Visibility visibilityToCreate) throws CampaignNotFoundException, OrganizationalUnitNotFoundException;

    void update(Visibility visibilityToUpdate) throws VisibilityNotFoundException;

    Visibility getVisibilityBySurveyUnitId(String surveyUnitId);
}
