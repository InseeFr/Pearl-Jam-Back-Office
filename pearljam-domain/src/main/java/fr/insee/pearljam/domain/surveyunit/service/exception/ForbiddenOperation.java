package fr.insee.pearljam.domain.surveyunit.service.exception;

public class ForbiddenOperation extends RuntimeException {
    public ForbiddenOperation(String message) {
        super(message);
    }
}
