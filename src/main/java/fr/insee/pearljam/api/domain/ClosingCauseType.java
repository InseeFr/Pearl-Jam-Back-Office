package fr.insee.pearljam.api.domain;

/**
 * Define the type of the ContactOutcome entity
 * @author scorcaud
 *
 */
public enum ClosingCauseType {
	NPA("Not processed because of interviewer absence"),
	NPI("Not processed by interviewer"),
	NPX("Not processed for exceptional reason"),
	ROW("Right of withdrawal");
	
	/**
	 * label of the ContactOutcomeType
	 */
	private String label;

	/**
	 * Defaut constructor for a ContactOutcomeType
	 * @param label
	 */
	ClosingCauseType(String label) {
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
