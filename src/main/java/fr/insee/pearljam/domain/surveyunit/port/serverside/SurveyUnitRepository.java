package fr.insee.pearljam.domain.surveyunit.port.serverside;

import fr.insee.pearljam.api.repository.projection.ClosableSurveyUnitCandidateProjection;
import fr.insee.pearljam.api.repository.projection.ClosableSurveyUnitProjection;
import fr.insee.pearljam.api.repository.projection.SurveyUnitCampaignProjection;
import fr.insee.pearljam.domain.surveyunit.model.SurveyUnit;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SurveyUnitRepository {
    List<String> findIdsByInterviewerIdWithinVisibilityScope(String interviewerId, Long now, List<String> visibleTypes);

    Integer findCountUeINATBRByInterviewerIdAndCampaignId(String idInterviewer, String idCampaign, String idSurveyUnit);

    Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String surveyUnitId, String userId);

    List<String> findAllIds();

    List<ClosableSurveyUnitCandidateProjection> findClosableCandidates(long date, List<String> lstOuIds);

    Set<SurveyUnitCampaignProjection> findByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId);

    Set<SurveyUnitCampaignProjection> findByCampaignIdAndStateAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId, String state);

    Set<SurveyUnitCampaignProjection> findFinalizedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId);

    Set<SurveyUnitCampaignProjection> findClosedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId);

    List<SurveyUnit> findByInterviewerIdIgnoreCase(String id);

    List<Object[]> getCampaignStats(String campaignId, List<String> organizationalUnitIds);

    List<SurveyUnit> findByOrganizationUnitIdIn(List<String> lstOuId);

    List<SurveyUnit> findByIdInOrganizationalUnit(String id, List<String> organizationalUnitIds);

    Collection<SurveyUnit> findByCampaignId(String id);

    List<String> findAllIdsByCampaignId(String campaignId);

    List<String> findAllIdsByInterviewerId(String interviewerId);

    void setInterviewer(List<String> surveyUnitIds, String interviewerId);

    List<ClosableSurveyUnitProjection> findClosableSurveyUnits(Set<String> ids);

    Optional<SurveyUnit> findById(String surveyUnitId);

    SurveyUnit save(SurveyUnit surveyUnit);

    List<SurveyUnit> saveAll(List<SurveyUnit> surveyUnits);

    List<SurveyUnit> findAllById(Iterable<String> ids);

    void deleteById(String surveyUnitId);
}
