package fr.insee.pearljam.infrastructure.mail;

import fr.insee.pearljam.infrastructure.mail.exception.SendMailException;

public interface MailSender {
    /**
     * send mail
     * @param subject mail subject
     * @param content mail content
     */
    void sendMail(String subject, String content) throws SendMailException;
}
