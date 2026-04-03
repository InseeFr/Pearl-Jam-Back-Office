package fr.insee.pearljam.infrastructure.persistence.campaign.adapter;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.CampaignVisibilityPeriod;
import fr.insee.pearljam.domain.campaign.service.model.Visibility;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.campaign.service.exception.VisibilityNotFoundException;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.VisibilityDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.VisibilityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Deprecated(forRemoval = true)
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

    @Override
    public List<CampaignVisibilityPeriod> findCampaignsBySurveyUnitIds(List<String> surveyUnitIds) {
        return crudRepository.findCampaignsBySurveyUnitIds(surveyUnitIds).stream()
                .map(campaignVisibilityPeriodProjection -> new CampaignVisibilityPeriod(
                        campaignVisibilityPeriodProjection.getCampaignId(),
                        campaignVisibilityPeriodProjection.getCampaignLabel(),
                        campaignVisibilityPeriodProjection.getManagementStartDate(),
                        campaignVisibilityPeriodProjection.getEndDate()))
                .toList();
    }
}
