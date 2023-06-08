package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.communication.CommunicationRequest;
import fr.insee.pearljam.api.domain.communication.CommunicationRequestStatus;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestDto;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestStatusDto;
import fr.insee.pearljam.api.exception.NotFoundException;

@Service
public interface CommunicationRequestService {

    /**
     * If exist, update status of targetted mailRequest
     * 
     * @param id
     * @param newStatus
     * @return Updated MailRequestDto
     * @throws NotFoundException
     */
    public CommunicationRequestDto updateCommunicationRequest(Long id, CommunicationRequestStatus newStatus)
            throws NotFoundException;

    /**
     * Try to send an unprocessed mailRequest
     * 
     * @param id
     * @return
     */
    public boolean sendMailRequest(Long id);

    /**
     * Return last status of CommunicationRequest determined by higher date value
     * Return null if no CommunicationRequestStatus
     */
    public CommunicationRequestStatus getLastStatus(CommunicationRequest commRequest);

    public List<CommunicationRequestStatusDto> getStatusList(Long commRequestId);

    public void deleteBySurveyUnitId(String id);
}
