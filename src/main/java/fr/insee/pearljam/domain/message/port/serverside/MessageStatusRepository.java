package fr.insee.pearljam.domain.message.port.serverside;

import fr.insee.pearljam.domain.message.model.MessageStatus;

public interface MessageStatusRepository {
    void delete(MessageStatus messageStatus);
}
