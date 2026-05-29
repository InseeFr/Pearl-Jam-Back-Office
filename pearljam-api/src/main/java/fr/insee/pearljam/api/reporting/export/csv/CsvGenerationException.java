package fr.insee.pearljam.api.reporting.export.csv;

public class CsvGenerationException extends RuntimeException {

    public CsvGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
