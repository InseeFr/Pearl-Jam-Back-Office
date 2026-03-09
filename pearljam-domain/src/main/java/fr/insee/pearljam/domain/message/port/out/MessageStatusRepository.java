package fr.insee.pearljam.domain.message.port.out;

import fr.insee.pearljam.infrastructure.persistence.message.entity.MessageStatusDB;

public interface MessageStatusRepository {
    void delete(MessageStatusDB messageStatus);
}
