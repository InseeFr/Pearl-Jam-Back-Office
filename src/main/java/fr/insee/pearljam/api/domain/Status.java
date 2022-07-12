package fr.insee.pearljam.api.domain;

/**
 * Define the possible Status of a SurveyUnit
 * 
 * @author scorcaud
 */
public enum Status {

	INA("Interview accepted"),
	APT("Appointment made"),
	REF("Refusal"),
	TUN("Temporary UNavailable"),
	NOC("No contact"),
	MES("Message Sent"),
	UCD("Unusable Contact Data"),
	NLH("Notification Letter Hand-delivered"),
	NPS("Notice of Passage Sent"),
	PUN("Permanently UNavailable");

	/**
	 * label
	 */
	private String label;

	/**
	 * Defaut constructor for a Status
	 * 
	 * @param label
	 */
	Status(String label) {
		this.label = label;
	}

	/**
	 * Get the label for a Status
	 * 
	 * @return label
	 */
	public String getLabel() {
		return label;
	}
}
