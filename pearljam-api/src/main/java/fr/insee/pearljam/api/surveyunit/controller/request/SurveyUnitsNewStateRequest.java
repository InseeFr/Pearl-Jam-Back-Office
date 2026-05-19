package fr.insee.pearljam.api.surveyunit.controller.request;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SurveyUnitsNewStateRequest {

    @NotEmpty(message = "Survey unit IDs list cannot be empty")
    private List<String> surveyUnitIds;

    @NotNull(message = "State cannot be null")
    private StateType stateType;
}