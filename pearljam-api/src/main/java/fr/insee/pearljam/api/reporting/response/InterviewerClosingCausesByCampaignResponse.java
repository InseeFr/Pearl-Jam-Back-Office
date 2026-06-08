package fr.insee.pearljam.api.reporting.response;


import io.swagger.v3.oas.annotations.media.Schema;


public record InterviewerClosingCausesByCampaignResponse(
        String interviewerId,
        String interviewerLabel,
        String campaignLabel,
        ClosingCauseResponse closingCauses
) {
    @Schema(name = "InterviewerClosingCauseForSurveyUnit")
    public record ClosingCauseResponse (
            Long interviewerAbsence,
            Long notProcessedByInterviewer,
            Long exceptionalReason,
            Long rightOfWithdrawal,
            Long total
    ) {}
}
