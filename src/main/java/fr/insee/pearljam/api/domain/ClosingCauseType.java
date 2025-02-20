package fr.insee.pearljam.api.domain;

/**
 * Define the type of the ContactOutcome entity
 * @author scorcaud
 *
 */
public enum ClosingCauseType {
	NPA,//Not processed because of interviewer absence
	NPI,//Not processed by interviewer
	NPX,//Not processed for exceptional reason
	ROW,//Right of withdrawal
}
