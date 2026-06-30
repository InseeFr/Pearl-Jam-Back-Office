package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.SurveyUnitCampaignView;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;

import java.util.*;

public class SurveyUnitRepositoryStub implements SurveyUnitRepository {

    private final HashMap<String, SurveyUnitDB> surveyUnitDBs = new HashMap<>();

    @Override
    public List<String> findIdsByInterviewerIdWithinVisibilityScope(String interviewerId, Long now, List<String> visibleTypes) {
        return List.of();
    }

    @Override
    public Integer findCountUeINATBRByInterviewerIdAndCampaignId(String idInterviewer, String idCampaign, String idSurveyUnit) {
        return 0;
    }

    @Override
    public Optional<SurveyUnitDB> findByIdAndInterviewerIdIgnoreCase(String surveyUnitId, String userId) {
        return Optional.empty();
    }

    @Override
    public List<String> findAllIds() {
        return List.of();
    }

    @Override
    public List<ClosableSurveyUnitCandidateView> findClosableCandidates(long date, String campaignId, List<String> lstOuIds) {
        return List.of();
    }

    @Override
    public Set<SurveyUnitCampaignView> findByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return Set.of();
    }

    @Override
    public Set<SurveyUnitCampaignView> findByCampaignIdAndStateAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId, String state) {
        return Set.of();
    }

    @Override
    public Set<SurveyUnitCampaignView> findFinalizedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return Set.of();
    }

    @Override
    public Set<SurveyUnitCampaignView> findClosedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return Set.of();
    }

    @Override
    public List<SurveyUnitDB> findByInterviewerIdIgnoreCase(String id) {
        return List.of();
    }

    @Override
    public List<Object[]> getCampaignStats(String campaignId, List<String> organizationalUnitIds) {
        return List.of();
    }

    @Override
    public List<SurveyUnitDB> findByOrganizationUnitIdIn(List<String> lstOuId) {
        return List.of();
    }

    @Override
    public List<SurveyUnitDB> findByIdInOrganizationalUnit(String id, List<String> organizationalUnitIds) {
        return List.of();
    }

    @Override
    public Collection<SurveyUnitDB> findByCampaignId(String id) {
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
    public List<ClosableSurveyUnitView> findClosableSurveyUnits(Set<String> ids) {
        return List.of();
    }

    @Override
    public Optional<SurveyUnitDB> findById(String surveyUnitId) {
        return Optional.ofNullable(surveyUnitDBs.get(surveyUnitId));
    }

    @Override
    public SurveyUnitDB save(SurveyUnitDB surveyUnit) {
        surveyUnitDBs.put(surveyUnit.getId(), surveyUnit);
        return surveyUnit;
    }

    @Override
    public List<SurveyUnitDB> saveAll(List<SurveyUnitDB> surveyUnits) {
        surveyUnits.forEach(this::save);
        return surveyUnits;
    }

    @Override
    public List<SurveyUnitDB> findAllById(Iterable<String> ids) {
        return List.of();
    }

    @Override
    public void deleteById(String surveyUnitId) {
        surveyUnitDBs.remove(surveyUnitId);
    }

    @Override
    public List<String> findExistingIds(List<String> surveyUnitIds) {
        return surveyUnitIds.stream()
                .filter(surveyUnitDBs::containsKey)
                .toList();
    }
}