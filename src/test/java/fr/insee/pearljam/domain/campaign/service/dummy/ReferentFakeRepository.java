package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.campaign.model.Referent;
import fr.insee.pearljam.domain.campaign.port.serverside.ReferentRepository;

import java.util.List;

public class ReferentFakeRepository implements ReferentRepository {
    @Override
    public List<Referent> findByCampaignId(String id) {
        return List.of();
    }
}
