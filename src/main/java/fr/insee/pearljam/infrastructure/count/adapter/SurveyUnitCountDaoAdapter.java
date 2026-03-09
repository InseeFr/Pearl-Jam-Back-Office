package fr.insee.pearljam.infrastructure.count.adapter;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Infrastructure adapter for SurveyUnitCountRepository.
 * Implements the server-side port by delegating to the API repository.
 */
@Repository
@RequiredArgsConstructor
public class SurveyUnitCountDaoAdapter implements SurveyUnitCountRepository {

    private final SurveyUnitJpaRepository surveyUnitRepository;

    @Override
    public List<SurveyUnitDB> findByCampaignId(String campaignId) {
        return new ArrayList<>(surveyUnitRepository.findByCampaignId(campaignId));
    }

    @Override
    public List<SurveyUnitDB> findByCampaignIdAndStateAndOrganizationUnitIdIn(String campaignId, List<String> organizationUnitIds, String state) {
        return surveyUnitRepository.findByCampaignIdAndStateAndOrganizationUnitIdIn(campaignId, organizationUnitIds, state)
                .stream()
                .map(projection -> {
                    SurveyUnitDB su = new SurveyUnitDB();
                    su.setId(projection.getId());
                    su.setDisplayName(projection.getDisplayName());
                    return su;
                })
                .toList();
    }

    @Override
    public int countUnallocatedSurveyUnitsByCampaignId(String campaignId, List<String> organizationUnitIds) {
        return surveyUnitRepository.countUnallocatedSurveyUnitsByCampaignIdAndOrganizationUnitIdIn(campaignId, organizationUnitIds);
    }
}
