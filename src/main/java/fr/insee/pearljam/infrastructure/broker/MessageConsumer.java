package fr.insee.pearljam.infrastructure.broker;

public interface MessageConsumer {

    boolean shouldConsume(String type);

    void consume(String type, Payload payload);
}
