package fr.insee.pearljam.infrastructure.mail;

import fr.insee.pearljam.infrastructure.mail.exception.SendMailException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Dummy mail sender
 */
@Component
@ConditionalOnProperty(name = "feature.mail-service.enabled", havingValue = "false")
public class NoMailSender implements MailSender {
    @Override
    public void sendMail(String subject, String content) throws SendMailException {
        // dummy mail sender
    }
}
