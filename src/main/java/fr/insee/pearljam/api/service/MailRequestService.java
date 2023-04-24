package fr.insee.pearljam.api.service;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.MailRequestStatus;
import fr.insee.pearljam.api.dto.mailrequest.MailRequestDto;
import fr.insee.pearljam.api.exception.NotFoundException;

@Service
public interface MailRequestService {

    /**
     * If exist, update status of targetted mailRequest
     * 
     * @param id
     * @param newStatus
     * @return Updated MailRequestDto
     * @throws NotFoundException
     */
    public MailRequestDto updateMailRequest(Long id, MailRequestStatus newStatus) throws NotFoundException;

    /**
     * Try to send an unprocessed mailRequest
     * 
     * @param id
     * @return
     */
    public boolean sendMailRequest(Long id);
}
