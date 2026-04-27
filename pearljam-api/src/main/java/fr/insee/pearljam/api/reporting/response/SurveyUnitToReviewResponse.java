package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "SurveyUnitToReviewResponse")
public record SurveyUnitToReviewResponse(
        @Schema(description = "List of survey units to review")
        List<SurveyUnitToReviewDto> content,

        @Schema(description = "Current page number", example = "0")
        int page,

        @Schema(description = "Number of items per page", example = "20")
        int size,

        @Schema(description = "Total number of items", example = "100")
        long totalElements,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages
) {
}