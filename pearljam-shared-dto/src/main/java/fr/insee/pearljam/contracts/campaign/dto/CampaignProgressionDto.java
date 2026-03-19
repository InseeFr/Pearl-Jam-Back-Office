package fr.insee.pearljam.contracts.campaign.dto;

import java.util.List;

public record CampaignProgressionDto(
        String campaignId,
        String campaignLabel,
        float progressRate,
        SurveyUnits surveyUnits
) {

    public record SurveyUnits(
            int allocated,
            int notStarted,
            int inProgress,
            int pendingTransmission,
            int toReview,
            int validated,
            int preparingContact,
            int atLeastOneContact,
            int appointmentScheduled,
            int started,
            int noticeLetter,
            int reminderLetter
    ) {}



}