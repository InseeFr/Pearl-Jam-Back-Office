package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.identification;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.IdentificationConfiguration;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;
import fr.insee.pearljam.surveyunit.domain.model.Identification;
import fr.insee.pearljam.surveyunit.domain.model.IdentificationType;
import fr.insee.pearljam.surveyunit.domain.model.question.CategoryQuestionValue;
import fr.insee.pearljam.surveyunit.domain.model.question.SituationQuestionValue;
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

	@Override
	protected IdentificationConfiguration getIdentificationConfiguration() {
		return IdentificationConfiguration.HOUSETEL;
	}

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
	public void updateFields(Identification identification) {
		if (identification == null) {
			return;
		}
		this.setSituation(identification.situation());
		this.setCategory(identification.category());
	}
}
