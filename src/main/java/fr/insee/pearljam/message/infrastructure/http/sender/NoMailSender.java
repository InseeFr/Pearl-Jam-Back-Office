package fr.insee.pearljam.message.infrastructure.http.sender;

import fr.insee.pearljam.message.domain.service.exception.SendMailException;
import fr.insee.pearljam.message.domain.port.serverside.MailSender;
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
