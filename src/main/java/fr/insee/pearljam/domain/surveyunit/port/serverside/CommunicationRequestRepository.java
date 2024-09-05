package fr.insee.pearljam.domain.surveyunit.port.serverside;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;

import java.util.List;

public interface CommunicationRequestRepository {
    /**
     * Add communication requests to a survey unit
     * @param surveyUnit survey unit to update
     * @param communicationRequests communication requests to add
     */
    void addCommunicationRequests(SurveyUnit surveyUnit, List<CommunicationRequest> communicationRequests);
}
