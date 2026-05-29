package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.CommentDto;
import fr.insee.pearljam.domain.surveyunit.port.in.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Comment endpoints for survey units
 *
 */
@RestController
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentController {
    private final CommentService commentService;

    /**
     * This method is used to update the comment of a Survey Unit
     * @param comment comment to update
     * @param surveyUnitId survey unit id
     */
    @Operation(summary = "Update the comment of a survey unit")
    @PutMapping(path = "/survey-unit/{id}/comment")
    public void updateSurveyUnitComment(
            @Valid @NotNull @RequestBody CommentDto comment,
            @PathVariable(value = "id") String surveyUnitId) {
        commentService.updateSurveyUnitComment(CommentDto.toModel(surveyUnitId, comment));
    }
}
