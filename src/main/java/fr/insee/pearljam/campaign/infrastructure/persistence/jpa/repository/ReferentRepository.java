package fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository;

import java.util.List;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Referent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferentRepository extends JpaRepository<Referent, Long> {

    List<Referent> findByCampaignId(String id);
}
