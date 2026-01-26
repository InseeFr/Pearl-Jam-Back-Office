package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.port.serverside.VisibilityRepository;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;

import java.util.*;

public class VisibilityFakeRepository implements VisibilityRepository {

    private final List<Visibility> visibilities = new ArrayList<>();
    public static final CampaignVisibility CAMPAIGN_VISIBILITY =
            new CampaignVisibility(1627845600000L, 1627932000000L,
                    1628018400000L, 1628104800000L,
                    1628191200000L, 1628277600000L);

    @Override
    public CampaignVisibility getCampaignVisibility(String idCampaign, List<String> ouIds) {
        return CAMPAIGN_VISIBILITY;
    }

    @Override
    public Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId) {
        return visibilities.stream()
                .filter(visibility -> visibility.campaignId().equals(campaignId))
                .filter((visibility -> visibility.organizationalUnitId().equals(organizationalUnitId)))
                .findFirst();
    }

    @Override
    public List<Visibility> findVisibilities(String campaignId) {
        return visibilities.stream()
                .filter(visibility -> visibility.campaignId().equals(campaignId))
                .toList();
    }

    @Override
    public Set<String> findDistinctOrganizationalUnitIdByCampaignId(String campaignId) {
        return Set.of();
    }

    @Override
    public void updateDates(Visibility visibilityToUpdate) throws VisibilityNotFoundException {
        String campaignId = visibilityToUpdate.campaignId();
        String organizationalUnitId = visibilityToUpdate.organizationalUnitId();

        Visibility visibilityToRemove = findVisibility(campaignId, organizationalUnitId)
                .orElseThrow(VisibilityNotFoundException::new);

        visibilities.remove(visibilityToRemove);
        visibilities.add(visibilityToUpdate);
    }

    @Override
    public Visibility getVisibilityBySurveyUnitId(String surveyUnitId) {
        return new Visibility("campaign1", "OU1",
                1627845600000L, 1627932000000L, 1628018400000L,
                1628104800000L, 1628191200000L, 1628277600000L,
                true, "mail", "tel");
    }

    // Additional methods for testing purposes

    public void save(Visibility visibility) {
        if(!visibilities.contains(visibility)) {
            visibilities.add(visibility);
        }
    }
}
