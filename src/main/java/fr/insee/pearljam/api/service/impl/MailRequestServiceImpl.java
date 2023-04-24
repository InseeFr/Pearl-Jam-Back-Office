package fr.insee.pearljam.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.pearljam.api.domain.MailRequestStatus;
import fr.insee.pearljam.api.dto.mailrequest.MailRequestDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.MailRequestRepository;
import fr.insee.pearljam.api.service.MailRequestService;

public class MailRequestServiceImpl implements MailRequestService {

    @Autowired
    MailRequestRepository mailRequestRepository;

    @Override
    public MailRequestDto updateMailRequest(Long id, MailRequestStatus newStatus) throws NotFoundException {
        if (!mailRequestRepository.existsById(id)) {
            throw new NotFoundException("No mail request found for id : " + id);
        }
        mailRequestRepository.updateStatus(newStatus, id);
        return mailRequestRepository.findDtoById(id);

    }

    @Override
    public boolean sendMailRequest(Long id) {
        // TODO Auto-generated method stub
        return false;
    }

}
