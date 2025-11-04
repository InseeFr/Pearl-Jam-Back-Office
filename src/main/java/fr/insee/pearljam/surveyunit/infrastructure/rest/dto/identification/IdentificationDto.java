package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.identification;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.IdentificationConfiguration;
import fr.insee.pearljam.surveyunit.domain.model.Identification;

public interface IdentificationDto {
	Identification toModel();

	static Identification toModel(RawIdentificationDto rawIdentificationDto,
								  IdentificationConfiguration identificationConfiguration) {

		if (rawIdentificationDto == null || IdentificationConfiguration.NOIDENT == identificationConfiguration || identificationConfiguration == null) {
			return null;
		}

		// get all attributes from dto
		Identification identification = rawIdentificationDto.toModel();

		// remove unexpected attributes
		IdentificationDto identificationDto = switch (identificationConfiguration) {
			case HOUSEF2F, IASCO -> HouseF2FIdentificationDto.fromModel(identification);
			case HOUSETEL, HOUSETELWSR -> HouseTelIdentificationDto.fromModel(identification);
			case INDF2F, INDF2FNOR -> IndividualF2FIdentificationDto.fromModel(identification);
			case INDTEL, INDTELNOR -> IndividualTelIdentificationDto.fromModel(identification);
			case SRCVREINT -> SrcvReintIdentificationDto.fromModel(identification);
			default -> throw new IllegalStateException("Unexpected value: " + identificationConfiguration);
		};

		return identificationDto.toModel();
	}


	static IdentificationDto fromModel(Identification identification) {
		if (identification == null) {
			return null;
		}

		return switch (identification.identificationType()) {
			case HOUSEF2F -> HouseF2FIdentificationDto.fromModel(identification);
			case HOUSETEL -> HouseTelIdentificationDto.fromModel(identification);
			case INDF2F -> IndividualF2FIdentificationDto.fromModel(identification);
			case INDTEL -> IndividualTelIdentificationDto.fromModel(identification);
			case SRCVREINT -> SrcvReintIdentificationDto.fromModel(identification);
		};
	}


}
