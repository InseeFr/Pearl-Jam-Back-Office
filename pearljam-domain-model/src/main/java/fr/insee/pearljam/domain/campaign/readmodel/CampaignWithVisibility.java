package fr.insee.pearljam.domain.campaign.readmodel;

public record CampaignWithVisibility(
        String id,
        String label,
        Long managementStartDate,
        Long interviewerStartDate,
        Long identificationPhaseStartDate,
        Long collectionStartDate,
        Long collectionEndDate,
        Long endDate) {
}
