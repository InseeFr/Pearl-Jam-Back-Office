package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.domain.campaign.port.in.VisibilityService;
import fr.insee.pearljam.domain.campaign.service.model.Visibility;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.campaign.service.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.domain.campaign.service.exception.VisibilityNotFoundException;
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
}
