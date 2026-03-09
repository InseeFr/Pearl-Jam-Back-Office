package fr.insee.pearljam.domain.campaign.port.out;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.ReferentDB;

import java.util.List;

public interface ReferentRepository {

    List<ReferentDB> findByCampaignId(String id);
}
