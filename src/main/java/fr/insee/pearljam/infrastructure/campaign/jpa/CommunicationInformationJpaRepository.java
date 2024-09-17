package fr.insee.pearljam.infrastructure.campaign.jpa;

import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationInformationDB;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationInformationDBId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommunicationInformationJpaRepository extends JpaRepository<CommunicationInformationDB, CommunicationInformationDBId> {

	@Query(value = """
		SELECT ci FROM CommunicationInformationDB ci
		WHERE ci.campaign.id=?1
		AND ci.organizationUnit.id=?2""")
	Optional<CommunicationInformationDB> findCommunicationInformation(String campaignId, String organizationalUnitId);

	List<CommunicationInformationDB> findByCampaignId(String campaignId);
}
