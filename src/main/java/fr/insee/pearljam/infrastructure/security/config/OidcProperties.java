package fr.insee.pearljam.infrastructure.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feature.oidc")
public record OidcProperties(
        boolean enabled,
        String applicationHost,
        String authServerHost,
        String authServerUrl,
        String realm,
        String principalAttribute,
        String clientId) {
}
