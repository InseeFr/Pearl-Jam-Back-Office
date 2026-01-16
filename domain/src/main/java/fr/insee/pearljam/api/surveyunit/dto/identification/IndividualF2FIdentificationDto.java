package fr.insee.pearljam.api.surveyunit.dto.identification;

import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.question.IndividualStatusQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.InterviewerCanProcessQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.SituationQuestionValue;

public record IndividualF2FIdentificationDto
		(
		 IndividualStatusQuestionValue individualStatus,
		 InterviewerCanProcessQuestionValue interviewerCanProcess,
		 SituationQuestionValue situation,
		 Boolean demenagementWeb,
		 Boolean demenagementEnqueteur) implements IdentificationDto {


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
				.demenagementWeb(demenagementWeb)
				.demenagementEnqueteur(demenagementEnqueteur)
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
				identification.situation(),
				identification.demenagementWeb(),
				identification.demenagementEnqueteur()
		);
	}
}
