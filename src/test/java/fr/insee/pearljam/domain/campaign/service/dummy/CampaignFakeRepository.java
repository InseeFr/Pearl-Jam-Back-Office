package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.campaign.dto.CampaignDto;
import fr.insee.pearljam.api.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.api.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.domain.campaign.model.Campaign;
import fr.insee.pearljam.domain.campaign.port.serverside.CampaignRepository;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CampaignFakeRepository implements CampaignRepository {

    private final List<Campaign> campaigns = new ArrayList<>();

    @Getter
    private Campaign savedCampaign;

    @Override
    public Optional<Campaign> findById(String id) {
        return campaigns.stream().filter(campaign -> campaign.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public Optional<Campaign> findByIdIgnoreCase(String id) {
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
    public List<Campaign> findAll() {
        return new ArrayList<>(campaigns);
    }

    @Override
    public List<Campaign> findAllById(Iterable<String> ids) {
        List<Campaign> result = new ArrayList<>();
        for (String id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public Campaign save(Campaign campaign) {
        savedCampaign = campaign;
        findById(campaign.getId()).ifPresent(campaigns::remove);
        campaigns.add(campaign);
        return campaign;
    }

    @Override
    public void delete(Campaign campaign) {
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

    public void addCampaign(Campaign campaign) {
        campaigns.add(campaign);
    }
}
