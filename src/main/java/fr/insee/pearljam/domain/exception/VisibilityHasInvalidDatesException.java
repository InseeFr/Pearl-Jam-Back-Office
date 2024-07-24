package fr.insee.pearljam.domain.exception;

public class VisibilityHasInvalidDatesException extends Exception {

    public static final String MESSAGE = "Invalid Visibility dates : should be strictly increasing";

    public VisibilityHasInvalidDatesException() {
        super(MESSAGE);
    }
}
