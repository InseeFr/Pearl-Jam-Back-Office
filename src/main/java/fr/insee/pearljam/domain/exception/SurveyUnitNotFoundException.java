package fr.insee.pearljam.domain.exception;

public class SurveyUnitNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Survey unit %s not found";

    public SurveyUnitNotFoundException(String id) {
        super(String.format(MESSAGE, id));
    }
}
