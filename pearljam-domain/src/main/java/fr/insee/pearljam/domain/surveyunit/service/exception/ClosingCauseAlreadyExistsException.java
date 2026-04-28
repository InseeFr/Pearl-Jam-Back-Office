package fr.insee.pearljam.domain.surveyunit.service.exception;

public class ClosingCauseAlreadyExistsException extends RuntimeException {
    public ClosingCauseAlreadyExistsException(String surveyUnitId) {
        super(String.format("Closing cause already exists for survey unit ids %s", surveyUnitId));
    }
}