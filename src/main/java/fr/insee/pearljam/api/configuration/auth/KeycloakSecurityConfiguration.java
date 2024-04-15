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
	 *
	 * @param http Http Security Object
	 * @return the spring security filter
	 * @throws Exception exception
	 */
	@Bean
	@Order(2)
	protected SecurityFilterChain filterChain(HttpSecurity http,
			RoleProperties roleProperties) throws Exception {
		return http
				.securityMatcher("/**")
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.headers(headers -> headers
						.xssProtection(
								xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
						.contentSecurityPolicy(cspConfig -> cspConfig
								.policyDirectives("default-src 'none'"))
						.referrerPolicy(referrerPolicy -> referrerPolicy
								.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))
				.authorizeHttpRequests(configurer -> configurer
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/healthcheck").permitAll()
						// actuator (actuator metrics are disabled by default)
						.requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
						.anyRequest()
						.authenticated())
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

	/**
	 * Configure the accessible URI without any roles or permissions
	 */
	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// http
	// // disable csrf because of API mode
	// .csrf().disable()
	// .sessionManagement()
	// // use previously declared bean
	// .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
	// .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	// // keycloak filters for securisation
	// .and()
	// .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
	// .addFilterBefore(keycloakAuthenticationProcessingFilter(),
	// X509AuthenticationFilter.class)
	// .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
	// // delegate logout endpoint to spring security
	// .and()
	// .logout()
	// .addLogoutHandler(keycloakLogoutHandler())
	// .logoutUrl("/logout").logoutSuccessHandler(
	// // logout handler for API
	// (HttpServletRequest request, HttpServletResponse response,
	// Authentication authentication) ->
	// response.setStatus(HttpServletResponse.SC_OK))
	// .and()
	// // manage routes securisation
	// .authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
	// // healtcheck
	// .antMatchers(HttpMethod.GET, Constants.API_HEALTH_CHECK).permitAll()
	// // configuration for Swagger
	// .antMatchers("/swagger-ui.html/**", "/v2/api-docs", "/csrf", "/",
	// "/webjars/**",
	// "/swagger-resources/**")
	// .permitAll()
	// .antMatchers("/environnement", "/healthcheck").permitAll()
	// // configuration for endpoints
	// .antMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS)
	// .hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_TEMP_ZONE)
	// .hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.POST,
	// Constants.API_SURVEYUNITS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.POST,
	// Constants.API_SURVEYUNITS_INTERVIEWERS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_CLOSABLE)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_SURVEYUNIT_ID).hasAnyRole(adminRole, interviewerRole)
	// .antMatchers(HttpMethod.PUT,
	// Constants.API_SURVEYUNIT_ID).hasAnyRole(adminRole, interviewerRole)
	// .antMatchers(HttpMethod.POST, Constants.API_SURVEYUNIT_ID_TEMP_ZONE)
	// .hasAnyRole(adminRole, interviewerRole)
	// .antMatchers(HttpMethod.DELETE,
	// Constants.API_SURVEYUNIT_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_STATE)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_STATES)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_COMMENT)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_VIEWED)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_CLOSE)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_CLOSINGCAUSE)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_ADMIN_CAMPAIGNS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_INTERVIEWER_CAMPAIGNS).hasAnyRole(interviewerRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_SU_STATECOUNT)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_SU_CONTACTOUTCOMES)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.POST, Constants.API_CAMPAIGN).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.DELETE,
	// Constants.API_CAMPAIGN_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID)
	// .hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_COLLECTION_DATES)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_INTERVIEWERS)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SURVEYUNITS)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_ABANDONED)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_STATECOUNT)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_CONTACTOUTCOMES)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CONTACTOUTCOMES)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_CONTACTOUTCOMES)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_CAMPAIGN_ID_VISIBILITIES).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_REFERENTS)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_CAMPAIGNS_ID_ON_GOING).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET, Constants.API_INTERVIEWERS)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.POST,
	// Constants.API_INTERVIEWERS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_ADMIN_INTERVIEWERS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.PUT,
	// Constants.API_INTERVIEWER_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.DELETE,
	// Constants.API_INTERVIEWER_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_INTERVIEWER_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET, Constants.API_INTERVIEWERS_SU_STATECOUNT)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_INTERVIEWER_ID_CAMPAIGNS)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_USER).hasAnyRole(adminRole,
	// userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.POST, Constants.API_USER).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET, Constants.API_USER_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_USER_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.DELETE, Constants.API_USER_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.PUT,
	// Constants.API_USER_ID_ORGANIZATIONUNIT_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.POST,
	// Constants.API_ORGANIZATIONUNIT).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.POST,
	// Constants.API_ORGANIZATIONUNITS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_ORGANIZATIONUNITS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.DELETE,
	// Constants.API_ORGANIZATIONUNIT_ID).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.POST,
	// Constants.API_ORGANIZATIONUNIT_ID_USERS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_PREFERENCES)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.POST, Constants.API_MESSAGE)
	// .hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_MESSAGES_ID)
	// .hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.POST, Constants.API_VERIFYNAME)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.GET, Constants.API_MESSAGEHISTORY)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_MESSAGE_MARK_AS_READ)
	// .hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.PUT, Constants.API_MESSAGE_MARK_AS_DELETED)
	// .hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)
	// .antMatchers(HttpMethod.POST,
	// Constants.API_CREATEDATASET).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.DELETE,
	// Constants.API_DELETEDATASET).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET, Constants.API_CHECK_HABILITATION)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole, interviewerRole)
	// .antMatchers(HttpMethod.POST, Constants.API_MAIL).hasAnyRole(adminRole,
	// interviewerRole)
	// .antMatchers(HttpMethod.GET, Constants.API_ENUM_STATE)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole, interviewerRole)
	// .antMatchers(HttpMethod.GET, Constants.API_ENUM_CONTACT_ATTEMPT)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole, interviewerRole)
	// .antMatchers(HttpMethod.GET, Constants.API_ENUM_CONTACT_OUTCOME)
	// .hasAnyRole(adminRole, userLocalRole, userNationalRole, interviewerRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_ADMIN_SURVEYUNITS).hasAnyRole(adminRole)
	// .antMatchers(HttpMethod.GET,
	// Constants.API_ADMIN_CAMPAIGN_ID_SURVEYUNITS).hasAnyRole(adminRole)
	// .anyRequest().denyAll();
	// }

}