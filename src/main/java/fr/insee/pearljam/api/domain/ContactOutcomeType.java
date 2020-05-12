package fr.insee.pearljam.api.domain;

/**
 * Define the type of the ContactOutcome entity
 * @author scorcaud
 *
 */
public enum ContactOutcomeType {
	INA("Interview accepted"),
	REF("Refusal"),
	INI("Interview impossible"),
	ALA("Already answered (other mode)"),
	OOS("Out of scope");
	
	/**
	 * label of the ContactOutcomeType
	 */
	private String label;

	/**
	 * Defaut constructor for a ContactOutcomeType
	 * @param label
	 */
	ContactOutcomeType(String label) {
		this.label = label;
	}

	/**
	 * Get the label for a ContactOutcomeType
	 * @return label
	 */
	public String getLabel() {
		return label;
	}
}
