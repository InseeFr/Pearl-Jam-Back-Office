package fr.insee.pearljam.message.domain.port.userside;

import java.util.List;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.message.infrastructure.rest.dto.MessageDto;
import fr.insee.pearljam.message.infrastructure.rest.dto.VerifyNameResponseDto;

/**
 * Service for the Message entity
 * @author scorcaud
 *
 */
public interface MessageService {


	/**
	 * Update the Message by Id and UserId with the MessageDetailDto passed in parameter
	 * @param userId
	 * @param id
	 * @param MessageDetailDto
	 * @return HttpStatus
	 */
	
  HttpStatus addMessage(String text, List<String> recipients, String userId);
  HttpStatus markAsRead(Long id, String idep);
  HttpStatus markAsDeleted(Long id, String idep);
  List<MessageDto> getMessages(String interviewerId);
  List<VerifyNameResponseDto> verifyName(String text, String userId);
  List<MessageDto> getMessageHistory(String userId);
  void deleteMessageByUserId(String userId);

}
