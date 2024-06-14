package fr.insee.pearljam.api.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.external.service")
public record ExternalServicesProperties(
        String datacollectionUrl,
        String mailUrl) {

}
