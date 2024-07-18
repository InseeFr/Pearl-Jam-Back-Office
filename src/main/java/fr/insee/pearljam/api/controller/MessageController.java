package fr.insee.pearljam.api.controller;

import java.util.List;

import fr.insee.pearljam.infrastructure.mail.MailSender;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import fr.insee.pearljam.infrastructure.mail.exception.SendMailException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import fr.insee.pearljam.api.message.dto.MailDto;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.dto.message.VerifyNameResponseDto;
import fr.insee.pearljam.api.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api")
@Tag(name = "12. Messages", description = "Endpoints for messages")
@Slf4j
@RequiredArgsConstructor
@Validated
public class MessageController{

	private final MessageService messageService;
	private final SimpMessagingTemplate brokerMessagingTemplate;
	private final AuthenticatedUserService authenticatedUserService;
	private final MailSender mailSender;

	/**
	 * This method is used to post a message
	 */
	@Operation(summary = "Post a message")
	@PostMapping(path = "/message")
	public ResponseEntity<Object> postMessage(@RequestBody MessageDto message) {
		String userId = authenticatedUserService.getCurrentUserId();
		String text = message.getText();
		List<String> recipients = message.getRecipients();
		log.info("POST text '{}' ", text);
		for (String recipient : recipients) {
			log.info("POST recipient '{}' ", recipient);
		}
		HttpStatus returnCode = messageService.addMessage(text, recipients, userId);
		return new ResponseEntity<>(returnCode);

	}

	/**
	 * This method is used to mark a message as read with id: {id} as read for the
	 * interviewer {idep}
	 */
	@Operation(summary = "Mark a message as read")
	@PutMapping(path = "/message/{id}/interviewer/{idep}/read")
	public ResponseEntity<Object> postMessage(
			@PathVariable(value = "id") Long id,
			@PathVariable(value = "idep") String idep) {
		HttpStatus returnCode = messageService.markAsRead(id, idep);
		if (returnCode == HttpStatus.OK) {
			this.brokerMessagingTemplate.convertAndSend("/notifications/".concat(idep.toUpperCase()),
					"new message");
		}
		return new ResponseEntity<>(returnCode);
	}

	/**
	 * This method is used to mark a message as deleted with id: {id} as read for
	 * the interviewer {idep}
	 */
	@Operation(summary = "Mark a message as deleted")
	@PutMapping(path = "/message/{id}/interviewer/{idep}/delete")
	public ResponseEntity<Object> postDeletedMessage(
			@PathVariable(value = "id") Long id,
			@PathVariable(value = "idep") String idep) {
		HttpStatus returnCode = messageService.markAsDeleted(id, idep);
		if (returnCode == HttpStatus.OK) {
			this.brokerMessagingTemplate.convertAndSend("/notifications/".concat(idep.toUpperCase()),
					"new message");
		}
		return new ResponseEntity<>(returnCode);
	}

	/**
	 * Retrieves messages sent to the interviewer with id {id}
	 */
	@Operation(summary = "Get a message")
	@GetMapping(path = "/messages/{id}")
	public ResponseEntity<List<MessageDto>> getMessages(@PathVariable(value = "id") String id) {
		List<MessageDto> messages = messageService.getMessages(id);
		return new ResponseEntity<>(messages, HttpStatus.OK);
	}

	/**
	 * Retrieves message history
	 */
	@Operation(summary = "Get the message history")
	@GetMapping(path = "/message-history")
	public ResponseEntity<List<MessageDto>> getMessageHistory() {
		String userId = authenticatedUserService.getCurrentUserId();
		List<MessageDto> messages = messageService.getMessageHistory(userId);
		return new ResponseEntity<>(messages, HttpStatus.OK);
	}

	/**
	 * Retrieves matching interviewers and campaigns
	 */
	@Operation(summary = "Update Messages with campaigns or interviewers listed in request body")
	@PostMapping(path = "/verify-name")
	public ResponseEntity<Object> postMessage(@RequestBody WsText name) {
		String userId = authenticatedUserService.getCurrentUserId();
		String text = name.getText();
		List<VerifyNameResponseDto> resp = messageService.verifyName(text, userId);
		if (resp != null) {
			return new ResponseEntity<>(resp, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 *
	 * @param mail mail to send
	 * @throws SendMailException exception thrown when sending mail problems
	 */
	@Operation(summary = "Post a mail to admins")
	@PostMapping(path = "/mail")
	public void postMailMessage(@Valid @RequestBody MailDto mail) throws SendMailException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("User {} send a mail", userId);
		mailSender.sendMail(mail.subject(), mail.content());
	}
}
