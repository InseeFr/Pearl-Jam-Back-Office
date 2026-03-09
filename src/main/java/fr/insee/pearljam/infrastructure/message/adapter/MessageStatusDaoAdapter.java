package fr.insee.pearljam.infrastructure.message.adapter;

import fr.insee.pearljam.domain.message.model.MessageStatus;
import fr.insee.pearljam.domain.message.port.serverside.MessageStatusRepository;
import fr.insee.pearljam.infrastructure.message.jpa.MessageStatusJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageStatusDaoAdapter implements MessageStatusRepository {
    private final MessageStatusJpaRepository messageStatusJpaRepository;

    @Override
    public void delete(MessageStatus messageStatus) {
        messageStatusJpaRepository.delete(messageStatus);
    }
}
