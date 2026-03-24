package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.ReferentDB;
import fr.insee.pearljam.domain.campaign.port.out.ReferentRepository;

import java.util.List;

public class ReferentFakeRepository implements ReferentRepository {
    @Override
    public List<ReferentDB> findByCampaignId(String id) {
        return List.of();
    }
}
