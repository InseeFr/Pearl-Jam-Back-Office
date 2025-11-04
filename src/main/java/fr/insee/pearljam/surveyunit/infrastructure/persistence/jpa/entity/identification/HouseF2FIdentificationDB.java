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
@DiscriminatorValue("HOUSEF2F")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HouseF2FIdentificationDB extends IdentificationDB {

	@Column
	@Enumerated(EnumType.STRING)
	private IdentificationQuestionValue identification;

	@Column
	@Enumerated(EnumType.STRING)
	private AccessQuestionValue access;

	@Column
	@Enumerated(EnumType.STRING)
	private SituationQuestionValue situation;

	@Column
	@Enumerated(EnumType.STRING)
	private CategoryQuestionValue category;

	@Column
	@Enumerated(EnumType.STRING)
	private OccupantQuestionValue occupant;

	@Override
	protected IdentificationConfiguration getIdentificationConfiguration() {
		return IdentificationConfiguration.HOUSEF2F;
	}

	public HouseF2FIdentificationDB(
			Long id,
			SurveyUnit surveyUnit,
			IdentificationQuestionValue identification,
			AccessQuestionValue access,
			SituationQuestionValue situation,
			CategoryQuestionValue category,
			OccupantQuestionValue occupant) {
		super(id, IdentificationType.HOUSEF2F, surveyUnit);
		this.identification = identification;
		this.access = access;
		this.situation = situation;
		this.category = category;
		this.occupant = occupant;
	}

	@Override
	protected Identification toModel() {
		return Identification.builder()
				.id(id)
				.identificationType(identificationType)
				.identification(identification)
				.access(access)
				.situation(situation)
				.category(category)
				.occupant(occupant)
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

		this.setIdentification(identification.identification());
		this.setAccess(identification.access());
		this.setSituation(identification.situation());
		this.setCategory(identification.category());
		this.setOccupant(identification.occupant());
	}
}
