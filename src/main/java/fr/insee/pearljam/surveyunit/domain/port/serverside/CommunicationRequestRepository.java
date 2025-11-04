package fr.insee.pearljam.surveyunit.domain.port.serverside;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.campaign.domain.model.communication.CommunicationType;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequest;
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

  Long getCommRequestCountByCampaignAndType(String campaignId, CommunicationType type, Long date);

  Long getCommRequestCountByCampaignTypeAndOrgaUnit(String campaignId, CommunicationType type, Long date, List<String> ouIds);

  List<InterviewerCountDto> getCommRequestCountByInterviewersAndType(List<String> campaignIds, Set<String> interviewerId, CommunicationType type, List<String> ouIds, Long date);
}
