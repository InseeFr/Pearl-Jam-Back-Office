package fr.insee.pearljam.api.domain;

public enum ContactAttemptConfiguration {
    F2F("Face-to-face"), TEL("Telephone");

    /**
     * label of the ContactAttemptConfiguration
     */
    private String label;

    /**
     * Defaut constructor for a ContactAttemptConfiguration
     * 
     * @param label
     */
    ContactAttemptConfiguration(String label) {
        this.label = label;
    }

    /**
     * Get the label for a ContactAttemptConfiguration
     * 
     * @return label
     */
    public String getLabel() {
        return label;
    }
}
