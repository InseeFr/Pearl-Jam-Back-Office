package fr.insee.pearljam.api.domain;

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
