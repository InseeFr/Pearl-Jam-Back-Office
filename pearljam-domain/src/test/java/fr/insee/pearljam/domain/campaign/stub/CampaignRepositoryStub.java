package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CampaignRepositoryStub implements CampaignRepository {

    private HashMap<String, CampaignSummary> campaigns;

    public CampaignRepositoryStub(HashMap<String, CampaignSummary> campaigns) {
        this.campaigns = campaigns;
    }

    @Override
    public List<CampaignSummary> findAllManagedAndNotClosedCampaignsByOuIds(List<String> ouIds, Instant date) {
        return campaigns.values().stream().toList();
    }

    @Override public Optional<CampaignDB> findById(String id) { return Optional.empty(); }
    @Override public Optional<CampaignDB> findByIdIgnoreCase(String id) { return Optional.empty(); }
    @Override public boolean existsById(String id) { return campaigns.containsKey(id); }
    @Override public List<String> findAllCampaignIdsByOuIds(List<String> ouIds) { return List.of(); }
    @Override public List<CampaignDto> findByUserAndManagementVisibility(List<String> ouIds, String userId, Long date) { return List.of(); }
    @Override public List<String> findAllManagedAndNotClosedCampaignIdsByOuIds(List<String> ouIds, Long date) { return List.of(); }
    @Override public List<CampaignPreferenceDto> findByOuIdWithPreference(List<String> ouIds, String userId, Long date) { return List.of(); }
    @Override public CampaignDto findDtoById(String id) { return null; }
    @Override public CampaignDto findDtoBySurveyUnitId(String id) { return null; }
    @Override public List<CampaignDto> findAllDto() { return List.of(); }
    @Override public List<CampaignDto> findAllDtoByOuIds(List<String> ouIds) { return List.of(); }
    @Override public List<CampaignDB> findAll() { return List.of(); }
    @Override public List<CampaignDB> findAllById(Iterable<String> ids) { return List.of(); }
    @Override public CampaignDB save(CampaignDB campaign) { return campaign; }
    @Override public void delete(CampaignDB campaign) {
        // not used at this moment
    }
    @Override public List<String> findAllOrganistionUnitIdByCampaignId(String campaignId) { return List.of(); }
    @Override public List<VerifyNameResponseDto> findMatchingCampaigns(String text, List<String> ouIds, Long date, Pageable pageable) { return List.of(); }
}
