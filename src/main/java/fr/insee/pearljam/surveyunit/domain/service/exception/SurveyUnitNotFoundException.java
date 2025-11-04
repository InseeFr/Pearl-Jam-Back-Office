package fr.insee.pearljam.surveyunit.domain.service.exception;

public class SurveyUnitNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Survey unit %s not found";

    public SurveyUnitNotFoundException(String id) {
        super(String.format(MESSAGE, id));
    }
}
