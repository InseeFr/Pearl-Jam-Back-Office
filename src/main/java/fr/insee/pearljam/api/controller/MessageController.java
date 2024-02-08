package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.function.BiFunction;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.message.MailDto;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.dto.message.VerifyNameResponseDto;
import fr.insee.pearljam.api.service.MessageService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;
	private final BiFunction<String, String, HttpStatus> sendMail;
	private final UtilsService utilsService;
	private final SimpMessagingTemplate brokerMessagingTemplate;

	StompSessionHandler sessionHandler = new CustomStompSessionHandler();

	/**
	 * This method is used to post a message
	 */
	@ApiOperation(value = "Post a message")
	@PostMapping(path = "/message")
	public ResponseEntity<Object> postMessage(HttpServletRequest request, @RequestBody MessageDto message) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			if (!userId.equals(Constants.GUEST) && utilsService.existUser(userId, Constants.USER)
					&& !userId.equalsIgnoreCase(message.getSender())) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			String text = message.getText();
			List<String> recipients = message.getRecipients();
			log.info("POST text '{}' ", text);
			for (String recipient : recipients) {
				log.info("POST recipient '{}' ", recipient);
			}
			HttpStatus returnCode = messageService.addMessage(text, recipients, userId);
			return new ResponseEntity<>(returnCode);
		}
	}

	/**
	 * This method is used to mark a message as read with id: {id} as read for the
	 * interviewer {idep}
	 */
	@ApiOperation(value = "Mark a message as read")
	@PutMapping(path = "/message/{id}/interviewer/{idep}/read")
	public ResponseEntity<Object> postMessage(HttpServletRequest request, @PathVariable(value = "id") Long id,
			@PathVariable(value = "idep") String idep) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = messageService.markAsRead(id, idep);
			if (returnCode == HttpStatus.OK) {
				this.brokerMessagingTemplate.convertAndSend("/notifications/".concat(idep.toUpperCase()),
						"new message");
			}
			return new ResponseEntity<>(returnCode);
		}
	}

	/**
	 * This method is used to mark a message as deleted with id: {id} as read for
	 * the interviewer {idep}
	 */
	@ApiOperation(value = "Mark a message as deleted")
	@PutMapping(path = "/message/{id}/interviewer/{idep}/delete")
	public ResponseEntity<Object> postDeletedMessage(HttpServletRequest request, @PathVariable(value = "id") Long id,
			@PathVariable(value = "idep") String idep) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = messageService.markAsDeleted(id, idep);
			if (returnCode == HttpStatus.OK) {
				this.brokerMessagingTemplate.convertAndSend("/notifications/".concat(idep.toUpperCase()),
						"new message");
			}
			return new ResponseEntity<>(returnCode);
		}
	}

	/**
	 * Retrieves messages sent to the interviewer with id {id}
	 */
	@ApiOperation(value = "Get a message")
	@GetMapping(path = "/messages/{id}")
	public ResponseEntity<List<MessageDto>> getMessages(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<MessageDto> messages = messageService.getMessages(id);
			return new ResponseEntity<>(messages, HttpStatus.OK);
		}
	}

	/**
	 * Retrieves message history
	 */
	@ApiOperation(value = "Get the message history")
	@GetMapping(path = "/message-history")
	public ResponseEntity<List<MessageDto>> getMessageHistory(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<MessageDto> messages = messageService.getMessageHistory(userId);
			return new ResponseEntity<>(messages, HttpStatus.OK);
		}
	}

	/**
	 * Retrieves matching interviewers and campaigns
	 */
	@ApiOperation(value = "Update Messages with campaigns or interviewers listed in request body")
	@PostMapping(path = "/verify-name")
	public ResponseEntity<Object> postMessage(HttpServletRequest request, @RequestBody WsText name) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			String text = name.getText();
			List<VerifyNameResponseDto> resp = messageService.verifyName(text, userId);
			if (resp != null) {
				return new ResponseEntity<>(resp, HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method is used to post a mail
	 */
	@ApiOperation(value = "Post a mail to admins")
	@PostMapping(path = "/mail")
	public ResponseEntity<Object> postMailMessage(HttpServletRequest request, @RequestBody MailDto mail) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		log.info("User {} send a mail", userId);
		HttpStatus returnCode = sendMail.apply(mail.getContent(), mail.getSubject());
		return new ResponseEntity<>(returnCode);
	}
}
