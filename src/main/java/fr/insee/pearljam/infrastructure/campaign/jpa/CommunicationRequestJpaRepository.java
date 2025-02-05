package fr.insee.pearljam.infrastructure.campaign.jpa;

import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunicationRequestJpaRepository extends
    JpaRepository<CommunicationRequestDB, Long> {

  @Query("""
      SELECT COUNT(ct) FROM communication_request ct
      INNER JOIN ct.communicationTemplate cr
      INNER JOIN ct.status crs
      WHERE cr.campaign.id = :campaignId
      AND cr.type = :type
      AND crs.status IN :statusList
      """)
  Long getCommunicationRequestCountByCampaignAndCommunicationType(String campaignId,
      CommunicationType type, List<CommunicationStatusType> statusList);


  @Query("""
      SELECT new fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto(i.id, COUNT(ct))
      FROM communication_request ct
      INNER JOIN ct.surveyUnit su
      INNER JOIN su.interviewer i
      INNER JOIN ct.communicationTemplate cr
      INNER JOIN ct.status crs
      WHERE i.id IN :interviewerIds
      AND cr.type = :type
      AND crs.status IN :statusList
      GROUP BY i.id
      """)
  List<InterviewerCountDto> getCommunicationRequestCountByInterviewersAndCommunicationType(
      Set<String> interviewerIds, CommunicationType type, List<CommunicationStatusType> statusList);


}
