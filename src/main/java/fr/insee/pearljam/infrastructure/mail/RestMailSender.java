package fr.insee.pearljam.infrastructure.mail;

import fr.insee.pearljam.infrastructure.mail.entity.MessageTemplate;
import fr.insee.pearljam.infrastructure.mail.entity.Recipient;
import fr.insee.pearljam.infrastructure.mail.entity.Recipients;
import fr.insee.pearljam.infrastructure.mail.entity.SendRequest;
import fr.insee.pearljam.infrastructure.mail.config.MailProperties;
import fr.insee.pearljam.infrastructure.mail.exception.SendMailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Http mail sender
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "feature.mail-service.enabled", havingValue = "true")
public class RestMailSender implements MailSender {
    private final MailProperties mailProperties;

    private final RestTemplate mailRestTemplate;

    @Override
    public void sendMail(String subject, String content) throws SendMailException {
        MessageTemplate messagetemplate = new MessageTemplate();
        messagetemplate.setSubject(subject);
        messagetemplate.setContent(content);
        messagetemplate.setSender(mailProperties.mailSender());

        Recipient recipient = new Recipient();
        recipient.setAddress(mailProperties.mailRecipients());
        Recipients recipients = new Recipients();
        recipients.getRecipient().add(recipient);
        SendRequest body = new SendRequest();
        body.setMessageTemplate(messagetemplate);
        body.setRecipients(recipients);

        HttpEntity<SendRequest> request = new HttpEntity<>(body);
        log.info("Calling {}", mailProperties.url());
        try {
            ResponseEntity<String> response = mailRestTemplate.exchange(mailProperties.url(), HttpMethod.POST, request,
                    String.class);
            log.info("Response : message # {}", response.getStatusCode());
        } catch(RestClientException ex) {
            log.error(ex.getMessage(), ex);
            throw new SendMailException();
        }
    }
}

