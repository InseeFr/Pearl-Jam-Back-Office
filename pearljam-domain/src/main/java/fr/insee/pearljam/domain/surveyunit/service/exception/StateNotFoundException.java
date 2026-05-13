package fr.insee.pearljam.domain.surveyunit.service.exception;

public class StateNotFoundException extends RuntimeException {

    public static final String MESSAGE = "No state found for survey unit id %s";

    public StateNotFoundException(String id) {
        super(String.format(MESSAGE, id));
    }
}
