package fr.insee.pearljam.infrastructure.count.adapter;

import fr.insee.pearljam.domain.surveyunit.model.SurveyUnit;
import fr.insee.pearljam.infrastructure.surveyunit.jpa.SurveyUnitJpaRepository;
import fr.insee.pearljam.domain.count.port.serverside.SurveyUnitCountRepository;
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
    public List<SurveyUnit> findByCampaignId(String campaignId) {
        return new ArrayList<>(surveyUnitRepository.findByCampaignId(campaignId));
    }

    @Override
    public List<SurveyUnit> findByCampaignIdAndStateAndOrganizationUnitIdIn(String campaignId, List<String> organizationUnitIds, String state) {
        return surveyUnitRepository.findByCampaignIdAndStateAndOrganizationUnitIdIn(campaignId, organizationUnitIds, state)
                .stream()
                .map(projection -> {
                    SurveyUnit su = new SurveyUnit();
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
