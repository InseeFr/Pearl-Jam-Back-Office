package fr.insee.pearljam.domain.campaign.port.out;

import fr.insee.pearljam.contracts.campaign.dto.CampaignProjection;
import fr.insee.pearljam.contracts.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Optional<CampaignDB> findById(String id);

    Optional<CampaignDB> findByIdIgnoreCase(String id);

    boolean existsById(String id);

    List<String> findAllCampaignIdsByOuIds(List<String> ouIds);

    List<CampaignProjection> findByUserAndManagementVisibility(List<String> ouIds, String userId, Long date);

    List<String> findAllManagedAndNotClosedCampaignIdsByOuIds(List<String> ouIds, Long date);

    List<CampaignPreferenceDto> findByOuIdWithPreference(List<String> ouIds, String userId, Long date);

    CampaignProjection findDtoById(String id);

    CampaignProjection findDtoBySurveyUnitId(String id);

    List<CampaignProjection> findAllDto();

    List<CampaignProjection> findAllDtoByOuIds(List<String> ouIds);

    List<CampaignDB> findAll();

    List<CampaignDB> findAllById(Iterable<String> ids);

    CampaignDB save(CampaignDB campaign);

    void delete(CampaignDB campaign);

    List<String> findAllOrganistionUnitIdByCampaignId(String campaignId);

    List<VerifyNameResponseDto> findMatchingCampaigns(String text, List<String> ouIds, Long date, Pageable pageable);
}
