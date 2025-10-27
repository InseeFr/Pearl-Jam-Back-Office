package fr.insee.pearljam.infrastructure.broker;

import fr.insee.pearljam.domain.surveyunit.port.userside.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatusMessageConsumer implements MessageConsumer {
    private final StatusService statusService;

    @Override
    public boolean shouldConsume(String type) {
        return true;
    }

    @Override
    public void consume(String type, BrokerMessage.Payload payload) {
        this.statusService.updateStatus(payload.interrogationId(), type);
    }
}
