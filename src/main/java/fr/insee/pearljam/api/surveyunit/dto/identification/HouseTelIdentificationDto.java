package fr.insee.pearljam.api.surveyunit.dto.identification;

import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;

public record HouseTelIdentificationDto
		(
		 SituationQuestionValue situation,
		 CategoryQuestionValue category) implements IdentificationDto {


	/**
	 * Converts a HouseTelIdentificationDto to an Identification domain model.
	 *
	 * @return the corresponding Identification domain model
	 */

	@Override
	public Identification toModel() {
		return Identification.builder()
				.identificationType(IdentificationType.HOUSETEL)
				.situation(situation)
				.category(category)
				.build();
	}

	/**
	 * Converts an Identification domain model to a HouseTelIdentificationDto.
	 *
	 * @param identification the Identification domain model to convert
	 * @return the corresponding HouseTelIdentificationDto
	 */
	public static HouseTelIdentificationDto fromModel(Identification identification) {

		return new HouseTelIdentificationDto(
				identification.situation(),
				identification.category()
		);
	}
}
