package fr.insee.pearljam.infrastructure.persistence.message.adapter;

import fr.insee.pearljam.infrastructure.persistence.message.entity.MessageStatusDB;
import fr.insee.pearljam.domain.message.port.out.MessageStatusRepository;
import fr.insee.pearljam.infrastructure.persistence.message.jpa.MessageStatusJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageStatusDaoAdapter implements MessageStatusRepository {
    private final MessageStatusJpaRepository messageStatusJpaRepository;

    @Override
    public void delete(MessageStatusDB messageStatus) {
        messageStatusJpaRepository.delete(messageStatus);
    }
}
