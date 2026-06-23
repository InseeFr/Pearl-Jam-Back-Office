package fr.insee.pearljam.api.surveyunit.response;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "SurveyUnitCompleted")
public record  SurveyUnitCompletedPageResponse (

        List<SurveyUnitCompletedResponse> content,

        @Schema(description = "Current page number", example = "0")
        int page,

        @Schema(description = "Number of items per page", example = "20")
        int size,

        @Schema(description = "Total number of items", example = "100")
        long totalElements,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages
) {
    @Schema(name = "SurveyUnitCompletedResponse")
    public record SurveyUnitCompletedResponse
            (
                    String surveyUnitId,
                    String surveyUnitDisplayName,
                    String interviewerLabel,
                    String endDate,
                    ContactOutcomeType contactOutcome,
                    ClosingCauseType closingCauseType,
                    Boolean viewed,
                    String readOnlyUrl,
                    String comment
            ) {}
}
