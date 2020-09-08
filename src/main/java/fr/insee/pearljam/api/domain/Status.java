package fr.insee.pearljam.api.domain;

/**
 * Define the possible Status of a SurveyUnit
 * @author scorcaud
 */
public enum Status {

	NOC("No contact"),
	INA("Interview accepted"),
	APT("Appointment made"),
	REF("Refusal"),
	ABS("Occasional absence of the interviewer"),
	INI("Interview impossible"),
	ALA("Already answered in another mode"),
	WAM("Wish to answer in another mode");

	
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
