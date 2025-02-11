package fr.insee.pearljam.domain.surveyunit.service.dummy;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommunicationRequestFakeRepository implements CommunicationRequestRepository {

  private List<CommunicationRequest> communicationRequestsAdded;

  @Override
  public void addCommunicationRequests(SurveyUnit surveyUnit,
      List<CommunicationRequest> communicationRequests) {
    communicationRequestsAdded = communicationRequests;
  }

  @Override
  public Long getCommunicationRequestCountByCampaignAndCommunicationType(String campaignId,
      CommunicationType type, Long date) {
    return 0L;
  }

  @Override
  public Long getCommunicationRequestCountByCampaignAndCommunicationTypeByOU(String campaignId,
      CommunicationType type, Long date, List<String> ouIds) {
    return 0L;
  }

  @Override
  public List<InterviewerCountDto> getCommunicationRequestCountByInterviewersAndCommunicationType(
      List<String> campaignIds, Set<String> interviewerId, CommunicationType type,
      List<String> ouIds, Long date) {
    return List.of();
  }


}
