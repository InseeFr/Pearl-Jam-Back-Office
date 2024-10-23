package fr.insee.pearljam.domain.exception;

public class CommunicationInformationNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Communication Information not found";

    public CommunicationInformationNotFoundException() {
        super(MESSAGE);
    }
}
