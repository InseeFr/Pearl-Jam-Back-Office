package fr.insee.pearljam.infrastructure.security.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Handling security configuration for spring doc
 */
public interface SpringDocSecurityConfiguration {
    /**
     * Add security configuration to the OpenAPI object
     * @param openAPI configuration object for spring doc
     */
    void addSecurity(OpenAPI openAPI);
}
