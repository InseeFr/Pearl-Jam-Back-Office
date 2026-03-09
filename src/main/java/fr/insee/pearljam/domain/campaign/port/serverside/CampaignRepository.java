package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.api.campaign.dto.CampaignDto;
import fr.insee.pearljam.api.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.api.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.domain.campaign.model.Campaign;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Optional<Campaign> findById(String id);

    Optional<Campaign> findByIdIgnoreCase(String id);

    boolean existsById(String id);

    List<String> findAllCampaignIdsByOuIds(List<String> ouIds);

    List<CampaignDto> findByUserAndManagementVisibility(List<String> ouIds, String userId, Long date);

    List<CampaignPreferenceDto> findByOuIdWithPreference(List<String> ouIds, String userId, Long date);

    CampaignDto findDtoById(String id);

    CampaignDto findDtoBySurveyUnitId(String id);

    List<CampaignDto> findAllDto();

    List<CampaignDto> findAllDtoByOuIds(List<String> ouIds);

    List<Campaign> findAll();

    List<Campaign> findAllById(Iterable<String> ids);

    Campaign save(Campaign campaign);

    void delete(Campaign campaign);

    List<String> findAllOrganistionUnitIdByCampaignId(String campaignId);

    List<VerifyNameResponseDto> findMatchingCampaigns(String text, List<String> ouIds, Long date, Pageable pageable);
}
