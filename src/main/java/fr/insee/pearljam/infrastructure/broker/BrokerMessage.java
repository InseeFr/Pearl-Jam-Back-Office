package fr.insee.pearljam.infrastructure.broker;

public record BrokerMessage(String type, Payload payload) {
}
