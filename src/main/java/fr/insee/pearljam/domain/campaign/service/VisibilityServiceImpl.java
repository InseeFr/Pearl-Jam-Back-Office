package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.port.serverside.VisibilityRepository;
import fr.insee.pearljam.domain.campaign.port.userside.VisibilityService;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class VisibilityServiceImpl implements VisibilityService {

    private final VisibilityRepository visibilityRepository;

    @Override
    public List<Visibility> findVisibilities(String campaignId) throws CampaignNotFoundException {
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
