package fr.insee.pearljam.api.domain;

/**
 * The possible types of a State entity
 * @author scorcaud
 *
 */
public enum MessageStatusType {

	REA("Read"), 
	DEL("Deleted"), 
	NRD("Not read");

	/**
	 * label of the State type
	 */
	private String label;

	/**
	 * Defaut constructor for a StateType
	 * 
	 * @param label
	 */
	MessageStatusType(String label) {
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
