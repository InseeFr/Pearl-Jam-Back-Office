package fr.insee.pearljam.infrastructure.campaign.adapter;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.port.serverside.VisibilityRepository;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import fr.insee.pearljam.api.domain.VisibilityDB;
import fr.insee.pearljam.infrastructure.campaign.jpa.VisibilityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VisibilityDaoAdapter implements VisibilityRepository {
    private final VisibilityJpaRepository crudRepository;
    private final CampaignRepository campaignRepository;
    private final OrganizationUnitRepository organizationUnitRepository;

    @Override
    public CampaignVisibility findCampaignVisibility(String campaignId, List<String> ouIds) {
        return crudRepository.findVisibilityByCampaignId(campaignId, ouIds);
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
    public void create(Visibility visibilityToCreate) throws CampaignNotFoundException, OrganizationalUnitNotFoundException {
        Campaign campaign = campaignRepository.findById(visibilityToCreate.campaignId())
                .orElseThrow(CampaignNotFoundException::new);
        OrganizationUnit organizationUnit = organizationUnitRepository.findById(visibilityToCreate.organizationalUnitId())
                .orElseThrow(OrganizationalUnitNotFoundException::new);

        VisibilityDB visibilityDB = VisibilityDB.fromModel(visibilityToCreate, campaign, organizationUnit);
        crudRepository.save(visibilityDB);
    }

    @Override
    public void update(Visibility visibilityToUpdate) throws VisibilityNotFoundException {
        VisibilityDB visibilityDB = crudRepository.findVisibilityByCampaignIdAndOuId(
                visibilityToUpdate.campaignId(), visibilityToUpdate.organizationalUnitId())
                .orElseThrow(VisibilityNotFoundException::new);

        visibilityDB.updateDates(visibilityToUpdate);
        crudRepository.save(visibilityDB);
    }

    @Override
    public Visibility getVisibilityBySurveyUnitId(String surveyUnitId) {
        return VisibilityDB.toModel(crudRepository
                .getVisibilityBySurveyUnitId(surveyUnitId));
    }
}
