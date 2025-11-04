package fr.insee.pearljam.message.domain.service;

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

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Campaign;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.Interviewer;
import fr.insee.pearljam.message.infrastructure.persistence.jpa.entity.Message;
import fr.insee.pearljam.message.infrastructure.persistence.jpa.entity.MessageStatus;
import fr.insee.pearljam.message.domain.model.MessageStatusType;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.OrganizationUnit;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.User;
import fr.insee.pearljam.message.infrastructure.rest.dto.MessageDto;
import fr.insee.pearljam.message.infrastructure.rest.dto.VerifyNameResponseDto;
import fr.insee.pearljam.organization.infrastructure.rest.dto.OrganizationUnitDto;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository.CampaignRepository;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.repository.InterviewerRepository;
import fr.insee.pearljam.message.infrastructure.persistence.jpa.repository.MessageRepository;
import fr.insee.pearljam.message.infrastructure.persistence.jpa.repository.MessageStatusRepository;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.repository.OrganizationUnitRepository;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.repository.UserRepository;
import fr.insee.pearljam.message.domain.port.userside.MessageService;
import fr.insee.pearljam.organization.domain.port.userside.UserService;
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

	public HttpStatus markAsRead(Long id, String idep) {
		Optional<Interviewer> interv = interviewerRepository.findByIdIgnoreCase(idep);
		Optional<Message> msg = messageRepository.findById(id);
		if (interv.isPresent() && msg.isPresent()) {
			log.info("trying to save");
			Message message = msg.get();
			List<MessageStatus> statusList = message.getMessageStatus();
			if (statusList == null) {
				statusList = new ArrayList<>();
			} else {
				message.getMessageStatus().removeAll(statusList);
			}
			List<MessageStatus> newList = statusList.stream()
					.filter(c -> !c.getInterviewer().getId().equals(interv.get().getId()))
					.collect(Collectors.toList());
			newList.add(new MessageStatus(message, interv.get(), MessageStatusType.REA));
			message.setMessageStatus(newList);
			messageRepository.save(message);
			return HttpStatus.OK;
		}
		return HttpStatus.NOT_FOUND;
	}

	public HttpStatus markAsDeleted(Long id, String idep) {
		Optional<Interviewer> interv = interviewerRepository.findByIdIgnoreCase(idep);
		Optional<Message> msg = messageRepository.findById(id);
		if (interv.isPresent() && msg.isPresent()) {
			log.info("trying to save");
			Message message = msg.get();
			List<MessageStatus> statusList = message.getMessageStatus();
			if (statusList == null) {
				statusList = new ArrayList<>();
			} else {
				message.getMessageStatus().removeAll(statusList);
			}
			List<MessageStatus> newList = statusList.stream()
					.filter(c -> !c.getInterviewer().getId().equals(interv.get().getId()))
					.collect(Collectors.toList());
			newList.add(new MessageStatus(message, interv.get(), MessageStatusType.DEL));
			message.setMessageStatus(newList);
			messageRepository.save(message);
			return HttpStatus.OK;
		}
		return HttpStatus.NOT_FOUND;
	}

	public HttpStatus addMessage(String text, List<String> recipients, String userId) {
		Optional<User> optSender = userRepository.findByIdIgnoreCase(userId);
		User sender;
		ArrayList<OrganizationUnit> ouMessageRecipients = new ArrayList<>();
		ArrayList<Interviewer> interviewerMessageRecipients = new ArrayList<>();
		ArrayList<Campaign> campaignMessageRecipients = new ArrayList<>();
		List<String> userOUIds = userService.getUserOUs(userId, true)
				.stream().map(ou -> ou.getId()).collect(Collectors.toList());

		if (optSender.isPresent()) {
			sender = optSender.get();
		} else {
			log.warn("Message sender is null");
			sender = null;
		}
		Message message = new Message(text, sender, System.currentTimeMillis());

		for (String recipient : recipients) {
			if (recipient.equalsIgnoreCase("All") || recipient.equalsIgnoreCase("Tous")) {
				for (String OUId : userOUIds) {
					Optional<OrganizationUnit> ouRecipient = organizationUnitRepository.findByIdIgnoreCase(OUId);
					if (ouRecipient.isEmpty()) {
						return HttpStatus.BAD_REQUEST;

					}
					ouMessageRecipients.add(ouRecipient.get());
				}
			} else {
				Optional<Campaign> camp = campaignRepository.findByIdIgnoreCase(recipient);
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

		List<Interviewer> uniqueInterviwerRecipients = interviewerMessageRecipients.stream()
				.collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(Interviewer::getId))),
						ArrayList::new));
		message.setOuMessageRecipients(ouMessageRecipients);
		message.setCampaignMessageRecipients(campaignMessageRecipients);

		messageRepository.save(message);
		return HttpStatus.OK;
	}

	public List<MessageDto> getMessages(String interviewerId) {
		List<Long> ids = messageRepository.getMessageIdsByInterviewer(interviewerId);
		List<OrganizationUnitDto> userOUs = userService.getUserOUs(interviewerId, true);
		List<String> ouIds = userOUs.stream().map(ou -> ou.getId()).collect(Collectors.toList());
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
				if (!status.get(0).equals("REA")) {
					message.setStatus(status.get(0));
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
				.stream().map(ou -> ou.getId()).collect(Collectors.toList());
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
		List<VerifyNameResponseDto> returnValue = new ArrayList<>();
		List<String> userOUIds = userService.getUserOUs(userId, true)
				.stream().map(ou -> ou.getId()).collect(Collectors.toList());
		Pageable topFifteen = PageRequest.of(0, 15);

		returnValue.addAll(
				campaignRepository.findMatchingCampaigns(text, userOUIds, System.currentTimeMillis(), topFifteen));

		return returnValue.stream()
				.collect(
						collectingAndThen(
								toCollection(() -> new TreeSet<>(Comparator.comparing(VerifyNameResponseDto::getId))),
								ArrayList::new));
	}

	@Override
	@Transactional
	public void deleteMessageByUserId(String userId) {
		List<Message> lstMessage = messageRepository.findAllBySenderId(userId);
		lstMessage.stream().forEach(msg -> {
			messageRepository.deleteCampaignMessageRecipientByMessageId(msg.getId());
			messageRepository.deleteOUMessageRecipientByMessageId(msg.getId());
			msg.getMessageStatus().stream().forEach(messageStatusRepository::delete);
		});
		messageRepository.deleteAll(lstMessage);
	}

}
