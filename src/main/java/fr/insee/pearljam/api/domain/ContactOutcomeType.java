package fr.insee.pearljam.api.domain;

public enum ContactOutcomeType {
	INA("Interview accepted"),
	REF("Refusal"),
	INI("Interview impossible"),
	ALA("Already answered (other mode)"),
	OOS("Out of scope");
	
	/**
	 * label
	 */
	private String label;

	/**
	 * Defaut constructor for a StateType
	 * 
	 * @param label
	 */
	ContactOutcomeType(String label) {
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
