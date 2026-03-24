package fr.insee.pearljam.infrastructure.persistence.message.adapter;

import fr.insee.pearljam.contracts.message.dto.MessageDto;
import fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.infrastructure.persistence.message.entity.MessageDB;
import fr.insee.pearljam.domain.message.port.out.MessageRepository;
import fr.insee.pearljam.infrastructure.persistence.message.jpa.MessageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MessageDaoAdapter implements MessageRepository {
    private final MessageJpaRepository messageJpaRepository;

    @Override
    public Optional<MessageDB> findById(Long id) {
        return messageJpaRepository.findById(id);
    }

    @Override
    public MessageDB save(MessageDB message) {
        return messageJpaRepository.save(message);
    }

    @Override
    public List<Long> getMessageIdsByInterviewer(String interviewerId) {
        return messageJpaRepository.getMessageIdsByInterviewer(interviewerId);
    }

    @Override
    public List<Long> getMessageIdsByOrganizationUnit(List<String> organizationUnitIds) {
        return messageJpaRepository.getMessageIdsByOrganizationUnit(organizationUnitIds);
    }

    @Override
    public List<MessageDto> findMessagesDtoByIds(List<Long> ids) {
        return messageJpaRepository.findMessagesDtoByIds(ids);
    }

    @Override
    public List<String> getMessageStatus(Long messageId, String interviewerId) {
        return messageJpaRepository.getMessageStatus(messageId, interviewerId);
    }

    @Override
    public List<Long> getAllOrganizationMessagesIds(List<String> organizationUnitIds) {
        return messageJpaRepository.getAllOrganizationMessagesIds(organizationUnitIds);
    }

    @Override
    public List<VerifyNameResponseDto> getCampaignRecipients(Long messageId) {
        return messageJpaRepository.getCampaignRecipients(messageId);
    }

    @Override
    public List<VerifyNameResponseDto> getOuRecipients(Long messageId) {
        return messageJpaRepository.getOuRecipients(messageId);
    }

    @Override
    public void deleteCampaignMessageRecipientByCampaignId(String campaignId) {
        messageJpaRepository.deleteCampaignMessageRecipientByCampaignId(campaignId);
    }

    @Override
    public void deleteOUMessageRecipientByOrganizationUnitId(String organizationUnitId) {
        messageJpaRepository.deleteOUMessageRecipientByOrganizationUnitId(organizationUnitId);
    }

    @Override
    public void deleteCampaignMessageRecipientByMessageId(Long messageId) {
        messageJpaRepository.deleteCampaignMessageRecipientByMessageId(messageId);
    }

    @Override
    public void deleteOUMessageRecipientByMessageId(Long messageId) {
        messageJpaRepository.deleteOUMessageRecipientByMessageId(messageId);
    }

    @Override
    public List<MessageDB> findAllBySenderId(String userId) {
        return messageJpaRepository.findAllBySenderId(userId);
    }

    @Override
    public void deleteAll(List<MessageDB> messages) {
        messageJpaRepository.deleteAll(messages);
    }
}
