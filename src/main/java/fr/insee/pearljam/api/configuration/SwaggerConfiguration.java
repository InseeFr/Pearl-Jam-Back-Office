package fr.insee.pearljam.api.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;

import fr.insee.pearljam.api.configuration.ApplicationProperties.Mode;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationCodeGrant;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerConfiguration is the class using to configure swagger 3 ways to
 * authenticated : - without authentication, - basic authentication - and
 * keycloak authentication
 * 
 * @author Claudel Benjamin
 * 
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	/**
	 * The name of Spring application.<br>
	 * Generate with the application property spring.application.name
	 */
	@Value("${spring.application.name}")
	private String name;

	@Value("${keycloak.resource}")
	private String resource;

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.auth-server-url}")
	private String authUrl;

	public static final String SECURITY_SCHEMA_OAUTH2 = "oauth2";

	@Autowired
	BuildProperties buildProperties;

	@Autowired
	private ApplicationProperties applicationProperties;

	/**
	 * @return Docket (from SpringFox dependency) with a configuration
	 */
	@Bean
	public Docket productApi() {
		
		Docket docket = new Docket(DocumentationType.SWAGGER_2);
		ArrayList<ResponseMessage> messages = Lists.newArrayList(
				new ResponseMessageBuilder().code(200).message("Success!").build(),
				new ResponseMessageBuilder().code(401).message("Not authorized!").build(),
				new ResponseMessageBuilder().code(403).message("Forbidden!").build(),
				new ResponseMessageBuilder().code(404).message("Not found!").build(),
				new ResponseMessageBuilder().code(400)
				.message("Bad request! Please check the fields of your survey unit to update"
						+ "\n- ID must be fielded and must be the same as parameter ID"
						+ "\n- FirstName must be fielded" + "\n- LastName must be fielded"
						+ "\n- PhoneNumbers must be fielded" + "\n- Campaign must be fielded"
						+ "\n- Address must be fielded" 
						+ "\n- Priority must be fielded" + "\n- SampleIdentifiers must be fielded"
						+ "\n- States must be fielded")
				.build());
		docket.select().apis(RequestHandlerSelectors.basePackage("fr.insee.pearljam.api.controller")).build()
				.apiInfo(apiInfo())
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET, messages)
				.globalResponseMessage(RequestMethod.PUT, messages)
				.securitySchemes(securitySchema())
				.securityContexts(securityContext());
		return docket;
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				buildProperties.getName(), 
				"Back-office services for PearlJam",
				buildProperties.getVersion(), 
				"",
				new Contact("Metallica", "https://github.com/InseeFr/Pearl-Jam-Back-Office", ""), 
				"LICENSEE",
				"https://github.com/InseeFr/Pearl-Jam-Back-Office/blob/master/LICENSE", 
				List.of());
	}

	
	private List<? extends SecurityScheme> securitySchema() {
		switch (this.applicationProperties.getMode()) {
		case basic:
			return List.of(new BasicAuth(name));
		case keycloak:
			final String AUTH_SERVER = authUrl + "/realms/" + realm + "/protocol/openid-connect/auth";
			final String AUTH_SERVER_TOKEN_ENDPOINT = authUrl + "/realms/" + realm + "/protocol/openid-connect/token";
			final GrantType grantType = new AuthorizationCodeGrant(
					new TokenRequestEndpoint(AUTH_SERVER, resource, null),
					new TokenEndpoint(AUTH_SERVER_TOKEN_ENDPOINT, "access_token"));
			final List<AuthorizationScope> scopes = new ArrayList<>();
			scopes.add(new AuthorizationScope("sampleScope", "there must be at least one scope here"));
			return List.of(new OAuth(SECURITY_SCHEMA_OAUTH2, scopes, Collections.singletonList(grantType)));
		default:
			return List.of();
		}
	}

	private List<SecurityContext> securityContext() {
		switch (this.applicationProperties.getMode()) {
		case basic:
			return List.of(SecurityContext.builder()
					.securityReferences(List.of(new SecurityReference(name, new AuthorizationScope[0])))
					.forPaths(PathSelectors.any()).build());
		case keycloak:
			return List.of(SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build());
		default:
			return List.of();
		}
	}

	private List<SecurityReference> defaultAuth() {
		final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Collections.singletonList(new SecurityReference(SECURITY_SCHEMA_OAUTH2, authorizationScopes));
	}

	@Bean
	public SecurityConfiguration security() {
		if(this.applicationProperties.getMode()==Mode.keycloak)
			return SecurityConfigurationBuilder.builder().clientId(resource).realm(realm).scopeSeparator(",").build();
		else
			return null;
	}
	
}