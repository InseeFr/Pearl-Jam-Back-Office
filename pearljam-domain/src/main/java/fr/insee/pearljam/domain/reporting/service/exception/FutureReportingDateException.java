package fr.insee.pearljam.domain.reporting.service.exception;

public class FutureReportingDateException extends RuntimeException {

    public FutureReportingDateException() {
        super("date must not be in the future");
    }
}
