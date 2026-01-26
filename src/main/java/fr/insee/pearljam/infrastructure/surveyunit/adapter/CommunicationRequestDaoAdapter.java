package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.count.model.CommunicationRequestCount;
import fr.insee.pearljam.domain.exception.CommunicationTemplateNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationRequestJpaRepository;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationTemplateJpaRepository;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
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
  private final CommunicationRequestJpaRepository communicationRequestRepository;

  @Override
  @Transactional
  public void addCommunicationRequests(SurveyUnit surveyUnit,
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
  public Long getCommRequestCountByCampaignTypeAndOrgaUnit(String campaignId,
      CommunicationType type, Long date, List<String> ouIds) {
    return communicationRequestRepository.getCommRequestCountByCampaignTypeAndOrgaUnit(
        campaignId, type, date, ouIds);
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

}
