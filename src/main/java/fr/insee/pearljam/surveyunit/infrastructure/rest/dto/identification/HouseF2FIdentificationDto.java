package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.identification;

import fr.insee.pearljam.surveyunit.domain.model.Identification;
import fr.insee.pearljam.surveyunit.domain.model.IdentificationType;
import fr.insee.pearljam.surveyunit.domain.model.question.*;

public record HouseF2FIdentificationDto
		(IdentificationQuestionValue identification,
         AccessQuestionValue access,
         SituationQuestionValue situation,
         CategoryQuestionValue category,
         OccupantQuestionValue occupant) implements IdentificationDto {


	/**
	 * Converts a HouseF2FIdentificationDto to an Identification domain model.
	 *
	 * @return the corresponding Identification domain model
	 */
	@Override
	public Identification toModel() {
		return Identification.builder()
				.identificationType(IdentificationType.HOUSEF2F)
				.identification(identification)
				.access(access)
				.situation(situation)
				.category(category)
				.occupant(occupant)
				.build();
	}

	/**
	 * Converts an Identification domain model to a HouseF2FIdentificationDto.
	 *
	 * @param identification the Identification domain model to convert
	 * @return the corresponding HouseF2FIdentificationDto
	 */
	public static HouseF2FIdentificationDto fromModel(Identification identification) {

		return new HouseF2FIdentificationDto(
				identification.identification(),
				identification.access(),
				identification.situation(),
				identification.category(),
				identification.occupant()
		);
	}
}
