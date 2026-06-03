package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.campaign.service.exception.CommunicationTemplateNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationHistory;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCount;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CommunicationRequestJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CommunicationTemplateJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.CommunicationRequestDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommunicationRequestDaoAdapter implements CommunicationRequestRepository {

  private final SurveyUnitJpaRepository surveyUnitRepository;
  private final CommunicationTemplateJpaRepository communicationTemplateRepository;
  private final CommunicationRequestJpaRepository communicationRequestRepository;

  @Override
  @Transactional
  public void addCommunicationRequests(SurveyUnitDB surveyUnit,
                                       List<CommunicationRequest> communicationRequests) {
    List<CommunicationRequestDB> newCommunicationRequests = new ArrayList<>();
    for (CommunicationRequest communicationRequest : communicationRequests) {
      CommunicationTemplateDB communicationTemplate = communicationTemplateRepository
          .findCommunicationTemplate(communicationRequest.campaignId(), communicationRequest.meshuggahId())
          .orElseThrow(CommunicationTemplateNotFoundException::new);
      CommunicationRequestDB newCommunicationRequest = CommunicationRequestDB.fromModel(
          communicationRequest, surveyUnit, communicationTemplate );
      newCommunicationRequests.add(newCommunicationRequest);
    }

    Set<CommunicationRequestDB> currentCommunicationRequests = surveyUnit.getCommunicationRequests();
    currentCommunicationRequests.addAll(newCommunicationRequests);
    surveyUnitRepository.save(surveyUnit);
  }

  @Override
  public Long getCommRequestCountByCampaignAndType(String campaignId,
      CommunicationType type, Long date) {
    return communicationRequestRepository.getCommRequestCountByCampaignAndType(
        campaignId, type, date);
  }

  @Override
  public List<InterviewerCountDto> getCommRequestCountByInterviewersAndType(List<String> campaignIds,
      Set<String> interviewersId, CommunicationType type, List<String> ouIds, Long date) {

    return communicationRequestRepository.getCommRequestCountByInterviewersAndType(
        campaignIds, interviewersId, type, ouIds, date);
  }

  @Override
  public List<CommunicationRequestCount> getCommRequestCountByCampaigns(
          List<String> campaignIds,
          List<String> ouIds,
          Long date) {
    return communicationRequestRepository.getCommRequestCountByCampaigns(campaignIds, ouIds, date);
  }

  @Override
  public List<CommunicationRequestCount> getCommRequestCountByCampaignAndOus(String campaignId, List<String> ouIds, Long dateToUse) {
    return communicationRequestRepository.getCommRequestCountByCampaignAndOus(campaignId,ouIds,dateToUse);
  }

  @Override
  public List<CommunicationHistory> findAllDtoBySurveyUnitIdOrderByDateAsc(String surveyUnitId) {
    return communicationRequestRepository.getCommunicationsBySurveyUnitIdOrderByDateAsc(surveyUnitId);
  }

}
