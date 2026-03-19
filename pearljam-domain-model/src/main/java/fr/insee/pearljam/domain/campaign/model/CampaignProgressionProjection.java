// CampaignProgressionReport.java
package fr.insee.pearljam.domain.campaign.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CampaignProgressionProjection {

    private final String campaignId;
    private final String campaignLabel;
    private final float progressRate;
    private final SurveyUnits surveyUnits;

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