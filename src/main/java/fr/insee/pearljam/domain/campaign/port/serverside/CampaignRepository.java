package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.domain.campaign.model.Campaign;

import java.util.List;
import java.util.Optional;

/**
 * Serverside port for campaign persistence operations.
 * Implementations are in the infrastructure layer.
 */
public interface CampaignRepository {

    Optional<Campaign> findById(String campaignId);

    Optional<Campaign> findByIdIgnoreCase(String campaignId);

    List<Campaign> findAll();

    void save(Campaign campaign);

    void deleteById(String campaignId);

    boolean existsById(String campaignId);

    List<String> findAllCampaignIdsByOuIds(List<String> ouIds);

    List<String> findAllOrganizationUnitIdByCampaignId(String campaignId);
}
