package fr.insee.pearljam.api.domain;

public enum ContactOutcomeConfiguration {
    F2F("Face-to-face"), TEL("Telephone");

    /**
     * label of the ContactOutcomeConfiguration
     */
    private String label;

    /**
     * Defaut constructor for a ContactOutcomeConfiguration
     * 
     * @param label
     */
    ContactOutcomeConfiguration(String label) {
        this.label = label;
    }

    /**
     * Get the label for a ContactOutcomeConfiguration
     * 
     * @return label
     */
    public String getLabel() {
        return label;
    }
}
