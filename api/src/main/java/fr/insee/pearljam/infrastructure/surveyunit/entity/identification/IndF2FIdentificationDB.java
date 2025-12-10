package fr.insee.pearljam.infrastructure.surveyunit.entity.identification;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.question.IndividualStatusQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.InterviewerCanProcessQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.SituationQuestionValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("INDF2F")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IndF2FIdentificationDB extends IdentificationDB {

	@Column
	@Enumerated(EnumType.STRING)
	private IndividualStatusQuestionValue individualStatus;

	@Column
	@Enumerated(EnumType.STRING)
	private SituationQuestionValue situation;

	@Column
	@Enumerated(EnumType.STRING)
	private InterviewerCanProcessQuestionValue interviewerCanProcess;

	@Override
	protected IdentificationConfiguration getIdentificationConfiguration() {
		return IdentificationConfiguration.INDF2F;
	}


	public IndF2FIdentificationDB(
			Long id,
			SurveyUnit surveyUnit,
			IndividualStatusQuestionValue individualStatus,
			SituationQuestionValue situation,
			InterviewerCanProcessQuestionValue interviewerCanProcess) {
		super(id, IdentificationType.INDF2F, surveyUnit);
		this.individualStatus = individualStatus;
		this.situation = situation;
		this.interviewerCanProcess = interviewerCanProcess;
	}

	@Override
	protected Identification toModel() {
		return Identification.builder()
				.id(id)
				.identificationType(identificationType)
				.individualStatus(individualStatus)
				.situation(situation)
				.interviewerCanProcess(interviewerCanProcess)
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
		this.setIndividualStatus(identification.individualStatus());
		this.setInterviewerCanProcess(identification.interviewerCanProcess());
		this.setSituation(identification.situation());
	}
}
