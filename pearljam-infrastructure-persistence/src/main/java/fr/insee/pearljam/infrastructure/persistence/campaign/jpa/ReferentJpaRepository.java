package fr.insee.pearljam.infrastructure.persistence.campaign.jpa;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.ReferentDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferentJpaRepository extends JpaRepository<ReferentDB, Long> {

    List<ReferentDB> findByCampaignId(String id);
}
