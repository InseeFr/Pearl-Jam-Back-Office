package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.identification;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.IdentificationConfiguration;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;
import fr.insee.pearljam.surveyunit.domain.model.Identification;
import fr.insee.pearljam.surveyunit.domain.model.IdentificationType;
import fr.insee.pearljam.surveyunit.domain.model.question.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("SRCVREINT")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SrcvReintIdentificationDB extends IdentificationDB {

	@Column
	@Enumerated(EnumType.STRING)
	private NumberOfRespondentsQuestionValue numberOfRespondents;

	@Column
	@Enumerated(EnumType.STRING)
	private IndividualStatusQuestionValue individualStatus;

	@Column
	@Enumerated(EnumType.STRING)
	private HouseholdCompositionQuestionValue householdComposition;

	@Column
	@Enumerated(EnumType.STRING)
	private PresentInPreviousHomeQuestionValue presentInPreviousHome;

	@Column
	@Enumerated(EnumType.STRING)
	private SituationQuestionValue situation;

	@Override
	protected IdentificationConfiguration getIdentificationConfiguration() {
		return IdentificationConfiguration.SRCVREINT;
	}

	public SrcvReintIdentificationDB(
			Long id,
			SurveyUnit surveyUnit,
			NumberOfRespondentsQuestionValue numberOfRespondents,
			IndividualStatusQuestionValue individualStatus,
			HouseholdCompositionQuestionValue householdComposition,
			PresentInPreviousHomeQuestionValue presentInPreviousHome,
			SituationQuestionValue situation) {
		super(id, IdentificationType.SRCVREINT, surveyUnit);
		this.numberOfRespondents = numberOfRespondents;
		this.individualStatus = individualStatus;
		this.householdComposition = householdComposition;
		this.presentInPreviousHome = presentInPreviousHome;
		this.situation = situation;
	}

	protected  Identification toModel() {
		return Identification.builder()
				.id(id)
				.identificationType(identificationType)
				.numberOfRespondents(numberOfRespondents)
				.individualStatus(individualStatus)
				.householdComposition(householdComposition)
				.presentInPreviousHome(presentInPreviousHome)
				.situation(situation)
				.build();
	}

	/**
	 * update the db entity from the model object
	 *
	 * @param identification model object
	 */
	@Override
	protected void updateFields(Identification identification) {
		if (identification == null) {
			return;
		}
		this.setNumberOfRespondents(identification.numberOfRespondents());
		this.setIndividualStatus(identification.individualStatus());
		this.setHouseholdComposition(identification.householdComposition());
		this.setPresentInPreviousHome(identification.presentInPreviousHome());
		this.setSituation(identification.situation());
	}
}
