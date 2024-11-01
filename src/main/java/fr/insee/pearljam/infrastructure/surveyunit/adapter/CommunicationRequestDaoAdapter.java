package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.exception.CommunicationTemplateNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationTemplateJpaRepository;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommunicationRequestDaoAdapter implements CommunicationRequestRepository {
    private final SurveyUnitRepository surveyUnitRepository;
    private final CommunicationTemplateJpaRepository communicationTemplateRepository;

    @Override
    @Transactional
    public void addCommunicationRequests(SurveyUnit surveyUnit, List<CommunicationRequest> communicationRequests) {
        List<CommunicationRequestDB> newCommunicationRequests = new ArrayList<>();
        for(CommunicationRequest communicationRequest: communicationRequests) {
            CommunicationTemplateDB communicationTemplate = communicationTemplateRepository
                    .findCommunicationTemplate(communicationRequest.communicationTemplateId(), surveyUnit.getCampaign().getId())
                    .orElseThrow(CommunicationTemplateNotFoundException::new);
            CommunicationRequestDB newCommunicationRequest = CommunicationRequestDB.fromModel(communicationRequest, surveyUnit, communicationTemplate);
            newCommunicationRequests.add(newCommunicationRequest);
        }

        Set<CommunicationRequestDB> currentCommunicationRequests = surveyUnit.getCommunicationRequests();
        currentCommunicationRequests.addAll(newCommunicationRequests);
        surveyUnitRepository.save(surveyUnit);
    }
}
