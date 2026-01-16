package fr.insee.pearljam.api.surveyunit.dto.identification;

import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;

public record SrcvReintIdentificationDto
		(NumberOfRespondentsQuestionValue numberOfRespondents,
		 IndividualStatusQuestionValue individualStatus,
		 HouseholdCompositionQuestionValue householdComposition,
		 PresentInPreviousHomeQuestionValue presentInPreviousHome,
		 SituationQuestionValue situation,
		 Boolean demenagementWeb,
		 Boolean demenagementEnqueteur) implements IdentificationDto {


	/**
	 * Converts a SrcvReintdentificationDto to an Identification domain model.
	 *
	 * @return the corresponding Identification domain model
	 */
	@Override
	public Identification toModel() {
		return Identification.builder()
				.identificationType(IdentificationType.SRCVREINT)
				.numberOfRespondents(numberOfRespondents)
				.individualStatus(individualStatus)
				.householdComposition(householdComposition)
				.presentInPreviousHome(presentInPreviousHome)
				.situation(situation)
				.demenagementWeb(demenagementWeb)
				.demenagementEnqueteur(demenagementEnqueteur)
				.build();
	}

	/**
	 * Converts an Identification domain model to a SrcvReintdentificationDto.
	 *
	 * @param identification the Identification domain model to convert
	 * @return the corresponding SrcvReintdentificationDto
	 */
	public static SrcvReintIdentificationDto fromModel(Identification identification) {

		return new SrcvReintIdentificationDto(
				identification.numberOfRespondents(),
				identification.individualStatus(),
				identification.householdComposition(),
				identification.presentInPreviousHome(),
				identification.situation(),
				identification.demenagementWeb(),
				identification.demenagementEnqueteur()
		);
	}
}
