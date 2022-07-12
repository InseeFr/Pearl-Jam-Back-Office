package fr.insee.pearljam.api.domain;

public enum IdentificationConfiguration {
    IASCO("Identification - Access - Situation - Category - Occupant"),
    NOIDENT("No identification");

    /**
     * label of the IdentificationConfiguration
     */
    private String label;

    /**
     * Defaut constructor for a IdentificationConfiguration
     * 
     * @param label
     */
    IdentificationConfiguration(String label) {
        this.label = label;
    }

    /**
     * Get the label for a IdentificationConfiguration
     * 
     * @return label
     */
    public String getLabel() {
        return label;
    }
}
