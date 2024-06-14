package fr.insee.pearljam.api.configuration;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.insee.pearljam.api.configuration.properties.ApplicationProperties;
import fr.insee.pearljam.api.configuration.properties.KeycloakProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

/**
 * SwaggerConfiguration is the class using to configure swagger 2 ways to
 * authenticated : - without authentication, - keycloak authentication
 * 
 * @author Claudel Benjamin
 * 
 */
@Configuration
@RequiredArgsConstructor
public class SpringDocConfiguration {

	public static final String SECURITY_SCHEMA_OAUTH2 = "oauth2";

	@Bean
	@ConditionalOnProperty(name = "application.auth", havingValue = "NOAUTH")
	protected OpenAPI noAuthOpenAPI(BuildProperties buildProperties) {
		return generateOpenAPI(buildProperties);
	}

	@Bean
	@ConditionalOnProperty(name = "application.auth", havingValue = "KEYCLOAK")
	protected OpenAPI keycloakOpenAPI(ApplicationProperties applicationProperties,
			KeycloakProperties keycloakProperties, BuildProperties buildProperties) {
		String authUrl = String.format("%s/realms/%s/protocol/openid-connect",
				keycloakProperties.authServerUrl(),
				keycloakProperties.realm());

		return generateOpenAPI(buildProperties)
				.addServersItem(new Server().url(applicationProperties.host()))
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEMA_OAUTH2, List.of("read", "write")))
				.components(
						new Components()
								.addSecuritySchemes(SECURITY_SCHEMA_OAUTH2,
										new SecurityScheme()
												.name(SECURITY_SCHEMA_OAUTH2)
												.type(SecurityScheme.Type.OAUTH2)
												.flows(getFlows(authUrl))));

	}

	private OpenAPI generateOpenAPI(BuildProperties buildProperties) {
		return new OpenAPI().info(
				new Info()
						.title(buildProperties.getName())
						.description(buildProperties.get("description"))
						.version(buildProperties.getVersion()));
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

	// Docket docket = new Docket(DocumentationType.SWAGGER_2);
	// ArrayList<ResponseMessage> messages = Lists.newArrayList(
	// new ResponseMessageBuilder().code(200).message("Success!").build(),
	// new ResponseMessageBuilder().code(401).message("Not authorized!").build(),
	// new ResponseMessageBuilder().code(403).message("Forbidden!").build(),
	// new ResponseMessageBuilder().code(404).message("Not found!").build(),
	// new ResponseMessageBuilder().code(400)
	// .message("Bad request! Please check the fields of your survey unit to update"
	// + "\n- ID must be fielded and must be the same as parameter ID"
	// + "\n- FirstName must be fielded" + "\n- LastName must be fielded"
	// + "\n- PhoneNumbers must be fielded" + "\n- Campaign must be fielded"
	// + "\n- Address must be fielded"
	// + "\n- Priority must be fielded" + "\n- SampleIdentifiers must be fielded"
	// + "\n- States must be fielded")
	// .build());
	// docket.select().apis(RequestHandlerSelectors.basePackage("fr.insee.pearljam.api.controller")).build()
	// .apiInfo(apiInfo())
	// .useDefaultResponseMessages(false)
	// .globalResponseMessage(RequestMethod.GET, messages)
	// .globalResponseMessage(RequestMethod.PUT, messages)
	// .securitySchemes(securitySchema())
	// .securityContexts(securityContext());
	// return docket;
	// }

}