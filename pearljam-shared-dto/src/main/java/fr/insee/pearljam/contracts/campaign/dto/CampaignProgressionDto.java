package fr.insee.pearljam.contracts.campaign.dto;

public record CampaignProgressionDto(
        String campaignId,
        String campaignLabel,
        float progressRate,
        SurveyUnits surveyUnits
) {

    public record SurveyUnits(
            long allocated,
            long notStarted,
            long inProgress,
            long pendingTransmission,
            long toReview,
            long validated,
            long preparingContact,
            long atLeastOneContact,
            long appolongmentScheduled,
            long started,
            long noticeLetter,
            long reminderLetter
    ) {}



}