package fr.insee.pearljam.infrastructure.http.mail.sender;

import fr.insee.pearljam.infrastructure.http.mail.dto.MessageTemplate;
import fr.insee.pearljam.infrastructure.http.mail.dto.Recipient;
import fr.insee.pearljam.infrastructure.http.mail.dto.Recipients;
import fr.insee.pearljam.infrastructure.http.mail.dto.SendRequest;
import fr.insee.pearljam.infrastructure.http.mail.config.MailProperties;
import fr.insee.pearljam.domain.message.service.exception.SendMailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Http mail sender
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "feature.mail-service.enabled", havingValue = "true")
public class RestMailSender implements MailSender {
    private final MailProperties mailProperties;

    @Qualifier("mailRestClient")
    private final RestClient.Builder mailRestClient;

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
        SendRequest request = new SendRequest();
        request.setMessageTemplate(messagetemplate);
        request.setRecipients(recipients);

        log.info("Calling {}", mailProperties.url());
        try {
            HttpStatusCode status = mailRestClient
                    .build()
                    .post()
                    .body(request)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();

            if (status.value() == HttpStatus.OK.value()) {
                log.info("Le mail a bien été envoyé");
                return;
            }
            throw new SendMailException(String.format("Error sending mail. Status: %s", status.value()));
        } catch(RestClientException ex) {
            throw new SendMailException(ex.getMessage());
        }
    }
}

