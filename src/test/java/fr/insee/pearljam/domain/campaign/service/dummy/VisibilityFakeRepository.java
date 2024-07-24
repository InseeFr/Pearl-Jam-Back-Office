package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.port.serverside.VisibilityRepository;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;

import java.util.List;
import java.util.Optional;

public class VisibilityFakeRepository implements VisibilityRepository {
    @Override
    public CampaignVisibility getCampaignVisibility(String idCampaign, List<String> ouIds) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<Visibility> findVisibilities(String campaignId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void updateDates(Visibility visibilityToUpdate) throws VisibilityNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Visibility getVisibilityBySurveyUnitId(String surveyUnitId) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
