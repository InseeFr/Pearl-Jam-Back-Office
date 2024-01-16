package fr.insee.pearljam.api.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.communication.CommunicationRequest;
import fr.insee.pearljam.api.domain.communication.CommunicationRequestStatus;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestDto;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestStatusDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.CommunicationRequestRepository;
import fr.insee.pearljam.api.service.CommunicationRequestService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunicationRequestServiceImpl implements CommunicationRequestService {

    private final CommunicationRequestRepository communicationRequestRepository;

    @Override
    public CommunicationRequestDto updateCommunicationRequest(Long id, CommunicationRequestStatus newStatus)
            throws NotFoundException {
        Optional<CommunicationRequest> commRequestOpt = communicationRequestRepository.findById(id);
        if (!commRequestOpt.isPresent()) {
            throw new NotFoundException("No mail request found for id : " + id);
        }
        CommunicationRequest commRequest = commRequestOpt.get();
        commRequest.getStatus().add(newStatus);
        return communicationRequestRepository.findDtoById(id);

    }

    @Override
    public boolean sendMailRequest(Long id) {
        // TODO waiting for Messhugah specifications
        return false;
    }

    @Override
    public CommunicationRequestStatus getLastStatus(CommunicationRequest commRequest) {
        return commRequest.getStatus().stream()
                .sorted((status1, status2) -> Long.compare(status1.getDate(), status2.getDate()))
                .findFirst().orElse(null);
    }

    @Override
    public List<CommunicationRequestStatusDto> getStatusList(Long commRequestId) {
        return communicationRequestRepository.findDtoById(commRequestId).getStatus();
    }

    @Override
    public void deleteBySurveyUnitId(String id) {
        communicationRequestRepository.deleteBySurveyUnitId(id);
    }

}
