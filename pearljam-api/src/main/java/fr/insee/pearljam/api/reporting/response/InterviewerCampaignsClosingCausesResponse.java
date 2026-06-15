package fr.insee.pearljam.api.reporting.response;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


public record InterviewerCampaignsClosingCausesResponse(

        List<InterviewerCampaignSurveyUnits> interviewerCampaignSurveyUnits,
        InterviewerCampaignsTotalSurveyUnit interviewerCampaignsTotalSurveyUnit
) {

    @Schema(name = "InterviewerCampaignsClosingCauses")
    public record InterviewerCampaignSurveyUnits  (
            String campaignLabel,
            Long allocated,
            ClosingCauseResponse closingCauses
    ) {
        @Schema(name = "InterviewerCampaignsClosingCausesForSurveyUnit")
        public record ClosingCauseResponse (
                Long interviewerAbsence,
                Long notProcessedByInterviewer,
                Long exceptionalReason,
                Long rightOfWithdrawal,
                Long total
        ) {}
    }

    @Schema(name = "InterviewerCampaignsClosingCausesTotalSurveyUnit")
    public record InterviewerCampaignsTotalSurveyUnit(
            Long allocated,
            ClosingCauseResponse closingCauses
    ) {
        @Schema(name = "InterviewerCampaignsClosingCausesTotal")
        public record ClosingCauseResponse(
                Long interviewerAbsence,
                Long notProcessedByInterviewer,
                Long exceptionalReason,
                Long rightOfWithdrawal,
                Long total
        ) {
        }
    }
}
