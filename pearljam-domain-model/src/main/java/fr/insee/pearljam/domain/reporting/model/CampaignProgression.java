package fr.insee.pearljam.domain.reporting.model;

public record CampaignProgression(String campaignId, String campaignLabel, float progressRate,
                                  CampaignProgressionSurveyUnits surveyUnits) {

    public record CampaignProgressionSurveyUnits(
            Long allocated,
            Long notStarted,
            Long inProgress,
            Long pendingTransmission,
            Long toReview,
            Long validated,
            Long preparingContact,
            Long atLeastOneContact,
            Long appoLongmentScheduled,
            Long started,
            Long noticeLetter,
            Long reminderLetter
    ) {
    }
}