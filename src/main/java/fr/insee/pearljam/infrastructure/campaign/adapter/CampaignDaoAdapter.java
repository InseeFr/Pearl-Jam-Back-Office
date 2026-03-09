package fr.insee.pearljam.infrastructure.campaign.adapter;

import fr.insee.pearljam.api.campaign.dto.CampaignDto;
import fr.insee.pearljam.api.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.api.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.domain.campaign.model.Campaign;
import fr.insee.pearljam.domain.campaign.port.serverside.CampaignRepository;
import fr.insee.pearljam.infrastructure.campaign.jpa.CampaignJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CampaignDaoAdapter implements CampaignRepository {
    private final CampaignJpaRepository campaignJpaRepository;

    @Override
    public Optional<Campaign> findById(String id) {
        return campaignJpaRepository.findById(id);
    }

    @Override
    public Optional<Campaign> findByIdIgnoreCase(String id) {
        return campaignJpaRepository.findByIdIgnoreCase(id);
    }

    @Override
    public boolean existsById(String id) {
        return campaignJpaRepository.existsById(id);
    }

    @Override
    public List<String> findAllCampaignIdsByOuIds(List<String> ouIds) {
        return campaignJpaRepository.findAllCampaignIdsByOuIds(ouIds);
    }

    @Override
    public List<CampaignDto> findByUserAndManagementVisibility(List<String> ouIds, String userId, Long date) {
        return campaignJpaRepository.findByUserAndManagementVisibility(ouIds, userId, date);
    }

    @Override
    public List<CampaignPreferenceDto> findByOuIdWithPreference(List<String> ouIds, String userId, Long date) {
        return campaignJpaRepository.findByOuIdWithPreference(ouIds, userId, date);
    }

    @Override
    public CampaignDto findDtoById(String id) {
        return campaignJpaRepository.findDtoById(id);
    }

    @Override
    public CampaignDto findDtoBySurveyUnitId(String id) {
        return campaignJpaRepository.findDtoBySurveyUnitId(id);
    }

    @Override
    public List<CampaignDto> findAllDto() {
        return campaignJpaRepository.findAllDto();
    }

    @Override
    public List<CampaignDto> findAllDtoByOuIds(List<String> ouIds) {
        return campaignJpaRepository.findAllDtoByOuIds(ouIds);
    }

    @Override
    public List<Campaign> findAll() {
        return campaignJpaRepository.findAll();
    }

    @Override
    public List<Campaign> findAllById(Iterable<String> ids) {
        return campaignJpaRepository.findAllById(ids);
    }

    @Override
    public Campaign save(Campaign campaign) {
        return campaignJpaRepository.save(campaign);
    }

    @Override
    public void delete(Campaign campaign) {
        campaignJpaRepository.delete(campaign);
    }

    @Override
    public List<String> findAllOrganistionUnitIdByCampaignId(String campaignId) {
        return campaignJpaRepository.findAllOrganistionUnitIdByCampaignId(campaignId);
    }

    @Override
    public List<VerifyNameResponseDto> findMatchingCampaigns(String text, List<String> ouIds, Long date, Pageable pageable) {
        return campaignJpaRepository.findMatchingCampaigns(text, ouIds, date, pageable);
    }
}
