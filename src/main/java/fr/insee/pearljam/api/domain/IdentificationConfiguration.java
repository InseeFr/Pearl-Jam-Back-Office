package fr.insee.pearljam.api.domain;

import lombok.Getter;

@Getter
public enum IdentificationConfiguration {
	IASCO("Identification - Access - Situation - Category - Occupant"),// will be replaced by `HOUSEF2F`
	HOUSEF2F("Household identification - Face to face"),
	HOUSETEL("Household identification - Telephone"),
	HOUSETELWSR("Household identification - Telephone - including secondary residence"),
	INDF2F("Individual identification - Face to face"),
	INDF2FNOR("Individual identification - Face to face - including Non Ordinary residence"),
	INDTEL("Individual identification - Telephone"),
	INDTELNOR("Individual identification - Telephone - including Non Ordinary residence"),
	SRCVREINT("Only for SRCV campaign re-interrogation"),
	NOIDENT("No identification");

	/**
	 * label of the IdentificationConfiguration
	 */
	private final String label;

	/**
	 * Defaut constructor for a IdentificationConfiguration
	 *
	 * @param label label of identificationConfiguration
	 */
	IdentificationConfiguration(String label) {
		this.label = label;
	}


	/**
	 * Finds the IdentificationConfiguration enum by its name.
	 *
	 * @param name the name to search for
	 * @return the corresponding IdentificationConfiguration enum
	 */
	public static IdentificationConfiguration fromName(String name) {
		try {
			return IdentificationConfiguration.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("No matching IdentificationConfiguration for name: " + name, e);
		}
	}

}
