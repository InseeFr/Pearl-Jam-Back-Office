package fr.insee.pearljam.domain.message.port.in;

import java.util.List;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.contracts.message.dto.MessageDto;

/**
 * Service for the Message entity
 * @author scorcaud
 *
 */
public interface MessageService {
  HttpStatus markAsRead(Long id, String idep);
  HttpStatus markAsDeleted(Long id, String idep);
  List<MessageDto> getMessages(String interviewerId);
  void deleteMessageByUserId(String userId);

}
