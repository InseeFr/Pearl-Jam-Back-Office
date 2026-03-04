package fr.insee.pearljam.infrastructure.adapter.count;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.count.port.serverside.SurveyUnitCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Infrastructure adapter for SurveyUnitCountRepository.
 * Implements the server-side port by delegating to the API repository.
 */
@Repository
@RequiredArgsConstructor
public class SurveyUnitCountRepositoryImpl implements SurveyUnitCountRepository {

    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    public List<SurveyUnit> findByCampaignId(String campaignId) {
        return new java.util.ArrayList<>(surveyUnitRepository.findByCampaignId(campaignId));
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
