package fr.insee.pearljam.infrastructure.surveyunit.entity.identification;

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

	@Override
	public void update(Identification identification) {
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
