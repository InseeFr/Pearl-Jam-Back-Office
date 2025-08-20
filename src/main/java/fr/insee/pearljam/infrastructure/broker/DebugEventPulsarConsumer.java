package fr.insee.pearljam.infrastructure.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.schema.GenericRecord;
import org.apache.pulsar.common.schema.KeyValue;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventPulsarConsumer {
    private final List<MessageConsumer> consumers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PulsarListener(
            id="pearl-listener",
            topics = "dbserver1.public.outbox",
            schemaType = SchemaType.KEY_VALUE
    )
    public void listen(KeyValue<GenericRecord, GenericRecord> kv){
        try {
            GenericRecord record = kv.getValue();

            GenericRecord payload = (GenericRecord) record.getField("payload");
            if (payload == null) {
                log.debug("Debezium envelope without 'payload' → skipping message");
                return;
            }

            GenericRecord after = (GenericRecord) payload.getField("after");
            if (after == null) {
                log.debug("Debezium envelope without 'after' → skipping message");
                return;
            }

            String type = (String) after.getField("type");
            String payloadJson = (String) after.getField("payload"); // ton JSON métier (string stockée en BDD)
            BrokerMessage event = objectMapper.readValue(payloadJson, BrokerMessage.class);


            consumers.forEach(c -> {
                if(c.shouldConsume(event.type())){
                    c.consume(type, event.payload());
                }
            });

        } catch (JsonProcessingException e) {
            log.error("Error when processing Pulsar Json message");
        }
    }
}