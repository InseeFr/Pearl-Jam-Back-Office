package fr.insee.pearljam.configuration.security.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration to use no authentication with OpenAPI
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "false")
public class SpringDocNoAuthConfiguration implements SpringDocSecurityConfiguration {
    @Override
    public void addSecurity(OpenAPI openAPI) {
        // nothing to do as no security here
    }
}
