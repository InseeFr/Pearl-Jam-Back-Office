package fr.insee.pearljam.infrastructure.http.mail.sender;

import fr.insee.pearljam.domain.message.service.exception.SendMailException;

public interface MailSender {
    /**
     * send mail
     * @param subject mail subject
     * @param content mail content
     */
    void sendMail(String subject, String content) throws SendMailException;
}
