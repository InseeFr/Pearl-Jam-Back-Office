package fr.insee.pearljam.domain.message.service;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.infrastructure.persistence.message.entity.MessageDB;
import fr.insee.pearljam.infrastructure.persistence.message.entity.MessageStatusDB;
import fr.insee.pearljam.domain.message.model.MessageStatusType;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import fr.insee.pearljam.contracts.message.dto.MessageDto;
import fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.domain.message.port.out.MessageRepository;
import fr.insee.pearljam.domain.message.port.out.MessageStatusRepository;
import fr.insee.pearljam.domain.message.port.in.MessageService;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.organizationunit.port.out.UserRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final MessageStatusRepository messageStatusRepository;
	private final UserService userService;
	private final InterviewerRepository interviewerRepository;

	public HttpStatus markAsRead(Long id, String idep) {
		Optional<InterviewerDB> interv = interviewerRepository.findByIdIgnoreCase(idep);
		Optional<MessageDB> msg = messageRepository.findById(id);
		if (interv.isPresent() && msg.isPresent()) {
			log.info("trying to save");
			MessageDB message = msg.get();
			List<MessageStatusDB> statusList = message.getMessageStatus();
			if (statusList == null) {
				statusList = new ArrayList<>();
			} else {
				message.getMessageStatus().removeAll(statusList);
			}
			List<MessageStatusDB> newList = statusList.stream()
					.filter(c -> !c.getInterviewer().getId().equals(interv.get().getId()))
					.collect(Collectors.toList());
			newList.add(new MessageStatusDB(message, interv.get(), MessageStatusType.REA));
			message.setMessageStatus(newList);
			messageRepository.save(message);
			return HttpStatus.OK;
		}
		return HttpStatus.NOT_FOUND;
	}

	public HttpStatus markAsDeleted(Long id, String idep) {
		Optional<InterviewerDB> interv = interviewerRepository.findByIdIgnoreCase(idep);
		Optional<MessageDB> msg = messageRepository.findById(id);
		if (interv.isPresent() && msg.isPresent()) {
			log.info("trying to save");
			MessageDB message = msg.get();
			List<MessageStatusDB> statusList = message.getMessageStatus();
			if (statusList == null) {
				statusList = new ArrayList<>();
			} else {
				message.getMessageStatus().removeAll(statusList);
			}
			List<MessageStatusDB> newList = statusList.stream()
					.filter(c -> !c.getInterviewer().getId().equals(interv.get().getId()))
					.collect(Collectors.toList());
			newList.add(new MessageStatusDB(message, interv.get(), MessageStatusType.DEL));
			message.setMessageStatus(newList);
			messageRepository.save(message);
			return HttpStatus.OK;
		}
		return HttpStatus.NOT_FOUND;
	}

	public List<MessageDto> getMessages(String interviewerId) {
		List<Long> ids = messageRepository.getMessageIdsByInterviewer(interviewerId);
		List<OrganizationUnitDto> userOUs = userService.getUserOUs(interviewerId, true);
		List<String> ouIds = userOUs.stream().map(OrganizationUnitDto::getId).collect(Collectors.toList());
		List<Long> idsByOU = messageRepository.getMessageIdsByOrganizationUnit(ouIds);
		for (Long id : idsByOU) {
			if (!ids.contains(id)) {
				ids.add(id);
			}
		}
		List<MessageDto> messages = messageRepository.findMessagesDtoByIds(ids);
		List<MessageDto> messagesDeleted = new ArrayList<>();
		for (MessageDto message : messages) {
			List<String> status = messageRepository.getMessageStatus(message.getId(), interviewerId);
			if (!status.isEmpty()) {
				if (!status.getFirst().equals("REA")) {
					message.setStatus(status.getFirst());
				} else {
					messagesDeleted.add(message);
				}
			}
		}
		if (!messagesDeleted.isEmpty()) {
			messages.removeAll(messagesDeleted);
		}
		return messages;
	}
	
	@Override
	@Transactional
	public void deleteMessageByUserId(String userId) {
		List<MessageDB> lstMessage = messageRepository.findAllBySenderId(userId);
		lstMessage.forEach(msg -> {
			messageRepository.deleteCampaignMessageRecipientByMessageId(msg.getId());
			messageRepository.deleteOUMessageRecipientByMessageId(msg.getId());
			msg.getMessageStatus().forEach(messageStatusRepository::delete);
		});
		messageRepository.deleteAll(lstMessage);
	}

}
