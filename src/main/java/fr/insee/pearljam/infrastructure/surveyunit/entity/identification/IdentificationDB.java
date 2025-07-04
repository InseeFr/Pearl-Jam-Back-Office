package fr.insee.pearljam.infrastructure.surveyunit.entity.identification;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "identification")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "identification_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
@Getter
@Setter
public abstract class IdentificationDB implements Serializable {

	@Serial
	private static final long serialVersionUID = 1987L;

	/**
	 * Identification id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "identification_type", insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	protected IdentificationType identificationType;

	@Column(name = "identification_state", insertable = false)
	@Enumerated(EnumType.STRING)
	protected IdentificationState identificationState;

	protected abstract IdentificationConfiguration getIdentificationConfiguration();

	/**
	 * The SurveyUnit associated to Identification
	 */
	@OneToOne
	protected SurveyUnit surveyUnit;

	protected IdentificationDB(Long id, IdentificationType identificationType, SurveyUnit surveyUnit) {
		this.id = id;
		this.identificationType = identificationType;
		this.surveyUnit = surveyUnit;
	}

	/**
	 * return model of the db entity
	 *
	 * @param identificationDB identification DB entity
	 * @return the model object of the db entity
	 */
	public static Identification toModel(IdentificationDB identificationDB) {
		if (identificationDB == null) {
			return null;
		}
		return identificationDB.toModel();
	}

	protected abstract Identification toModel();

	public void update(Identification identification) {
		if (identification == null) {
			return;
		}
		updateFields(identification);
		updateIdentificationState(identification, getIdentificationConfiguration());
	}

	private void updateIdentificationState(Identification identification, IdentificationConfiguration configuration) {
		if (identification != null && configuration != null) {
			this.identificationState = IdentificationState.getState(identification, configuration);
		}
	}

	protected abstract void updateFields(Identification identification);

	public static IdentificationDB fromModel(SurveyUnit surveyUnit,
											 Identification identification,
											 IdentificationConfiguration configuration) {
		if (surveyUnit == null) {
			throw new IllegalArgumentException("SurveyUnit cannot be null");
		}

		if(identification == null){
			return null;
		}


		IdentificationType identificationType = switch (configuration) {
			case HOUSEF2F, IASCO -> IdentificationType.HOUSEF2F;
			case HOUSETEL, HOUSETELWSR -> IdentificationType.HOUSETEL;
			case INDF2F, INDF2FNOR -> IdentificationType.INDF2F;
			case INDTEL, INDTELNOR -> IdentificationType.INDTEL;
			case SRCVREINT -> IdentificationType.SRCVREINT;
			case NOIDENT -> null;
		};
		if (identificationType == null) {
			return null;
		}


		return switch (identificationType) {
			case HOUSEF2F -> new HouseF2FIdentificationDB(
					identification.id(),
					surveyUnit,
					identification.identification(),
					identification.access(),
					identification.situation(),
					identification.category(),
					identification.occupant());
			case HOUSETEL -> new HouseTelIdentificationDB(
					identification.id(),
					surveyUnit,
					identification.situation(),
					identification.category()
			);
			case INDTEL -> new IndTelIdentificationDB(
					identification.id(),
					surveyUnit,
					identification.individualStatus(),
					identification.situation()
			);
			case INDF2F -> new IndF2FIdentificationDB(
					identification.id(),
					surveyUnit,
					identification.individualStatus(),
					identification.situation(),
					identification.interviewerCanProcess()
			);
			case SRCVREINT -> new SrcvReintIdentificationDB(
					identification.id(),
					surveyUnit,
					identification.numberOfRespondents(),
					identification.individualStatus(),
					identification.householdComposition(),
					identification.presentInPreviousHome(),
					identification.situation()
			);
		};


	}
}
