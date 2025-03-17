package fr.insee.pearljam.infrastructure.security.config;

import java.util.Collection;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.domain.security.model.AuthorityRole;
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

import lombok.RequiredArgsConstructor;

/**
 * Spring security configuration when using OIDC auth
 */
@Configuration
@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "true")
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class OidcSecurityConfiguration {
	private final OidcProperties oidcProperties;

	/**
	 * Configure spring security filter chain to handle swagger urls
	 *
	 * @param http Http Security configuration object
	 * @param oidcProperties oidc properties
	 * @return the spring security filter chain
	 * @throws Exception exception
	 */
	@Bean
	@Order(1)
	@ConditionalOnProperty(name = "feature.swagger.enabled", havingValue = "true")
	protected SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http,
															 SpringDocSecurityFilterChain springDocSecurityFilterChain,
															 OidcProperties oidcProperties) throws Exception {
		String authorizedConnectionHost = oidcProperties.enabled() ?
				" " + oidcProperties.authServerHost() : "";
		return springDocSecurityFilterChain.buildSecurityFilterChain(http, authorizedConnectionHost);
	}

	/**
	 *
	 * @param http  Http Security configuration object
	 * @param roleProperties role properties
	 * @return the spring security filter chain
	 * @throws Exception exception
	 */
	@Bean
	@Order(2)
	protected SecurityFilterChain filterChain(HttpSecurity http,
			RoleProperties roleProperties) throws Exception {

        http
				.securityMatcher("/**")
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.headers(headers -> headers
						.xssProtection(xssConfig -> xssConfig.headerValue(
								XXssProtectionHeaderWriter.HeaderValue.DISABLED))
						.contentSecurityPolicy(cspConfig -> cspConfig.policyDirectives("default-src 'none'"))
						.referrerPolicy(referrerPolicy -> referrerPolicy.policy(
								ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(roleProperties))));
		authorizeRequests(http);
		return http.build();
	}

	/**
	 * Add security on endpoints
	 * @param http Http security configuration
	 * @throws Exception exception
	 */
	private void authorizeRequests(HttpSecurity http) throws Exception {
		final String adminRole = AuthorityRole.ADMIN.name();
		final String localUserRole = AuthorityRole.LOCAL_USER.name();
		final String nationalUserRole = AuthorityRole.NATIONAL_USER.name();
		final String interviewerRole = AuthorityRole.INTERVIEWER.name();
		final String webclientRole = AuthorityRole.WEBCLIENT.name();

		http
				.authorizeHttpRequests(configurer -> configurer
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.GET, Constants.API_HEALTH_CHECK).permitAll()
						// configuration for endpoints
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS)
						.hasAnyRole(adminRole, interviewerRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_TEMP_ZONE)
						.hasAnyRole(adminRole, interviewerRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.POST, Constants.API_SURVEYUNITS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.POST, Constants.API_SURVEYUNITS_INTERVIEWERS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_CLOSABLE)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_INTERVIEWER, Constants.API_SURVEYUNIT_ID)
						.hasAnyRole(adminRole, interviewerRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID)
						.hasAnyRole(adminRole, interviewerRole)
						.requestMatchers(HttpMethod.POST, Constants.API_SURVEYUNIT_ID_TEMP_ZONE)
						.hasAnyRole(adminRole, interviewerRole)
						.requestMatchers(HttpMethod.DELETE, Constants.API_SURVEYUNIT_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_STATE)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_STATES)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_COMMENT)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_VIEWED)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_CLOSE)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_CLOSINGCAUSE)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_ADMIN_CAMPAIGNS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_INTERVIEWER_CAMPAIGNS)
						.hasRole(interviewerRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_SU_STATECOUNT)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_SU_CONTACTOUTCOMES)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.POST, Constants.API_CAMPAIGN)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.DELETE, Constants.API_CAMPAIGN_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID)
						.hasAnyRole(adminRole, interviewerRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_COLLECTION_DATES)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_INTERVIEWERS)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SURVEYUNITS)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_ABANDONED)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_STATECOUNT)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_CONTACTOUTCOMES)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CONTACTOUTCOMES)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_CONTACTOUTCOMES)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_ID_OU_ID_COMMUNICATION_INFORMATION)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_VISIBILITIES)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_COMMUNICATION_TEMPLATES)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_COMMUNICATION_INFORMATIONS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_REFERENTS)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_ID_ON_GOING)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_ON_GOING)
						.hasRole(webclientRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_INTERVIEWERS_STATECOUNT)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_INTERVIEWERS)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.POST, Constants.API_INTERVIEWERS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_ADMIN_INTERVIEWERS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_INTERVIEWER_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.DELETE, Constants.API_INTERVIEWER_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_INTERVIEWER_ID)
						.hasAnyRole(interviewerRole, adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_INTERVIEWERS_SU_STATECOUNT)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_INTERVIEWER_ID_CAMPAIGNS)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_USER)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.POST, Constants.API_USER)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_USER_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_USER_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.DELETE, Constants.API_USER_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_USER_ID_ORGANIZATIONUNIT_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.POST, Constants.API_ORGANIZATIONUNIT)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.POST, Constants.API_ORGANIZATIONUNITS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_ORGANIZATIONUNITS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.DELETE, Constants.API_ORGANIZATIONUNIT_ID)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.POST, Constants.API_ORGANIZATIONUNIT_ID_USERS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_PREFERENCES)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.POST, Constants.API_MESSAGE)
						.hasAnyRole(adminRole, interviewerRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_MESSAGES_ID)
						.hasAnyRole(adminRole, interviewerRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.POST, Constants.API_VERIFYNAME)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.GET, Constants.API_MESSAGEHISTORY)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_MESSAGE_MARK_AS_READ)
						.hasAnyRole(adminRole, interviewerRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.PUT, Constants.API_MESSAGE_MARK_AS_DELETED)
						.hasAnyRole(adminRole, interviewerRole, localUserRole, nationalUserRole)
						.requestMatchers(HttpMethod.POST, Constants.API_CREATEDATASET)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.DELETE, Constants.API_DELETEDATASET)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_CHECK_HABILITATION)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole, interviewerRole)
						.requestMatchers(HttpMethod.POST, Constants.API_MAIL)
						.hasAnyRole(adminRole, interviewerRole)
						.requestMatchers(HttpMethod.GET, Constants.API_ENUM_STATE)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole, interviewerRole)
						.requestMatchers(HttpMethod.GET, Constants.API_ENUM_CONTACT_ATTEMPT)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole, interviewerRole)
						.requestMatchers(HttpMethod.GET, Constants.API_ENUM_CONTACT_OUTCOME)
						.hasAnyRole(adminRole, localUserRole, nationalUserRole, interviewerRole)
						.requestMatchers(HttpMethod.GET, Constants.API_ADMIN_SURVEYUNITS)
						.hasRole(adminRole)
						.requestMatchers(HttpMethod.GET, Constants.API_ADMIN_CAMPAIGN_ID_SURVEYUNITS)
						.hasRole(adminRole)
						.anyRequest()
						.denyAll());
	}

	@Bean
	protected JwtAuthenticationConverter jwtAuthenticationConverter(RoleProperties roleProperties) {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setPrincipalClaimName(oidcProperties.principalAttribute());
		jwtAuthenticationConverter
				.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter(roleProperties));
		return jwtAuthenticationConverter;
	}

	Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter(RoleProperties roleProperties) {
		return new GrantedAuthorityConverter(roleProperties);
	}

}