// CampaignProgressionReport.java
package fr.insee.pearljam.domain.reporting.projection;

public record CampaignProgressionProjection(String campaignId, String campaignLabel, float progressRate,
                                            SurveyUnits surveyUnits) {

    public record SurveyUnits(
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
    ) {}
}