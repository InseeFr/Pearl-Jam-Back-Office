package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;import fr.insee.pearljam.contracts.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CampaignFakeRepository implements CampaignRepository {

    private final List<CampaignDB> campaigns = new ArrayList<>();

    @Getter
    private CampaignDB savedCampaign;

    @Override
    public Optional<CampaignDB> findById(String id) {
        return campaigns.stream().filter(campaign -> campaign.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public Optional<CampaignDB> findByIdIgnoreCase(String id) {
        return findById(id);
    }

    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    @Override
    public List<String> findAllCampaignIdsByOuIds(List<String> ouIds) {
        return List.of();
    }

    @Override
    public List<CampaignDto> findByUserAndManagementVisibility(List<String> ouIds, String userId, Long date) {
        return List.of();
    }

    @Override
    public List<CampaignPreferenceDto> findByOuIdWithPreference(List<String> ouIds, String userId, Long date) {
        return List.of();
    }

    @Override
    public CampaignDto findDtoById(String id) {
        return null;
    }

    @Override
    public CampaignDto findDtoBySurveyUnitId(String id) {
        return null;
    }

    @Override
    public List<CampaignDto> findAllDto() {
        return List.of();
    }

    @Override
    public List<CampaignDto> findAllDtoByOuIds(List<String> ouIds) {
        return List.of();
    }

    @Override
    public List<CampaignDB> findAll() {
        return new ArrayList<>(campaigns);
    }

    @Override
    public List<CampaignDB> findAllById(Iterable<String> ids) {
        List<CampaignDB> result = new ArrayList<>();
        for (String id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public CampaignDB save(CampaignDB campaign) {
        savedCampaign = campaign;
        findById(campaign.getId()).ifPresent(campaigns::remove);
        campaigns.add(campaign);
        return campaign;
    }

    @Override
    public void delete(CampaignDB campaign) {
        campaigns.remove(campaign);
    }

    @Override
    public List<String> findAllOrganistionUnitIdByCampaignId(String campaignId) {
        return List.of();
    }

    @Override
    public List<VerifyNameResponseDto> findMatchingCampaigns(String text, List<String> ouIds, Long date, Pageable pageable) {
        return List.of();
    }

    public void addCampaign(CampaignDB campaign) {
        campaigns.add(campaign);
    }
}
