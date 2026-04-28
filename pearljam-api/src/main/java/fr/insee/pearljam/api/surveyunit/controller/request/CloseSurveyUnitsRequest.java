package fr.insee.pearljam.api.surveyunit.controller.request;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CloseSurveyUnitsRequest {

    @NotEmpty(message = "Survey unit IDs list cannot be empty")
    private List<String> surveyUnitIds;

    @NotEmpty(message = "Closing Cause cannot be empty")
    private ClosingCauseType closingCauseType;
}