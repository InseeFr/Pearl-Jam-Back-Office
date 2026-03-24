package fr.insee.pearljam.domain.campaign.service.exception;

public class VisibilityHasInvalidDatesException extends Exception {

    public static final String MESSAGE = "Invalid Visibility dates : should be strictly increasing";

    public VisibilityHasInvalidDatesException() {
        super(MESSAGE);
    }
}
