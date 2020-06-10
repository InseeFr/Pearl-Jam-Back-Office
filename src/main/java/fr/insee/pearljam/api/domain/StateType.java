package fr.insee.pearljam.api.domain;

/**
 * The possible types of a State entity
 * @author scorcaud
 *
 */
public enum StateType {

	ANS("Assigned, not started"), 
	PRC("Preparing contact"), 
	AOC("At least one contact made"),
	APS("Appointment scheduled"), 
	INS("Interview started"), 
	WFT("Waiting for transmission"),
	WFS("Waiting for synchronization"), 
	TBR("To be reviewed"), 
	FIN("Finalized"), 
	NVI("Not visible to interviewer"),
	NVM("Not visible to management");

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
