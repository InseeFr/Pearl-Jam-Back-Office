package fr.insee.pearljam.infrastructure.campaign.jpa;

import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationInformationDB;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationInformationDBId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunicationInformationJpaRepository extends JpaRepository<CommunicationInformationDB, CommunicationInformationDBId> {

	Optional<CommunicationInformationDB> findByCampaignIdAndOrganizationUnitId(String campaignId, String organizationalUnitId);

	List<CommunicationInformationDB> findByCampaignId(String campaignId);
}
