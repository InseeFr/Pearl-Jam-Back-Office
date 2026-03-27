package fr.insee.pearljam.domain.reporting.readmodel;

public record CampaignWithVisibility(String id,
                                     String label,
                                     Long managementStartDate,
                                     Long interviewerStartDate,
                                     Long identificationPhaseStartDate,
                                     Long collectionStartDate,
                                     Long collectionEndDate,
                                     Long endDate) {
}