package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCountProjection;
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
  void addCommunicationRequests(SurveyUnitDB surveyUnit,
                                List<CommunicationRequest> communicationRequests);

  Long getCommRequestCountByCampaignAndType(String campaignId, CommunicationType type, Long date);

  List<InterviewerCountDto> getCommRequestCountByInterviewersAndType(List<String> campaignIds, Set<String> interviewerId, CommunicationType type, List<String> ouIds, Long date);

  List<CommunicationRequestCountProjection> getCommRequestCountByCampaigns(
          List<String> campaignIds,
          List<String> ouIds,
          Long date);

  List<CommunicationRequestCountProjection> getCommRequestCountByCampaignAndOus(String campaignId,
                                                                                List<String> ouIds,
                                                                                Long dateToUse);

}
