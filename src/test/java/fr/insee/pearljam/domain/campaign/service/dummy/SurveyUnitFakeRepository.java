package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.repository.projection.ClosableSurveyUnitCandidateProjection;
import fr.insee.pearljam.api.repository.projection.ClosableSurveyUnitProjection;
import fr.insee.pearljam.api.repository.projection.SurveyUnitCampaignProjection;
import fr.insee.pearljam.domain.surveyunit.model.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.port.serverside.SurveyUnitRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SurveyUnitFakeRepository implements SurveyUnitRepository {
    @Override
    public List<String> findIdsByInterviewerIdWithinVisibilityScope(String interviewerId, Long now, List<String> visibleTypes) {
        return List.of();
    }

    @Override
    public Integer findCountUeINATBRByInterviewerIdAndCampaignId(String idInterviewer, String idCampaign, String idSurveyUnit) {
        return 0;
    }

    @Override
    public Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String surveyUnitId, String userId) {
        return Optional.empty();
    }

    @Override
    public List<String> findAllIds() {
        return List.of();
    }

    @Override
    public List<ClosableSurveyUnitCandidateProjection> findClosableCandidates(long date, List<String> lstOuIds) {
        return List.of();
    }

    @Override
    public Set<SurveyUnitCampaignProjection> findByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return Set.of();
    }

    @Override
    public Set<SurveyUnitCampaignProjection> findByCampaignIdAndStateAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId, String state) {
        return Set.of();
    }

    @Override
    public Set<SurveyUnitCampaignProjection> findFinalizedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return Set.of();
    }

    @Override
    public Set<SurveyUnitCampaignProjection> findClosedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return Set.of();
    }

    @Override
    public List<SurveyUnit> findByInterviewerIdIgnoreCase(String id) {
        return List.of();
    }

    @Override
    public List<Object[]> getCampaignStats(String campaignId, List<String> organizationalUnitIds) {
        return List.of();
    }

    @Override
    public List<SurveyUnit> findByOrganizationUnitIdIn(List<String> lstOuId) {
        return List.of();
    }

    @Override
    public List<SurveyUnit> findByIdInOrganizationalUnit(String id, List<String> organizationalUnitIds) {
        return List.of();
    }

    @Override
    public Collection<SurveyUnit> findByCampaignId(String id) {
        return List.of();
    }

    @Override
    public List<String> findAllIdsByCampaignId(String campaignId) {
        return List.of();
    }

    @Override
    public List<String> findAllIdsByInterviewerId(String interviewerId) {
        return List.of();
    }

    @Override
    public void setInterviewer(List<String> surveyUnitIds, String interviewerId) {
        // not used at this moment
    }

    @Override
    public List<ClosableSurveyUnitProjection> findClosableSurveyUnits(Set<String> ids) {
        return List.of();
    }

    @Override
    public Optional<SurveyUnit> findById(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public SurveyUnit save(SurveyUnit surveyUnit) {
        return surveyUnit;
    }

    @Override
    public List<SurveyUnit> saveAll(List<SurveyUnit> surveyUnits) {
        return surveyUnits;
    }

    @Override
    public List<SurveyUnit> findAllById(Iterable<String> ids) {
        return List.of();
    }

    @Override
    public void deleteById(String surveyUnitId) {
        // not used at this moment
    }
}
