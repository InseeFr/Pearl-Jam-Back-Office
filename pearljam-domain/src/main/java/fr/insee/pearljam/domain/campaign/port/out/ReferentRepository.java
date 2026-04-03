package fr.insee.pearljam.domain.campaign.port.out;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.ReferentDB;

import java.util.List;

@Deprecated(forRemoval = true)
public interface ReferentRepository {

    List<ReferentDB> findByCampaignId(String id);
}
