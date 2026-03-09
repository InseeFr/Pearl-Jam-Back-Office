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
import fr.insee.pearljam.api.message.dto.MessageDto;
import fr.insee.pearljam.api.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
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
	private final UserRepository userRepository;
	private final UserService userService;
	private final InterviewerRepository interviewerRepository;
	private final CampaignRepository campaignRepository;
	private final OrganizationUnitRepository organizationUnitRepository;
	private final SimpMessagingTemplate brokerMessagingTemplate;

	private static final String NOTIFICATIONS = "/notifications/";

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

	public HttpStatus addMessage(String text, List<String> recipients, String userId) {
		Optional<UserDB> optSender = userRepository.findByIdIgnoreCase(userId);
		UserDB sender;
		ArrayList<OrganizationUnitDB> ouMessageRecipients = new ArrayList<>();
		ArrayList<InterviewerDB> interviewerMessageRecipients = new ArrayList<>();
		ArrayList<CampaignDB> campaignMessageRecipients = new ArrayList<>();
		List<String> userOUIds = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId).collect(Collectors.toList());

		if (optSender.isPresent()) {
			sender = optSender.get();
		} else {
			log.warn("Message sender is null");
			sender = null;
		}
		MessageDB message = new MessageDB(text, sender, System.currentTimeMillis());

		for (String recipient : recipients) {
			if (recipient.equalsIgnoreCase("All") || recipient.equalsIgnoreCase("Tous")) {
				for (String OUId : userOUIds) {
					Optional<OrganizationUnitDB> ouRecipient = organizationUnitRepository.findByIdIgnoreCase(OUId);
					if (ouRecipient.isEmpty()) {
						return HttpStatus.BAD_REQUEST;

					}
					ouMessageRecipients.add(ouRecipient.get());
				}
			} else {
				Optional<CampaignDB> camp = campaignRepository.findByIdIgnoreCase(recipient);
				if (camp.isPresent()) {
					campaignMessageRecipients.add(camp.get());
					interviewerMessageRecipients.addAll(
							interviewerRepository.findInterviewersWorkingOnCampaign(camp.get().getId(), userOUIds));
				} else {
					String errMsg = String.format("Campaign message recipient %s was not found in database", recipient);
					log.error(errMsg);
					return HttpStatus.BAD_REQUEST;
				}

			}

		}

		List<InterviewerDB> uniqueInterviwerRecipients = interviewerMessageRecipients.stream()
				.collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(InterviewerDB::getId))),
						ArrayList::new));
		message.setOuMessageRecipients(ouMessageRecipients);
		message.setCampaignMessageRecipients(campaignMessageRecipients);

		for (InterviewerDB recipient : uniqueInterviwerRecipients) {
			log.info("push to '{}' ", NOTIFICATIONS.concat(recipient.getId().toUpperCase()));
			this.brokerMessagingTemplate.convertAndSend(NOTIFICATIONS.concat(recipient.getId().toUpperCase()),
					"new message");
		}

		for (OrganizationUnitDB recipient : ouMessageRecipients) {
			log.info("push to '{}' ", NOTIFICATIONS.concat(recipient.getId().toUpperCase()));
			this.brokerMessagingTemplate.convertAndSend(NOTIFICATIONS.concat(recipient.getId().toUpperCase()),
					"new message");
		}

		messageRepository.save(message);
		return HttpStatus.OK;
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

	public List<MessageDto> getMessageHistory(String userId) {
		List<String> userOUIds = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId).collect(Collectors.toList());
		List<Long> messageIds = messageRepository.getAllOrganizationMessagesIds(userOUIds);

		List<MessageDto> messages = messageRepository.findMessagesDtoByIds(messageIds);
		for (MessageDto message : messages) {
			List<VerifyNameResponseDto> recipients = messageRepository.getCampaignRecipients(message.getId());

			recipients.addAll(
					messageRepository.getOuRecipients(message.getId()));

			message.setTypedRecipients(recipients);

		}

		return messages;
	}

	public List<VerifyNameResponseDto> verifyName(String text, String userId) {
        List<String> userOUIds = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId).collect(Collectors.toList());
		Pageable topFifteen = PageRequest.of(0, 15);

        List<VerifyNameResponseDto> returnValue = new ArrayList<>(campaignRepository.findMatchingCampaigns(text, userOUIds, System.currentTimeMillis(), topFifteen));

		return returnValue.stream()
				.collect(
						collectingAndThen(
								toCollection(() -> new TreeSet<>(Comparator.comparing(VerifyNameResponseDto::id))),
								ArrayList::new));
	}

	@Override
	@Transactional
	public void deleteMessageByUserId(String userId) {
		List<MessageDB> lstMessage = messageRepository.findAllBySenderId(userId);
		lstMessage.stream().forEach(msg -> {
			messageRepository.deleteCampaignMessageRecipientByMessageId(msg.getId());
			messageRepository.deleteOUMessageRecipientByMessageId(msg.getId());
			msg.getMessageStatus().stream().forEach(messageStatusRepository::delete);
		});
		messageRepository.deleteAll(lstMessage);
	}

}
