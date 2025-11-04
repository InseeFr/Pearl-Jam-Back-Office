package fr.insee.pearljam.campaign.infrastructure.persistence.jpa.adapter;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository.VisibilityJpaRepository;
import fr.insee.pearljam.campaign.domain.model.CampaignVisibility;
import fr.insee.pearljam.campaign.domain.model.Visibility;
import fr.insee.pearljam.campaign.domain.port.serverside.VisibilityRepository;
import fr.insee.pearljam.campaign.domain.service.exception.VisibilityNotFoundException;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.VisibilityDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class VisibilityDaoAdapter implements VisibilityRepository {
    private final VisibilityJpaRepository crudRepository;

    @Override
    public CampaignVisibility getCampaignVisibility(String campaignId, List<String> ouIds) {
        return crudRepository
                .getCampaignVisibility(campaignId, ouIds);
    }

    @Override
    public Optional<Visibility> findVisibility(String campaignId, String organizationalUnitId) {
        return crudRepository
                .findVisibilityByCampaignIdAndOuId(campaignId, organizationalUnitId)
                .map(VisibilityDB::toModel);
    }

    @Override
    public List<Visibility> findVisibilities(String campaignId) {
        return VisibilityDB.toModel(
                crudRepository.findByCampaignId(campaignId)
        );
    }

    @Override
    public void updateDates(Visibility visibilityToUpdate) throws VisibilityNotFoundException {
        VisibilityDB visibilityDB = crudRepository.findVisibilityByCampaignIdAndOuId(
                        visibilityToUpdate.campaignId(), visibilityToUpdate.organizationalUnitId())
                .orElseThrow(VisibilityNotFoundException::new);

        visibilityDB.update(visibilityToUpdate);
        crudRepository.save(visibilityDB);
    }

    @Override
    public Visibility getVisibilityBySurveyUnitId(String surveyUnitId) {
        return VisibilityDB.toModel(crudRepository
                .getVisibilityBySurveyUnitId(surveyUnitId));
    }
}
