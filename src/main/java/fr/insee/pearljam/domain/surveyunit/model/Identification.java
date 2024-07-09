package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.domain.surveyunit.model.question.*;

public record Identification(
        IdentificationQuestionValue identification,
        AccessQuestionValue access,
        SituationQuestionValue situation,
        CategoryQuestionValue category,
        OccupantQuestionValue occupant) {
}
