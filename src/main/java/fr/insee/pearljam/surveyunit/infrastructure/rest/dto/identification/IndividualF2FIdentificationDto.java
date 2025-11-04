package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.identification;

import fr.insee.pearljam.surveyunit.domain.model.Identification;
import fr.insee.pearljam.surveyunit.domain.model.question.IndividualStatusQuestionValue;
import fr.insee.pearljam.surveyunit.domain.model.question.InterviewerCanProcessQuestionValue;
import fr.insee.pearljam.surveyunit.domain.model.question.SituationQuestionValue;

public record IndividualF2FIdentificationDto
		(
		 IndividualStatusQuestionValue individualStatus,
		 InterviewerCanProcessQuestionValue interviewerCanProcess,
		 SituationQuestionValue situation) implements IdentificationDto {


	/**
	 * Converts a IndividualF2FIdentificationDto to an Identification domain model.
	 *
	 * @return the corresponding Identification domain model
	 */
	@Override
	 public  Identification toModel() {
		return Identification.builder()
				.individualStatus(individualStatus)
				.interviewerCanProcess(interviewerCanProcess)
				.situation(situation)
				.build();
	}

	/**
	 * Converts an Identification domain model to a IndividualF2FIdentificationDto.
	 *
	 * @param identification the Identification domain model to convert
	 * @return the corresponding IndividualF2FIdentificationDto
	 */
	public static IndividualF2FIdentificationDto fromModel(Identification identification) {

		return new IndividualF2FIdentificationDto(
				identification.individualStatus(),
				identification.interviewerCanProcess(),
				identification.situation()
		);
	}
}
