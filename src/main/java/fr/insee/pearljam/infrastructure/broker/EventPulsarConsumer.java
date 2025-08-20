package fr.insee.pearljam.infrastructure.broker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.support.PulsarHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventPulsarConsumer {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DebeziumEnvelope(
            Value before,
            Value after,
            Source source,
            String op,
            @JsonProperty("ts_ms") Long tsMs,
            Transaction transaction
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value(
            String id,
            String payload,
            @JsonProperty("created_date") Long createdDateMicros
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Source(
            String version,
            String connector,
            String name,
            @JsonProperty("ts_ms") Long tsMs,
            String snapshot,
            String db,
            String sequence,
            String schema,
            String table
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Transaction(
            String id,
            @JsonProperty("total_order") Long totalOrder,
            @JsonProperty("data_collection_order") Long dataCollectionOrder
    ) {}

    // --- Ton message métier --------------------------------------------------

    private final List<MessageConsumer> consumers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PulsarListener(
            id="pearl-listener",
            subscriptionName = "pearl-sub",
            topics = "persistent://public/default/dbserver1.public.outbox",
            schemaType = SchemaType.KEY_VALUE
    )
    public void listen(@Payload String json,
                       @Header(PulsarHeaders.KEY) String keyJson ){
        try {

            DebeziumEnvelope record = objectMapper.readValue(json, DebeziumEnvelope.class);

            if (record == null) {
                log.debug("Debezium envelope without 'payload' → skipping message");
                return;
            }

            var after = record.after();
            if (after == null) {
                log.debug("Debezium envelope without 'after' → skipping message");
                return;
            }

            BrokerMessage event = objectMapper.readValue(after.payload(), BrokerMessage.class);


            String type = event.type();
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