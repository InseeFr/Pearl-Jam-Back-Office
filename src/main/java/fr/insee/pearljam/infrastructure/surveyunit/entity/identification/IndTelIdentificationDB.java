package fr.insee.pearljam.infrastructure.surveyunit.entity.identification;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.question.IndividualStatusQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.SituationQuestionValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("INDTEL")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IndTelIdentificationDB extends IdentificationDB {

	@Column
	@Enumerated(EnumType.STRING)
	private IndividualStatusQuestionValue individualStatus;

	@Column
	@Enumerated(EnumType.STRING)
	private SituationQuestionValue situation;


	public IndTelIdentificationDB(
			Long id,
			SurveyUnit surveyUnit,
			IndividualStatusQuestionValue individualStatus,
			SituationQuestionValue situation) {
		super(id, IdentificationType.INDTEL, surveyUnit);
		this.individualStatus = individualStatus;
		this.situation = situation;
	}

	protected Identification toModel() {
		return Identification.builder()
				.id(id)
				.identificationType(identificationType)
				.individualStatus(individualStatus)
				.situation(situation)
				.build();
	}


	/**
	 * update the db entity from the model object
	 *
	 * @param identification model object
	 */
	@Override
	public void update(Identification identification) {
		if (identification == null) {
			return;
		}
		this.setIndividualStatus(identification.individualStatus());
		this.setSituation(identification.situation());

		updateIdentificationState(identification, IdentificationConfiguration.INDTEL);
	}
}
