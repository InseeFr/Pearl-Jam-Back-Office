package fr.insee.pearljam.infrastructure.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventPulsarConsumer {
    private final List<MessageConsumer> consumers;

    @PulsarListener(id="pearl-listener", topics = "dbserver1.public.outbox")
    public void listen(byte[] raw){

        var nodeString = new String(raw);


        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode node1 = (ObjectNode) mapper.readTree(nodeString);

            ObjectNode node = (ObjectNode) mapper.readTree(node1.get("after").get("payload").asText());
            String type = node.get("type").asText();

            consumers.forEach(c -> {
                if(c.shouldConsume(type)){
                    c.consume(type, node.get("payload"));
                }
            });

        } catch (JsonProcessingException e) {
            log.error("Error when processing Pulsar Json message");
        }
    }
}