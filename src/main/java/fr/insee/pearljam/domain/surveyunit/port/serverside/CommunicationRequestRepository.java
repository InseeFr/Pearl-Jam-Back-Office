package fr.insee.pearljam.domain.surveyunit.port.serverside;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import java.util.List;
import java.util.Set;

public interface CommunicationRequestRepository {

  /**
   * Add communication requests to a survey unit
   *
   * @param surveyUnit            survey unit to update
   * @param communicationRequests communication requests to add
   */
  void addCommunicationRequests(SurveyUnit surveyUnit,
      List<CommunicationRequest> communicationRequests);

  Long getCommunicationRequestCountByCampaignAndCommunicationType(String campaignId, CommunicationType type, Long date);

  Long getCommunicationRequestCountByCampaignAndCommunicationTypeAndOrgaUnitId(String campaignId, CommunicationType type, Long date, List<String> ouIds);

  List<InterviewerCountDto> getCommunicationRequestCountByInterviewersAndCommunicationType(List<String> campaignIds, Set<String> interviewerId, CommunicationType type, List<String> ouIds, Long date);
}
