package fr.insee.pearljam.infrastructure.campaign.jpa;

import fr.insee.pearljam.api.domain.Referent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferentJpaRepository extends JpaRepository<Referent, Long> {

    List<Referent> findByCampaignId(String id);
}