package fr.insee.pearljam.api.crossenvironmentcommunication.broker;


import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.EventDto;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.EventPayloadDto;

public interface MessageConsumer {
    boolean shouldConsume(EventDto.EventTypeEnum type);
    void consume(EventDto.EventTypeEnum type, EventPayloadDto payload);
}
