package fr.insee.pearljam.domain.message.port.serverside;

import fr.insee.pearljam.api.message.dto.MessageDto;
import fr.insee.pearljam.api.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.domain.message.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    Optional<Message> findById(Long id);

    Message save(Message message);

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

    List<Message> findAllBySenderId(String userId);

    void deleteAll(List<Message> messages);
}
