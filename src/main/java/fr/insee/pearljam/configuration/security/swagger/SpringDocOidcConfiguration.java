package fr.insee.pearljam.configuration.security.swagger;

import java.util.List;

import fr.insee.pearljam.configuration.security.OidcProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

/**
 * Swagger configuration to use OIDC authentication with OpenAPI
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "true")
public class SpringDocOidcConfiguration implements SpringDocSecurityConfiguration {

    private final OidcProperties oidcProperties;

    public static final String SECURITY_SCHEMA_OAUTH2 = "oauth2";

    @Override
    public void addSecurity(OpenAPI openAPI) {
        String authUrl = String.format("%s/realms/%s/protocol/openid-connect",
                oidcProperties.authServerUrl(),
                oidcProperties.realm());

        openAPI
                .addServersItem(new Server().url(oidcProperties.applicationHost()))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEMA_OAUTH2, List.of("read", "write")))
                .components(
                        new Components()
                                .addSecuritySchemes(SECURITY_SCHEMA_OAUTH2,
                                        new SecurityScheme()
                                                .name(SECURITY_SCHEMA_OAUTH2)
                                                .type(SecurityScheme.Type.OAUTH2)
                                                .flows(getFlows(authUrl))));
    }

    private OAuthFlows getFlows(String authUrl) {
        OAuthFlows flows = new OAuthFlows();
        OAuthFlow flow = new OAuthFlow();
        Scopes scopes = new Scopes();
        flow.setAuthorizationUrl(authUrl + "/auth");
        flow.setTokenUrl(authUrl + "/token");
        flow.setRefreshUrl(authUrl + "/token");
        flow.setScopes(scopes);
        return flows.authorizationCode(flow);
    }
}