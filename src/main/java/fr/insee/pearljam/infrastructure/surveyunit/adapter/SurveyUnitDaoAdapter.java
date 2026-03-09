package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.api.repository.projection.ClosableSurveyUnitCandidateProjection;
import fr.insee.pearljam.api.repository.projection.ClosableSurveyUnitProjection;
import fr.insee.pearljam.api.repository.projection.SurveyUnitCampaignProjection;
import fr.insee.pearljam.domain.surveyunit.model.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.port.serverside.SurveyUnitRepository;
import fr.insee.pearljam.infrastructure.surveyunit.jpa.SurveyUnitJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SurveyUnitDaoAdapter implements SurveyUnitRepository {
    private final SurveyUnitJpaRepository surveyUnitJpaRepository;

    @Override
    public List<String> findIdsByInterviewerIdWithinVisibilityScope(String interviewerId, Long now, List<String> visibleTypes) {
        return surveyUnitJpaRepository.findIdsByInterviewerIdWithinVisibilityScope(interviewerId, now, visibleTypes);
    }

    @Override
    public Integer findCountUeINATBRByInterviewerIdAndCampaignId(String idInterviewer, String idCampaign, String idSurveyUnit) {
        return surveyUnitJpaRepository.findCountUeINATBRByInterviewerIdAndCampaignId(idInterviewer, idCampaign, idSurveyUnit);
    }

    @Override
    public Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String surveyUnitId, String userId) {
        return surveyUnitJpaRepository.findByIdAndInterviewerIdIgnoreCase(surveyUnitId, userId);
    }

    @Override
    public List<String> findAllIds() {
        return surveyUnitJpaRepository.findAllIds();
    }

    @Override
    public List<ClosableSurveyUnitCandidateProjection> findClosableCandidates(long date, List<String> lstOuIds) {
        return surveyUnitJpaRepository.findClosableCandidates(date, lstOuIds);
    }

    @Override
    public Set<SurveyUnitCampaignProjection> findByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return surveyUnitJpaRepository.findByCampaignIdAndOrganizationUnitIdIn(campaignId, lstOuId);
    }

    @Override
    public Set<SurveyUnitCampaignProjection> findByCampaignIdAndStateAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId, String state) {
        return surveyUnitJpaRepository.findByCampaignIdAndStateAndOrganizationUnitIdIn(campaignId, lstOuId, state);
    }

    @Override
    public Set<SurveyUnitCampaignProjection> findFinalizedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return surveyUnitJpaRepository.findFinalizedByCampaignIdAndOrganizationUnitIdIn(campaignId, lstOuId);
    }

    @Override
    public Set<SurveyUnitCampaignProjection> findClosedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId) {
        return surveyUnitJpaRepository.findClosedByCampaignIdAndOrganizationUnitIdIn(campaignId, lstOuId);
    }

    @Override
    public List<SurveyUnit> findByInterviewerIdIgnoreCase(String id) {
        return surveyUnitJpaRepository.findByInterviewerIdIgnoreCase(id);
    }

    @Override
    public List<Object[]> getCampaignStats(String campaignId, List<String> organizationalUnitIds) {
        return surveyUnitJpaRepository.getCampaignStats(campaignId, organizationalUnitIds);
    }

    @Override
    public List<SurveyUnit> findByOrganizationUnitIdIn(List<String> lstOuId) {
        return surveyUnitJpaRepository.findByOrganizationUnitIdIn(lstOuId);
    }

    @Override
    public List<SurveyUnit> findByIdInOrganizationalUnit(String id, List<String> organizationalUnitIds) {
        return surveyUnitJpaRepository.findByIdInOrganizationalUnit(id, organizationalUnitIds);
    }

    @Override
    public Collection<SurveyUnit> findByCampaignId(String id) {
        return surveyUnitJpaRepository.findByCampaignId(id);
    }

    @Override
    public List<String> findAllIdsByCampaignId(String campaignId) {
        return surveyUnitJpaRepository.findAllIdsByCampaignId(campaignId);
    }

    @Override
    public List<String> findAllIdsByInterviewerId(String interviewerId) {
        return surveyUnitJpaRepository.findAllIdsByInterviewerId(interviewerId);
    }

    @Override
    public void setInterviewer(List<String> surveyUnitIds, String interviewerId) {
        surveyUnitJpaRepository.setInterviewer(surveyUnitIds, interviewerId);
    }

    @Override
    public List<ClosableSurveyUnitProjection> findClosableSurveyUnits(Set<String> ids) {
        return surveyUnitJpaRepository.findClosableSurveyUnits(ids);
    }

    @Override
    public Optional<SurveyUnit> findById(String surveyUnitId) {
        return surveyUnitJpaRepository.findById(surveyUnitId);
    }

    @Override
    public SurveyUnit save(SurveyUnit surveyUnit) {
        return surveyUnitJpaRepository.save(surveyUnit);
    }

    @Override
    public List<SurveyUnit> saveAll(List<SurveyUnit> surveyUnits) {
        return surveyUnitJpaRepository.saveAll(surveyUnits);
    }

    @Override
    public List<SurveyUnit> findAllById(Iterable<String> ids) {
        return surveyUnitJpaRepository.findAllById(ids);
    }

    @Override
    public void deleteById(String surveyUnitId) {
        surveyUnitJpaRepository.deleteById(surveyUnitId);
    }
}
