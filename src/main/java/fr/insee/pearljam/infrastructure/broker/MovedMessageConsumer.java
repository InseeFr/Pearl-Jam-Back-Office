package fr.insee.pearljam.infrastructure.broker;

import fr.insee.pearljam.domain.surveyunit.port.userside.MovedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovedMessageConsumer implements MessageConsumer {
    private final MovedService movedService;

    @Override
    public boolean shouldConsume(String type) {
        return type.equalsIgnoreCase("MULTIMODE_MOVED");
    }

    @Override
    public void consume(String type, BrokerMessage.Payload payload) {
        this.movedService.updateMovedSurveyUnit(payload.interrogationId());
    }
}
