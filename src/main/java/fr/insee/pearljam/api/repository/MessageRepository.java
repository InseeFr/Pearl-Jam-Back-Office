package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.api.domain.Message;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.dto.message.VerifyNameResponseDto;

/**
* MessageRepository is the repository using to access to Message table in DB
* 
* @author Guillemet Paul
* 
*/
public interface MessageRepository extends JpaRepository<Message, Long> {

  Optional<Message> findById(Long id);
  
  @Query(value="select distinct(id) from message m "
    + "inner join interviewer_message_recipient imr "
    + "on m.id = imr.message_id "
    + "where imr.interviewer_id = ?1", nativeQuery=true)
    List<Long> getMessageIdsByInterviewer(String interviewerId);
  
  @Query(value="select distinct(id) from message m "
    + "inner join oumessage_recipient oumr "
    + "on m.id = oumr.message_id "
    + "where oumr.organization_unit_id IN (:organizationUnitIds)", nativeQuery=true)
    List<Long> getMessageIdsByOrganizationUnit(@Param("organizationUnitIds") List<String> organizationUnitIds);
  
  @Query(value="select distinct(m.id) from message m "
		    + "WHERE m.id IN (select distinct(m.id) from message m "
		    + "inner join interviewer_message_recipient imr "
		    + "on m.id = imr.message_id "
		    + "inner join interviewer interv "
		    + "on interv.id = imr.interviewer_id "
		    + "inner join survey_unit su "
		    + "on su.interviewer_id = interv.id "
		    + "where su.organization_unit_id IN (:organizationUnitIds)) "
		    + "OR m.id in (SELECT DISTINCT(oumr.message_id) FROM oumessage_recipient oumr WHERE oumr.organization_unit_id IN (:organizationUnitIds)) "
		    + "OR 'GUEST' IN (:organizationUnitIds) ", nativeQuery=true)
		    List<Long> getAllOrganizationMessagesIds(@Param("organizationUnitIds") List<String> organizationUnitIds);
	
	@Query("SELECT new fr.insee.pearljam.api.dto.message.MessageDto(m.id, m.text, m.sender.id, m.date) "
			  + "FROM Message m "
			  + "WHERE m.id in (:ids)")
    List<MessageDto> findMessagesDtoByIds(@Param("ids") List<Long> ids);
	
	@Query(value="select status from message_status "
			+ "where message_id = ?1 "
		    + "and interviewer_id = ?2", nativeQuery=true)
	List<String> getMessageStatus(Long messageId, String interviewerId);
	
	
	@Query("SELECT new fr.insee.pearljam.api.dto.message.VerifyNameResponseDto(interv.id,  'interviewer', concat(interv.firstName, ' ', interv.lastName)) "
			  + "FROM InterviewerMessageRecipient imr "
			  + "INNER JOIN Interviewer interv "
			  + "ON interv.id = imr.interviewer.id "
			  + "WHERE imr.message.id = :messageId ")
	List<VerifyNameResponseDto> getInterviewerRecipients(@Param("messageId") Long messageId);
	
	@Query("SELECT new fr.insee.pearljam.api.dto.message.VerifyNameResponseDto(camp.id,  'campaign', camp.label) "
			  + "FROM CampaignMessageRecipient cmr "
			  + "INNER JOIN Campaign camp "
			  + "ON camp.id = cmr.campaign.id "
			  + "WHERE cmr.message.id = :messageId ")
	List<VerifyNameResponseDto> getCampaignRecipients(@Param("messageId") Long messageId);
	
	@Query("SELECT new fr.insee.pearljam.api.dto.message.VerifyNameResponseDto(ou.id,  'organization', ou.label) "
			  + "FROM OUMessageRecipient oumr "
			  + "INNER JOIN OrganizationUnit ou "
			  + "ON ou.id = oumr.organizationUnit.id "
			  + "WHERE oumr.message.id = :messageId "
			  + "AND NOT EXISTS (SELECT 1 FROM OUMessageRecipient oumr2 "
			  + "WHERE oumr.organizationUnit.organizationUnitParent.id = oumr2.organizationUnit.id "
			  + "AND oumr2.message.id=:messageId )")
	List<VerifyNameResponseDto> getOuRecipients(@Param("messageId") Long messageId);

}
