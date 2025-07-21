package fr.insee.pearljam.infrastructure.broker;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventKafkaConsumer {
    public EventKafkaConsumer() {
    }

    @KafkaListener(id="pearl-events-listener", topics = "events")
    public void listen(ObjectNode in){

        System.out.println(in);
    }
}