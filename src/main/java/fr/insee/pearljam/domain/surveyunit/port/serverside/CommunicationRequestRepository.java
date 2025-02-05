package fr.insee.pearljam.domain.surveyunit.port.serverside;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import java.util.List;
import java.util.Set;
import org.springframework.data.repository.query.Param;

public interface CommunicationRequestRepository {

  /**
   * Add communication requests to a survey unit
   *
   * @param surveyUnit            survey unit to update
   * @param communicationRequests communication requests to add
   */
  void addCommunicationRequests(SurveyUnit surveyUnit,
      List<CommunicationRequest> communicationRequests);

  Long getCommunicationRequestCountByCampaignAndCommunicationType(
      @Param("campaignId") String campaignId,
      @Param("type") CommunicationType type);

  List<InterviewerCountDto> getCommunicationRequestCountByInterviewersAndCommunicationType(
      @Param("interviewersId") Set<String> interviewerId,
      @Param("type") CommunicationType type);
}
