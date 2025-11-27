package fr.insee.pearljam.api.crossenvironmentcommunication.broker;

import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.EventDto;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.EventPayloadDto;
import fr.insee.pearljam.domain.crossenvironmentcommunication.port.userside.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatusMessageConsumer implements MessageConsumer {
    private final StatusService statusService;

    @Override
    public boolean shouldConsume(EventDto.EventTypeEnum type) {
        return true;
    }

    @Override
    public void consume(EventDto.EventTypeEnum type, EventPayloadDto payload) {
        this.statusService.updateStatus(payload.getInterrogationId(), type.getValue());
    }
}