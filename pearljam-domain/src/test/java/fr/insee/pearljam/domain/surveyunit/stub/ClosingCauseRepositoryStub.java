package fr.insee.pearljam.domain.surveyunit.stub;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.count.ClosingCauseCount;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ClosingCauseRepositoryStub  implements ClosingCauseRepository {
    private final Map<String, ClosingCauseType> closingCauses = new HashMap<>();
    int addedClosingCausesCount;

    @Override
    public boolean existsClosingCauseFromSurveyUnitId(String surveyUnitId) {
        return closingCauses.containsKey(surveyUnitId);
    }

    @Override
    public Map<String, Long> getStateClosedByClosingCauseCount(String campaignId, String interviewerId, List<String> ouIds, Long date) {
        return Map.of();
    }

    @Override
    public Map<String, Long> getClosingCauseCountNotAttributed(String campaignId, List<String> ouIds, Long date) {
        return Map.of();
    }

    @Override
    public Map<String, Long> getClosingCauseCountSumByInterviewer(List<String> campaignIds, String interviewerId, List<String> ouIds, Long date) {
        return Map.of();
    }

    @Override
    public Map<String, Long> getClosingCauseCountByCampaignId(String campaignId, Long date) {
        return Map.of();
    }

    @Override
    public Map<String, Long> getClosingCauseCount(String campaignId, String interviewerId, List<String> ouIds, Long date) {
        return Map.of();
    }

    @Override
    public List<ClosingCauseCount> getStateClosedByClosingCauseCountByCampaigns(List<String> campaignIds, List<String> ouIds, Long date) {
        return List.of();
    }

    @Override
    public List<ClosingCauseCount> getClosingCauseCountByCampaignAndOus(String campaignId, List<String> ouIds, Long dateToUse) {
        return List.of();
    }

    @Override
    public void deleteBySurveyUnitId(String surveyUnitId) {
        //not used
    }

    @Override
    public void addClosingCauseToSurveyUnit(String surveyUnitId, ClosingCauseType type) {
        addedClosingCausesCount++;
        closingCauses.put(surveyUnitId, type);
    }

    public void addInitialClosingCauseToSurveyUnit(String surveyUnitId, ClosingCauseType type) {
        closingCauses.put(surveyUnitId, type);
    }

    public ClosingCauseType getClosingCauseType(String surveyUnitId) {
        return closingCauses.get(surveyUnitId);
    }
}
