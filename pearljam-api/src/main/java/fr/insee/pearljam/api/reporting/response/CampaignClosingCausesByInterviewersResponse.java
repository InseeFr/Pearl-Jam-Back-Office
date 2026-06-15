package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignClosingCausesByInterviewers")
public record CampaignClosingCausesByInterviewersResponse(
        List<Interviewer> interviewers,
        TotalInterviewers total
) {
    @Schema(name = "CampaignClosingCausesByInterviewer")
    public record Interviewer(
            String interviewerId,
            String interviewerLabel,
            SurveyUnitsResponse surveyUnits
    ) {

        @Schema(name = "CampaignClosingCausesByInterviewersSurveyUnits")
        public record SurveyUnitsResponse (
                Long allocated,
                ClosingCauseResponse closingCauses
        ) {
            @Schema(name = "CampaignClosingCausesByInterviewerClosingCause")
            public record ClosingCauseResponse (
                    Long interviewerAbsence,
                    Long notProcessedByInterviewer,
                    Long exceptionalReason,
                    Long rightOfWithdrawal,
                    Long total
            ) {}

        }

    }
    @Schema(name = "CampaignClosingCausesByInterviewersTotalInterviewers")
    public record TotalInterviewers(
            Long allocated,
            TotalInterviewerClosingCauses closingCauses) {
            @Schema(name = "CampaignClosingCausesByInterviewersTotalInterviewer")
            public record TotalInterviewerClosingCauses(
                    Long interviewerAbsence,
                    Long notProcessedByInterviewer,
                    Long exceptionalReason,
                    Long rightOfWithdrawal,
                    Long total
            ) {
        }
    }
}
