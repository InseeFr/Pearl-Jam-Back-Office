package fr.insee.pearljam.api.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.security.keycloak")
public record KeycloakProperties(
        String authServerHost,
        String authServerUrl,
        String realm,
        String principalAttribute,
        String clientId) {
}
