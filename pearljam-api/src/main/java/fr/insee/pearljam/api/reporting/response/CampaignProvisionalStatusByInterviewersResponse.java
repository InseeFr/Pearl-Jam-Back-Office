package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignProgressByInterviewers")
public record CampaignProvisionalStatusByInterviewersResponse(
        List<Interviewer> interviewers,
        OrganizationUnitSite site
) {
    @Schema(name = "CampaignProgressByInterviewersInterviewer")
    public record Interviewer(
            String InterviewerId,
            String interviewerLabel,
            SurveyUnitsResponse[] surveyUnits
    ) {

        @Schema(name = "SurveyUnitsStateCountsForInterviewer")
        public record SurveyUnitsResponse (
                Long allocated,
                ClosingCauseResponse closingCauses
        ) {
            @Schema(name = "ClosingCauseForSurveyUnit")
            public record ClosingCauseResponse (
                    Long interviewerAbsence,
                    Long notProcessedByInterviewer,
                    Long exceptionalReason,
                    Long rightOfWithdrawal,
                    Long total
            ) {}

        }

    }
    @Schema(name = "OrganizationUnitSite")
    public record OrganizationUnitSite(
            String label,
            SurveyUnitsSiteResponse surveyUnits
    ) {
        @Schema(name = "SurveyUnitsStateCountsForSite")
        public record SurveyUnitsSiteResponse(
                Long allocated,
                ClosingCauseSiteResponse closingCauses
        ) {
            @Schema(name = "ClosingCauseForSiteSurveyUnits")
            public record ClosingCauseSiteResponse(
                    Long interviewerAbsence,
                    Long notProcessedByInterviewer,
                    Long exceptionalReason,
                    Long rightOfWithdrawal,
                    Long total
            ) {
            }
        }
    }
}
