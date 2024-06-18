package fr.insee.pearljam.api.configuration.auth;

import java.util.Collection;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import fr.insee.pearljam.api.configuration.properties.ApplicationProperties;
import fr.insee.pearljam.api.configuration.properties.AuthEnumProperties;
import fr.insee.pearljam.api.configuration.properties.KeycloakProperties;
import fr.insee.pearljam.api.configuration.properties.RoleProperties;
import fr.insee.pearljam.api.constants.Constants;
import lombok.RequiredArgsConstructor;

/**
 * This class defines the KeyCloak configuration
 * 
 * @author scorcaud
 *
 */
@Configuration
@ConditionalOnProperty(name = "application.auth", havingValue = "KEYCLOAK")
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class KeycloakSecurityConfiguration {
	private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

	/**
	 * Configure spring security filter chain to handle keycloak authentication
	 * AuthConstants.generateAuthority(AuthorityRoleEnum.LOCAL_USER)
	 * 
	 * @param http Http Security Object
	 * @return the spring security filter
	 * @throws Exception exception
	 */
	@Bean
	@Order(2)
	protected SecurityFilterChain filterChain(HttpSecurity http,
			RoleProperties roleProperties) throws Exception {
		final String ADMIN = AuthConstants.generateAuthority(AuthorityRoleEnum.ADMIN);
		final String LOCAL_USER = AuthConstants.generateAuthority(AuthorityRoleEnum.LOCAL_USER);
		final String NATIONAL_USER = AuthConstants.generateAuthority(AuthorityRoleEnum.NATIONAL_USER);
		final String INTERVIEWER = AuthConstants.generateAuthority(AuthorityRoleEnum.INTERVIEWER);
		return http
				.securityMatcher("/**")
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.headers(headers -> headers
						.xssProtection(xssConfig -> xssConfig.headerValue(
								XXssProtectionHeaderWriter.HeaderValue.DISABLED))
						.contentSecurityPolicy(cspConfig -> cspConfig.policyDirectives("default-src 'none'"))
						.referrerPolicy(referrerPolicy -> referrerPolicy.policy(
								ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))
				.authorizeHttpRequests(configurer -> configurer
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/healthcheck").permitAll()
						.requestMatchers(HttpMethod.GET, "/swagger-ui.html/**", "/v2/api-docs", "/csrf", "/",
								"/webjars/**", "/swagger-resources/**")
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/environnement", "/healthcheck").permitAll()
						// configuration for endpoints
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS)
						.hasAnyAuthority(ADMIN, INTERVIEWER, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_TEMP_ZONE)
						.hasAnyAuthority(ADMIN, INTERVIEWER, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.POST,
								Constants.API_SURVEYUNITS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.POST,
								Constants.API_SURVEYUNITS_INTERVIEWERS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_CLOSABLE)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_SURVEYUNIT_ID)
						.hasAnyAuthority(ADMIN, INTERVIEWER)
						.requestMatchers(HttpMethod.PUT,
								Constants.API_SURVEYUNIT_ID)
						.hasAnyAuthority(ADMIN, INTERVIEWER)
						.requestMatchers(HttpMethod.POST, Constants.API_SURVEYUNIT_ID_TEMP_ZONE)
						.hasAnyAuthority(ADMIN, INTERVIEWER)
						.requestMatchers(HttpMethod.DELETE,
								Constants.API_SURVEYUNIT_ID)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_STATE)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_STATES)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_COMMENT)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_VIEWED)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_CLOSE)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_CLOSINGCAUSE)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_ADMIN_CAMPAIGNS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_INTERVIEWER_CAMPAIGNS)
						.hasAnyAuthority(INTERVIEWER)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_SU_STATECOUNT)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_SU_CONTACTOUTCOMES)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.POST, Constants.API_CAMPAIGN).hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.DELETE,
								Constants.API_CAMPAIGN_ID)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_ID).hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID)
						.hasAnyAuthority(ADMIN, INTERVIEWER, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_COLLECTION_DATES)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_INTERVIEWERS)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SURVEYUNITS)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_ABANDONED)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_STATECOUNT)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_CONTACTOUTCOMES)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CONTACTOUTCOMES)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_CONTACTOUTCOMES)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_CAMPAIGN_ID_VISIBILITIES)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_REFERENTS)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_CAMPAIGNS_ID_ON_GOING)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET, Constants.API_INTERVIEWERS)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.POST,
								Constants.API_INTERVIEWERS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET,
								Constants.API_ADMIN_INTERVIEWERS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.PUT,
								Constants.API_INTERVIEWER_ID)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.DELETE,
								Constants.API_INTERVIEWER_ID)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET,
								Constants.API_INTERVIEWER_ID)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET, Constants.API_INTERVIEWERS_SU_STATECOUNT)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_INTERVIEWER_ID_CAMPAIGNS)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_USER).hasAnyAuthority(ADMIN,
								LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.POST, Constants.API_USER).hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET, Constants.API_USER_ID).hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.PUT, Constants.API_USER_ID).hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.DELETE, Constants.API_USER_ID).hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.PUT,
								Constants.API_USER_ID_ORGANIZATIONUNIT_ID)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.POST,
								Constants.API_ORGANIZATIONUNIT)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.POST,
								Constants.API_ORGANIZATIONUNITS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET,
								Constants.API_ORGANIZATIONUNITS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.DELETE,
								Constants.API_ORGANIZATIONUNIT_ID)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.POST,
								Constants.API_ORGANIZATIONUNIT_ID_USERS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.PUT, Constants.API_PREFERENCES)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.POST, Constants.API_MESSAGE)
						.hasAnyAuthority(ADMIN, INTERVIEWER, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_MESSAGES_ID)
						.hasAnyAuthority(ADMIN, INTERVIEWER, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.POST, Constants.API_VERIFYNAME)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.GET, Constants.API_MESSAGEHISTORY)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.PUT, Constants.API_MESSAGE_MARK_AS_READ)
						.hasAnyAuthority(ADMIN, INTERVIEWER, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.PUT, Constants.API_MESSAGE_MARK_AS_DELETED)
						.hasAnyAuthority(ADMIN, INTERVIEWER, LOCAL_USER, NATIONAL_USER)
						.requestMatchers(HttpMethod.POST,
								Constants.API_CREATEDATASET)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.DELETE,
								Constants.API_DELETEDATASET)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET, Constants.API_CHECK_HABILITATION)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER, INTERVIEWER)
						.requestMatchers(HttpMethod.POST, Constants.API_MAIL).hasAnyAuthority(ADMIN,
								INTERVIEWER)
						.requestMatchers(HttpMethod.GET, Constants.API_ENUM_STATE)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER, INTERVIEWER)
						.requestMatchers(HttpMethod.GET, Constants.API_ENUM_CONTACT_ATTEMPT)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER, INTERVIEWER)
						.requestMatchers(HttpMethod.GET, Constants.API_ENUM_CONTACT_OUTCOME)
						.hasAnyAuthority(ADMIN, LOCAL_USER, NATIONAL_USER, INTERVIEWER)
						.requestMatchers(HttpMethod.GET,
								Constants.API_ADMIN_SURVEYUNITS)
						.hasAnyAuthority(ADMIN)
						.requestMatchers(HttpMethod.GET,
								Constants.API_ADMIN_CAMPAIGN_ID_SURVEYUNITS)
						.hasAnyAuthority(ADMIN)
						// other requests should be rejected
						.anyRequest().denyAll())

				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(roleProperties))))
				.build();
	}

	@Bean
	@Order(1)
	protected SecurityFilterChain filterPublicUrlsChain(HttpSecurity http, ApplicationProperties applicationProperties,
			KeycloakProperties keycloakProperties) throws Exception {
		String authorizedConnectionHost = applicationProperties.auth().equals(AuthEnumProperties.KEYCLOAK)
				? " " + keycloakProperties.authServerHost()
				: "";
		return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http,
				applicationProperties.publicUrls(), authorizedConnectionHost);
	}

	@Bean
	protected JwtAuthenticationConverter jwtAuthenticationConverter(RoleProperties roleProperties) {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setPrincipalClaimName("name");
		jwtAuthenticationConverter
				.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter(roleProperties));
		return jwtAuthenticationConverter;
	}

	Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter(RoleProperties roleProperties) {
		return new GrantedAuthorityConverter(roleProperties);
	}

}