package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.domain.surveyunit.model.count.ClosingCauseCount;

import java.util.List;
import java.util.Map;

public interface ClosingCauseRepository {
    Map<String, Long> getStateClosedByClosingCauseCount(String campaignId, String interviewerId, List<String> ouIds, Long date);

    Map<String, Long> getClosingCauseCountNotAttributed(String campaignId, List<String> ouIds, Long date);

    Map<String, Long> getClosingCauseCountSumByInterviewer(List<String> campaignIds, String interviewerId, List<String> ouIds, Long date);

    Map<String, Long> getClosingCauseCountByCampaignId(String campaignId, Long date);

    Map<String, Long> getClosingCauseCount(String campaignId, String interviewerId, List<String> ouIds, Long date);

    List<ClosingCauseCount> getStateClosedByClosingCauseCountByCampaigns(List<String> campaignIds, List<String> ouIds, Long date);

    List<ClosingCauseCount> getClosingCauseCountByCampaignAndOus(String campaignId, List<String> ouIds, Long dateToUse);

    void deleteBySurveyUnitId(String surveyUnitId);
}
