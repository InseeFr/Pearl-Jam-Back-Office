package fr.insee.pearljam.infrastructure.campaign.jpa;

import java.util.List;

import fr.insee.pearljam.infrastructure.campaign.entity.ReferentDB;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for ReferentDB entity.
 */
public interface ReferentJpaRepository extends JpaRepository<ReferentDB, Long> {

    List<ReferentDB> findByCampaignId(String id);
}
