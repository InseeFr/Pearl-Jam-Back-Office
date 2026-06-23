package fr.insee.pearljam.api.message.controller.dummy;

import fr.insee.pearljam.infrastructure.http.mail.sender.MailSender;
import fr.insee.pearljam.domain.message.service.exception.SendMailException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MailFakeSender implements MailSender {

    @Getter
    private boolean isMailSent = false;

    @Setter
    private boolean throwSendMailException = false;

    @Override
    public void sendMail(String subject, String content) throws SendMailException {
        if(throwSendMailException) {
            throw new SendMailException("error");
        }
        isMailSent = true;
    }
}
