package fr.insee.pearljam.campaign.domain.service;

import fr.insee.pearljam.campaign.domain.model.CampaignVisibility;
import fr.insee.pearljam.campaign.domain.model.Visibility;
import fr.insee.pearljam.campaign.domain.port.serverside.VisibilityRepository;
import fr.insee.pearljam.campaign.domain.port.userside.VisibilityService;
import fr.insee.pearljam.campaign.domain.service.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.campaign.domain.service.exception.VisibilityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class VisibilityServiceImpl implements VisibilityService {

    private final VisibilityRepository visibilityRepository;

    @Override
    public Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId) {
        return visibilityRepository.findVisibility(campaignId, organizationalUnitId);
    }

    @Override
    public List<Visibility> findVisibilities(String campaignId) {
        return visibilityRepository.findVisibilities(campaignId);
    }

    @Override
    public void updateVisibility(Visibility visibilityToUpdate) throws VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        Visibility currentVisibility = visibilityRepository
                .findVisibility(visibilityToUpdate.campaignId(), visibilityToUpdate.organizationalUnitId())
                .orElseThrow(VisibilityNotFoundException::new);
        Visibility mergedVisibility = Visibility.merge(currentVisibility, visibilityToUpdate);
        visibilityRepository.updateDates(mergedVisibility);
    }

    @Override
    public CampaignVisibility getCampaignVisibility(String idCampaign, List<String> ouIds) {
        return visibilityRepository.getCampaignVisibility(idCampaign, ouIds);
    }
}
