package fr.insee.pearljam.infrastructure.http.datacollection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.external.service")
public record DataCollectionHttpProperties(
        String datacollectionUrl) {

}
