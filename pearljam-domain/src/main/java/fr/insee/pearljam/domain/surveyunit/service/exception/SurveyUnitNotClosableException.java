package fr.insee.pearljam.domain.surveyunit.service.exception;

public class SurveyUnitNotClosableException extends RuntimeException {
    public SurveyUnitNotClosableException(String surveyUnitId) {
        super(String.format("Closing cause already exists for survey unit ids %s", surveyUnitId));
    }
}