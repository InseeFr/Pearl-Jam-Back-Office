package fr.insee.pearljam.api.domain;

public enum Status {
	COM("Contact made"),
	NIN("Number not in use"),
	NOC("No contact"),
	BUL("Busy line"),
	ANM("Answering machine");
	
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
