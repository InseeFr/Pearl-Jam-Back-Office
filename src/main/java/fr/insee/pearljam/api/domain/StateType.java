package fr.insee.pearljam.api.domain;

/**
 * The possible types of a State entity
 * @author scorcaud
 *
 */
public enum StateType {
	
	NVM("Not visible to management"),
	NNS("Not Assigned, not started"),
	ANV("Assigned Not visible to interviewer"),
	VIN("Visible to the interviewer and not clickable"),
	VIC("Visible to the interviewer and clickable"),
	PRC("Preparing contact"), 
	AOC("At least one contact made"),
	APS("Appointment scheduled"), 
	INS("Interview started"), 
	WFT("Waiting for transmission"),
	WFS("Waiting for synchronization"), 
	TBR("To be reviewed"), 
	FIN("Finalized"),
	QNA("Questionnaire Not Available to interviewer"),
	CLO("Closed"),
	NVA("Not Available to All");
	
	/**
	 * label of the State type
	 */
	private String label;

	/**
	 * Defaut constructor for a StateType
	 * 
	 * @param label
	 */
	StateType(String label) {
		this.label = label;
	}

	/**
	 * Get the label for a StateType
	 * 
	 * @return label
	 */
	public String getLabel() {
		return label;
	}
}
