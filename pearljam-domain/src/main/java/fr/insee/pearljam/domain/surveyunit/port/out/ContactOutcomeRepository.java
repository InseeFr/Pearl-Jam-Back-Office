package fr.insee.pearljam.domain.surveyunit.port.out;

import java.util.List;
import java.util.Map;

public interface ContactOutcomeRepository {
    Map<String, Long> findContactOutcomeTypeByInterviewerAndCampaign(String campaignId, String interviewerId, List<String> ouIds, Long date);

    Map<String, Long> findContactOutcomeTypeNotAttributed(String campaignId, List<String> ouIds, Long date);

    Map<String, Long> getContactOutcomeTypeCountByCampaignId(String campaignId, Long date);

    Map<String, Long> getContactOutcomeTypeCountByCampaignAndOU(String campaignId, String organizationalUnitId, Long date);
}
