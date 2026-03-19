package fr.insee.pearljam.domain.surveyunit.service.dummy;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCountProjection;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class CommunicationRequestFakeRepository implements CommunicationRequestRepository {

  private List<CommunicationRequest> communicationRequestsAdded;

  @Override
  public void addCommunicationRequests(SurveyUnitDB surveyUnit,
                                       List<CommunicationRequest> communicationRequests) {
    communicationRequestsAdded = communicationRequests;
  }

  @Override
  public Long getCommRequestCountByCampaignAndType(String campaignId,
      CommunicationType type, Long date) {
    return 0L;
  }

  @Override
  public List<InterviewerCountDto> getCommRequestCountByInterviewersAndType(
      List<String> campaignIds, Set<String> interviewerId, CommunicationType type,
      List<String> ouIds, Long date) {
    return List.of();
  }

  @Override
  public List<CommunicationRequestCountProjection> getCommRequestCountByCampaigns(List<String> campaignIds, List<String> ouIds, Long date) {
    return List.of();
  }

  @Override
  public List<CommunicationRequestCountProjection> getCommRequestCountByCampaignAndOus(String campaignId, List<String> ouIds, Long dateToUse) {
    return List.of();
  }


}
