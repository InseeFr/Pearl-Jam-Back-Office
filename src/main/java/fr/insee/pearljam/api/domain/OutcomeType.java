package fr.insee.pearljam.api.domain;

public enum OutcomeType {
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
	OutcomeType(String label) {
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
