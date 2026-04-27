package fr.insee.pearljam.api.surveyunit.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CloseSurveyUnitsRequest {

    @NotEmpty(message = "Survey unit IDs list cannot be empty")
    private List<String> surveyUnitIds;

    public CloseSurveyUnitsRequest(List<String> surveyUnitIds) {
        this.surveyUnitIds = surveyUnitIds;
    }

}