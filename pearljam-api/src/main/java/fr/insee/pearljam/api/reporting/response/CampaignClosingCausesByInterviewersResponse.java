package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignProgressByInterviewers")
public record CampaignClosingCausesByInterviewersResponse(
        List<Interviewer> interviewers,
        TotalInterviewers total
) {
    @Schema(name = "CampaignProgressByInterviewersInterviewer")
    public record Interviewer(
            String interviewerId,
            String interviewerLabel,
            SurveyUnitsResponse surveyUnits
    ) {

        @Schema(name = "SurveyUnitsStateCountsForInterviewer")
        public record SurveyUnitsResponse (
                Long allocated,
                ClosingCauseResponse closingCauses
        ) {
            @Schema(name = "ClosingCauseForInterviewerSurveyUnit")
            public record ClosingCauseResponse (
                    Long interviewerAbsence,
                    Long notProcessedByInterviewer,
                    Long exceptionalReason,
                    Long rightOfWithdrawal,
                    Long total
            ) {}

        }

    }
    @Schema(name = "TotalInterviewers")
    public record TotalInterviewers(
            Long allocated,
            TotalInterviewerClosingCauses closingCauses) {
            @Schema(name = "TotalInterviewerClosingCauses")
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
