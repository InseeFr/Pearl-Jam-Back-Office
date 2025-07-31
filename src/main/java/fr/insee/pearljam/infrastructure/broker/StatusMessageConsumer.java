package fr.insee.pearljam.infrastructure.broker;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pearljam.domain.surveyunit.port.state.StatusService;
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
        return type.equals("QUESTIONNAIRE_INIT") || type.equals("QUESTIONNAIRE_COMPLETED");
    }

    @Override
    public void consume(String type, JsonNode payload) {
        String id = payload.get("interrogationId").asText();
        this.statusService.updateStatus(id, type);
    }
}
