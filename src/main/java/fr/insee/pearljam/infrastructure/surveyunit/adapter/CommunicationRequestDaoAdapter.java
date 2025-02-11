package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.exception.CommunicationTemplateNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationRequestJpaRepository;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationTemplateJpaRepository;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CommunicationRequestDaoAdapter implements CommunicationRequestRepository {

  private final SurveyUnitRepository surveyUnitRepository;
  private final CommunicationTemplateJpaRepository communicationTemplateRepository;
  private final CommunicationRequestJpaRepository communicationRequestRepository;

  @Override
  @Transactional
  public void addCommunicationRequests(SurveyUnit surveyUnit,
      List<CommunicationRequest> communicationRequests) {
    List<CommunicationRequestDB> newCommunicationRequests = new ArrayList<>();
    for (CommunicationRequest communicationRequest : communicationRequests) {
      CommunicationTemplateDB communicationTemplate = communicationTemplateRepository
          .findCommunicationTemplate(communicationRequest.communicationTemplateId(),
              surveyUnit.getCampaign().getId())
          .orElseThrow(CommunicationTemplateNotFoundException::new);
      CommunicationRequestDB newCommunicationRequest = CommunicationRequestDB.fromModel(
          communicationRequest, surveyUnit, communicationTemplate);
      newCommunicationRequests.add(newCommunicationRequest);
    }

    Set<CommunicationRequestDB> currentCommunicationRequests = surveyUnit.getCommunicationRequests();
    currentCommunicationRequests.addAll(newCommunicationRequests);
    surveyUnitRepository.save(surveyUnit);
  }

  @Override
  public Long getCommunicationRequestCountByCampaignAndCommunicationType(String campaignId,
      CommunicationType type, Long date) {


    return communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationType(
        campaignId, type, date);
  }

  @Override
  public Long getCommunicationRequestCountByCampaignAndCommunicationTypeByOU(String campaignId,
      CommunicationType type, Long date, List<String> ouIds) {

    return communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeByOU(
        campaignId, type, date, ouIds);
  }

  @Override
  public List<InterviewerCountDto> getCommunicationRequestCountByInterviewersAndCommunicationType(List<String> campaignIds,
      Set<String> interviewersId, CommunicationType type, List<String> ouIds, Long date) {

    return communicationRequestRepository.getCommunicationRequestCountByInterviewersAndCommunicationType(
        campaignIds, interviewersId, type, ouIds, date);
  }

}
