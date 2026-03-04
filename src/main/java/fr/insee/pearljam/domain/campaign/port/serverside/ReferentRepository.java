package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.api.domain.Referent;

import java.util.List;

public interface ReferentRepository {

    List<Referent> findByCampaignId(String id);
}
