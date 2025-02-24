package fr.insee.pearljam.infrastructure.surveyunit.entity.identification;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("HOUSETEL")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HouseTelIdentificationDB extends IdentificationDB {

	@Column
	@Enumerated(EnumType.STRING)
	private SituationQuestionValue situation;

	@Column
	@Enumerated(EnumType.STRING)
	private CategoryQuestionValue category;


	public HouseTelIdentificationDB(
			Long id,
			SurveyUnit surveyUnit,
			SituationQuestionValue situation,
			CategoryQuestionValue category) {
		super(id, IdentificationType.HOUSETEL, surveyUnit);
		this.situation = situation;
		this.category = category;
	}

	@Override
	protected Identification toModel() {
		return Identification.builder()
				.id(id)
				.identificationType(identificationType)
				.situation(situation)
				.category(category)
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
		this.setSituation(identification.situation());
		this.setCategory(identification.category());

		updateIdentificationState(identification, IdentificationConfiguration.HOUSETEL);
	}
}
