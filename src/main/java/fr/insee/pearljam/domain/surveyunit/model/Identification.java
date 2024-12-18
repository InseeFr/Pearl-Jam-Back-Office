package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.domain.surveyunit.model.question.*;
import lombok.Builder;

@Builder
public record Identification(
        IdentificationQuestionValue identification,
        AccessQuestionValue access,
        SituationQuestionValue situation,
        CategoryQuestionValue category,
        OccupantQuestionValue occupant,
        IndividualStatusQuestionValue individualStatus,
        InterviewerCanProcessQuestionValue interviewerCanProcess,
        NumberOfRespondentsQuestionValue numberOfRespondents,
        PresentInPreviousHomeQuestionValue presentInPreviousHome,
        HouseholdCompositionQuestionValue householdComposition
) {
}
