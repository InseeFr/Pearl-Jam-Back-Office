package fr.insee.pearljam.infrastructure.persistence.closingcause.adapter;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.count.ClosingCauseCount;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.infrastructure.persistence.closingcause.jpa.ClosingCauseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ClosingCauseDaoAdapter implements ClosingCauseRepository {
    private final ClosingCauseJpaRepository closingCauseJpaRepository;

    @Override
    public Map<String, Long> getClosingCauseCountSumByInterviewer(List<String> campaignIds, String interviewerId, List<String> ouIds, Long date) {
        return closingCauseJpaRepository.getClosingCauseCountSumByInterviewer(campaignIds, interviewerId, ouIds, date);
    }

    @Override
    public List<ClosingCauseCount> getStateClosedByClosingCauseCountByCampaigns(List<String> campaignIds, List<String> ouIds, Long date) {
        return closingCauseJpaRepository.getStateClosedByClosingCauseCountByCampaigns(campaignIds, ouIds, date);
    }

    @Override
    public void deleteBySurveyUnitId(String surveyUnitId) {
        closingCauseJpaRepository.deleteBySurveyUnitId(surveyUnitId);
    }

    @Override
    public void addClosingCauseToSurveyUnits(List<String> surveyUnitIds, ClosingCauseType closingCause) {
        closingCauseJpaRepository.addClosingCauseToSurveyUnits(surveyUnitIds, closingCause.toString());
    }
    @Override
    public void updateExistingClosingCauseToSurveyUnits(List<String> surveyUnitIds, ClosingCauseType closingCause) {
        closingCauseJpaRepository.updateExistingClosingCauseToSurveyUnits(surveyUnitIds, closingCause.toString());
    }

    @Override
    public List<String>  findSurveyUnitIdsWithClosingCause(List<String> surveyUnitIds) {
        return closingCauseJpaRepository.findSurveyUnitIdsWithClosingCause(surveyUnitIds);
    }
}
