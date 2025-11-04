package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.identification;

import fr.insee.pearljam.surveyunit.domain.model.Identification;
import fr.insee.pearljam.surveyunit.domain.model.question.*;

public record RawIdentificationDto(IdentificationQuestionValue identification,
                                   AccessQuestionValue access,
                                   SituationQuestionValue situation,
                                   CategoryQuestionValue category,
                                   OccupantQuestionValue occupant,
                                   IndividualStatusQuestionValue individualStatus,
                                   InterviewerCanProcessQuestionValue interviewerCanProcess,
                                   NumberOfRespondentsQuestionValue numberOfRespondents,
                                   PresentInPreviousHomeQuestionValue presentInPreviousHome,
                                   HouseholdCompositionQuestionValue householdComposition

) implements IdentificationDto {

	@Override
	public Identification toModel() {
		return Identification.builder()
				.identificationType(null)
				.identification(identification)
				.access(access)
				.situation(situation)
				.category(category)
				.occupant(occupant)
				.interviewerCanProcess(interviewerCanProcess)
				.numberOfRespondents(numberOfRespondents)
				.individualStatus(individualStatus)
				.householdComposition(householdComposition)
				.presentInPreviousHome(presentInPreviousHome)
				.build();
	}
}