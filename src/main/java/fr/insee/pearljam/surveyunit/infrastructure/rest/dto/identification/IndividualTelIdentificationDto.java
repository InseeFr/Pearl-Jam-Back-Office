package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.identification;

import fr.insee.pearljam.surveyunit.domain.model.Identification;
import fr.insee.pearljam.surveyunit.domain.model.IdentificationType;
import fr.insee.pearljam.surveyunit.domain.model.question.IndividualStatusQuestionValue;
import fr.insee.pearljam.surveyunit.domain.model.question.SituationQuestionValue;

public record IndividualTelIdentificationDto
		(IndividualStatusQuestionValue individualStatus,
		 SituationQuestionValue situation) implements IdentificationDto {


	/**
	 * Converts a IndividualTelIdentificationDto to an Identification domain model.
	 *
	 * @return the corresponding Identification domain model
	 */
	@Override
	public Identification toModel() {
				return Identification.builder()
				.identificationType(IdentificationType.INDTEL)
				.individualStatus(individualStatus)
				.situation(situation)
				.build();
	}

	/**
	 * Converts an Identification domain model to a IndividualTelIdentificationDto.
	 *
	 * @param identification the Identification domain model to convert
	 * @return the corresponding IndividualTelIdentificationDto
	 */
	public static IndividualTelIdentificationDto fromModel(Identification identification) {

		return new IndividualTelIdentificationDto(
				identification.individualStatus(),
				identification.situation()
		);
	}
}
