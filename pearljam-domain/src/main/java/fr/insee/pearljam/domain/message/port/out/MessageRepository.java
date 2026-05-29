package fr.insee.pearljam.domain.message.port.out;

import fr.insee.pearljam.contracts.message.dto.MessageDto;
import fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.infrastructure.persistence.message.entity.MessageDB;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    Optional<MessageDB> findById(Long id);

    MessageDB save(MessageDB message);

    List<Long> getMessageIdsByInterviewer(String interviewerId);

    List<Long> getMessageIdsByOrganizationUnit(List<String> organizationUnitIds);

    List<MessageDto> findMessagesDtoByIds(List<Long> ids);

    List<String> getMessageStatus(Long messageId, String interviewerId);

    List<Long> getAllOrganizationMessagesIds(List<String> organizationUnitIds);

    List<VerifyNameResponseDto> getCampaignRecipients(Long messageId);

    List<VerifyNameResponseDto> getOuRecipients(Long messageId);

    void deleteCampaignMessageRecipientByCampaignId(String campaignId);

    void deleteOUMessageRecipientByOrganizationUnitId(String organizationUnitId);

    void deleteCampaignMessageRecipientByMessageId(Long messageId);

    void deleteOUMessageRecipientByMessageId(Long messageId);

    List<MessageDB> findAllBySenderId(String userId);

    void deleteAll(List<MessageDB> messages);
}
