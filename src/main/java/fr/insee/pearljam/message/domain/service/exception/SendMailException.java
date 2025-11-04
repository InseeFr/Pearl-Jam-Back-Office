package fr.insee.pearljam.message.domain.service.exception;

/**
 * Handle exceptions when sending mails
 */
public class SendMailException extends Exception {
    public SendMailException(String message) {
        super(message);
    }
}