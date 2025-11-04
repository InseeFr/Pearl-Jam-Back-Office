package fr.insee.pearljam.message.domain.port.serverside;

import fr.insee.pearljam.message.domain.service.exception.SendMailException;

public interface MailSender {
    /**
     * send mail
     * @param subject mail subject
     * @param content mail content
     */
    void sendMail(String subject, String content) throws SendMailException;
}
