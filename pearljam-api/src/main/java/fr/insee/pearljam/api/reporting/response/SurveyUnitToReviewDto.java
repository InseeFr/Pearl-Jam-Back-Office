package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SurveyUnitToReviewDto")
public record SurveyUnitToReviewDto(
        @Schema(description = "Survey unit identifier", example = "SU-12345")
        String id,

        @Schema(description = "Campaign label", example = "Campaign 2024")
        String campaignLabel,

        @Schema(description = "Contact outcome", example = "CONTACTED")
        String contactOutcome,

        @Schema(description = "Interviewer name", example = "John Doe")
        String interviewerNameLabel,

        @Schema(description = "Whether the survey unit has been viewed", example = "false")
        Boolean viewed,

        @Schema(description = "URL to view the survey unit in read-only mode", example = "/read-only/SU-12345")
        String readOnlyUrl,

        @Schema(description = "Last comment on the survey unit", example = "Need to call back")
        String lastComment
) {
}