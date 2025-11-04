package fr.insee.pearljam.campaign.domain.model;

/**
 * Define the type of the ContactOutcome entity
 *
 * @author scorcaud
 */
public enum ContactOutcomeType {
	INA,//Interview accepted
	REF,//Refusal
	IMP,//Impossible to reach
	UCD,//Unusable Contact Data
	UTR,//Unable To Respond
	ALA,//Already answered
	DUK,//Definitely Unavailable for a Known reason
	DUU,//Definitely Unavailable for an Unknown reason => deprecated
	NUH,//No longer Used for Habitation
	DCD,//Deceased => deprecated
	NOA//Not Applicable

}
