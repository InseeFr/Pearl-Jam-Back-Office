package fr.insee.pearljam.surveyunit.domain.model;

import fr.insee.pearljam.surveyunit.domain.model.question.*;
import lombok.Builder;

@Builder
public record Identification(
		Long id,
		IdentificationType identificationType,
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
