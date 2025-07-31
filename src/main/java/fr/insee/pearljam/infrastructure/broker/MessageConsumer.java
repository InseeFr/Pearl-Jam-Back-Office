package fr.insee.pearljam.infrastructure.broker;

import com.fasterxml.jackson.databind.JsonNode;

public interface MessageConsumer {

    boolean shouldConsume(String type);

    void consume(String type, JsonNode payload);
}
