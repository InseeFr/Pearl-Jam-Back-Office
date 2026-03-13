package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.domain.campaign.model.Referent;

import java.util.List;

/**
 * Serverside port for referent persistence operations.
 */
public interface ReferentRepository {

    List<Referent> findByCampaignId(String campaignId);
}
