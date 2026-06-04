package fr.insee.pearljam.domain.surveyunit.readmodel;

import fr.insee.pearljam.domain.surveyunit.model.StateType;

public record SurveyUnitState(
        Long date,
        StateType type
) {
}
