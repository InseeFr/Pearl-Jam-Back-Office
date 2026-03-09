package fr.insee.pearljam.domain.message.service.exception;

/**
 * Handle exceptions when sending mails
 */
public class SendMailException extends Exception {
    public SendMailException(String message) {
        super(message);
    }
}