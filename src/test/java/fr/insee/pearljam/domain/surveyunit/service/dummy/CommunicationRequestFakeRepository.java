package fr.insee.pearljam.domain.surveyunit.service.dummy;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommunicationRequestFakeRepository implements CommunicationRequestRepository {
    @Getter
    private List<CommunicationRequest> communicationRequestsAdded;

    @Override
    public void addCommunicationRequests(SurveyUnit surveyUnit, List<CommunicationRequest> communicationRequests) {
        communicationRequestsAdded = communicationRequests;
    }
}
