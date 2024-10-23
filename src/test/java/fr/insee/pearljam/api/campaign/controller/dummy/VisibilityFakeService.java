package fr.insee.pearljam.api.campaign.controller.dummy;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.port.userside.VisibilityService;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisibilityFakeService implements VisibilityService {

    @Setter
    private boolean shouldThrowCampaignNotFoundException = false;

    @Setter
    private boolean shouldThrowVisibilityMergingException = false;

    @Getter
    private Visibility visibilityUpdated = null;

    private List<Visibility> visibilities = new ArrayList<>();

    @Override
    public List<Visibility> findVisibilities(String campaignId) throws CampaignNotFoundException {
        if(shouldThrowCampaignNotFoundException) {
            throw new CampaignNotFoundException();
        }
        return visibilities;
    }

    @Override
    public void updateVisibility(Visibility visibilityToUpdate) throws VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        if(shouldThrowVisibilityMergingException) {
            throw new VisibilityHasInvalidDatesException();
        }

        visibilities.stream()
                .filter(visibility -> visibility.organizationalUnitId().equals(visibilityToUpdate.organizationalUnitId()))
                .filter(visibility -> visibility.campaignId().equals(visibilityToUpdate.campaignId()))
                .findFirst()
                .orElseThrow(VisibilityNotFoundException::new);
        visibilityUpdated = visibilityToUpdate;
    }

    @Override
    public CampaignVisibility getCampaignVisibility(String idCampaign, List<String> ouIds) {
        return null;
    }

    @Override
    public Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId) {
        return visibilities.stream()
                .filter(visibility -> visibility.campaignId().equals(campaignId) && visibility.organizationalUnitId().equals(organizationalUnitId))
                .findFirst();
    }

    public void save(Visibility visibility) {
        visibilities.add(visibility);
    }

    public void clearVisibilities() {
        visibilities.clear();
    }
}
