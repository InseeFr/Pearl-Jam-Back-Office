package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.message.dto.MessageDto;
import fr.insee.pearljam.api.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.domain.message.model.Message;
import fr.insee.pearljam.domain.message.port.serverside.MessageRepository;

import java.util.List;
import java.util.Optional;

public class MessageFakeRepository implements MessageRepository {
    @Override
    public Optional<Message> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Message save(Message message) {
        return message;
    }

    @Override
    public List<Long> getMessageIdsByInterviewer(String interviewerId) {
        return List.of();
    }

    @Override
    public List<Long> getMessageIdsByOrganizationUnit(List<String> organizationUnitIds) {
        return List.of();
    }

    @Override
    public List<MessageDto> findMessagesDtoByIds(List<Long> ids) {
        return List.of();
    }

    @Override
    public List<String> getMessageStatus(Long messageId, String interviewerId) {
        return List.of();
    }

    @Override
    public List<Long> getAllOrganizationMessagesIds(List<String> organizationUnitIds) {
        return List.of();
    }

    @Override
    public List<VerifyNameResponseDto> getCampaignRecipients(Long messageId) {
        return List.of();
    }

    @Override
    public List<VerifyNameResponseDto> getOuRecipients(Long messageId) {
        return List.of();
    }

    @Override
    public void deleteCampaignMessageRecipientByCampaignId(String campaignId) {
        // not used at this moment
    }

    @Override
    public void deleteOUMessageRecipientByOrganizationUnitId(String organizationUnitId) {
        // not used at this moment
    }

    @Override
    public void deleteCampaignMessageRecipientByMessageId(Long messageId) {
        // not used at this moment
    }

    @Override
    public void deleteOUMessageRecipientByMessageId(Long messageId) {
        // not used at this moment
    }

    @Override
    public List<Message> findAllBySenderId(String userId) {
        return List.of();
    }

    @Override
    public void deleteAll(List<Message> messages) {
        // not used at this moment
    }
}
