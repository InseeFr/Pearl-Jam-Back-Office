package fr.insee.pearljam.api.configuration;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import fr.insee.pearljam.api.configuration.ApplicationProperties.Mode;
import fr.insee.pearljam.api.constants.Constants;

/**
 * SecurityConfiguration is the class using to configure security.<br>
 * 2 ways to authenticated : <br>
 * 0 - without authentication,<br>
 * 1 - basic authentication <br>
 * 
 * @author Claudel Benjamin
 * 
 */
@Configuration
@EnableWebSecurity
@ConditionalOnExpression("'${fr.insee.pearljam.application.mode}' == 'basic' or '${fr.insee.pearljam.application.mode}' == 'noauth'")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	/**
	 * The environment define in Spring application Generate with the application
	 * property environment
	 */
	@Autowired
	private Environment environment;

	@Autowired
	private ApplicationProperties applicationProperties;

	@Value("${fr.insee.pearljam.interviewer.role:#{null}}")
	private String interviewerRole;
	
	@Value("${fr.insee.pearljam.admin.role:#{null}}")
	private String adminRole;
	
	@Value("${fr.insee.pearljam.user.local.role:#{null}}")
	private String userLocalRole;
	
	@Value("${fr.insee.pearljam.user.national.role:#{null}}")
	private String userNationalRole;
	

	/**
	 * This method check if environment is development or test
	 * 
	 * @return true if environment matchs
	 */
	protected boolean isDevelopment() {
		return ArrayUtils.contains(environment.getActiveProfiles(), "dev")
				|| ArrayUtils.contains(environment.getActiveProfiles(), "test");
	}

	/**
	 * This method configure the HTTP security access
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.setProperty("keycloak.enabled", applicationProperties.getMode() != Mode.keycloak ? "false" : "true");
		http
			// disable csrf because of API mode
			.csrf().disable().sessionManagement()
			// use previously declared bean
			.sessionAuthenticationStrategy(sessionAuthenticationStrategy())
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		if(this.applicationProperties.getMode() == Mode.basic) {
			http.httpBasic().authenticationEntryPoint(unauthorizedEntryPoint());
			http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
			// healtcheck
			.antMatchers(HttpMethod.GET, Constants.API_HEALTH_CHECK).permitAll()
			// configuration for endpoints
			.antMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS).hasAnyRole(adminRole, interviewerRole,userLocalRole, userNationalRole)
			.antMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_TEMP_ZONE).hasAnyRole(adminRole, interviewerRole,userLocalRole, userNationalRole)
			.antMatchers(HttpMethod.POST, Constants.API_SURVEYUNITS).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.POST, Constants.API_SURVEYUNITS_INTERVIEWERS).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.GET, Constants.API_SURVEYUNITS_CLOSABLE).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID).hasAnyRole(adminRole, interviewerRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID).hasAnyRole(adminRole, interviewerRole)
			.antMatchers(HttpMethod.POST, Constants.API_SURVEYUNIT_ID_TEMP_ZONE).hasAnyRole(adminRole, interviewerRole)
			.antMatchers(HttpMethod.DELETE, Constants.API_SURVEYUNIT_ID).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_STATE).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_SURVEYUNIT_ID_STATES).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_COMMENT).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_VIEWED).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_CLOSE).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_SURVEYUNIT_ID_CLOSINGCAUSE).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_ADMIN_CAMPAIGNS).hasAnyRole(adminRole)
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS).hasAnyRole(adminRole, userLocalRole, userNationalRole)
			.antMatchers(HttpMethod.GET, Constants.API_INTERVIEWER_CAMPAIGNS).hasAnyRole(interviewerRole)
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_SU_STATECOUNT).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_SU_CONTACTOUTCOMES).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.POST, Constants.API_CAMPAIGN).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_COLLECTION_DATES).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.DELETE, Constants.API_CAMPAIGN_ID).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_INTERVIEWERS).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SURVEYUNITS).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_ABANDONED).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_STATECOUNT).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_CONTACTOUTCOMES).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CONTACTOUTCOMES).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_CONTACTOUTCOMES).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES	).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CAMPAIGNS_ID_ON_GOING).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.GET, Constants.API_INTERVIEWERS).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.POST, Constants.API_INTERVIEWERS).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.GET, Constants.API_INTERVIEWERS_SU_STATECOUNT).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_INTERVIEWER_ID_CAMPAIGNS).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_USER).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.POST, Constants.API_USER).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.GET, Constants.API_USER_ID).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_USER_ID).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.DELETE, Constants.API_USER_ID).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_USER_ID_ORGANIZATIONUNIT_ID).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.POST, Constants.API_GEOGRAPHICALLOCATIONS).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.POST, Constants.API_ORGANIZATIONUNITS).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.POST, Constants.API_ORGANIZATIONUNIT).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.GET, Constants.API_ORGANIZATIONUNITS).hasAnyRole(adminRole)			
			.antMatchers(HttpMethod.DELETE, Constants.API_ORGANIZATIONUNIT_ID).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.POST, Constants.API_ORGANIZATIONUNIT_ID_USERS).hasAnyRole(adminRole)			.antMatchers(HttpMethod.PUT, Constants.API_PREFERENCES).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.POST, Constants.API_MESSAGE).hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_MESSAGES_ID).hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.POST, Constants.API_VERIFYNAME).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.GET, Constants.API_MESSAGEHISTORY).hasAnyRole(adminRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_MESSAGE_MARK_AS_READ).hasAnyRole(adminRole,interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.PUT, Constants.API_MESSAGE_MARK_AS_DELETED).hasAnyRole(adminRole, interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(HttpMethod.POST, Constants.API_CREATEDATASET).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.DELETE, Constants.API_DELETEDATASET).hasAnyRole(adminRole)	
			.antMatchers(HttpMethod.GET, Constants.API_CHECK_HABILITATION).hasAnyRole(adminRole, userLocalRole, userNationalRole)
			.anyRequest().denyAll();
		}else{
			http.httpBasic().disable();
			http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
			// configuration for endpoints
			.antMatchers(Constants.API_SURVEYUNITS,
					Constants.API_SURVEYUNITS_TEMP_ZONE,
					Constants.API_SURVEYUNITS_INTERVIEWERS,
					Constants.API_SURVEYUNITS_CLOSABLE,
					Constants.API_SURVEYUNIT_ID,
					Constants.API_SURVEYUNIT_ID_TEMP_ZONE,
					Constants.API_SURVEYUNIT_ID_STATE,
					Constants.API_SURVEYUNIT_ID_STATES,
					Constants.API_SURVEYUNIT_ID_COMMENT,
					Constants.API_SURVEYUNIT_ID_VIEWED,
					Constants.API_SURVEYUNIT_ID_CLOSE,
					Constants.API_SURVEYUNIT_ID_CLOSINGCAUSE,
					Constants.API_CAMPAIGNS,
					Constants.API_CAMPAIGNS_SU_STATECOUNT,
					Constants.API_CAMPAIGNS_SU_CONTACTOUTCOMES,
					Constants.API_CAMPAIGN,
					Constants.API_CAMPAIGN_ID,
					Constants.API_CAMPAIGN_COLLECTION_DATES,
					Constants.API_CAMPAIGN_ID_INTERVIEWERS,
					Constants.API_CAMPAIGN_ID_SURVEYUNITS,
					Constants.API_CAMPAIGN_ID_SU_ABANDONED,
					Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED,
					Constants.API_CAMPAIGN_ID_SU_STATECOUNT,
					Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT,
					Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT,
					Constants.API_CAMPAIGN_ID_SU_CONTACTOUTCOMES,
					Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CONTACTOUTCOMES,
					Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_CONTACTOUTCOMES,
					Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES,	
					Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY,
					Constants.API_CAMPAIGNS_ID_ON_GOING,
					Constants.API_INTERVIEWERS,
					Constants.API_INTERVIEWERS_SU_STATECOUNT,
					Constants.API_INTERVIEWER_ID_CAMPAIGNS,
					Constants.API_USER,
					Constants.API_USER_ID,
					Constants.API_USER_ID_ORGANIZATIONUNIT_ID,
					Constants.API_GEOGRAPHICALLOCATIONS,
					Constants.API_ORGANIZATIONUNIT,
					Constants.API_ORGANIZATIONUNITS,
					Constants.API_ORGANIZATIONUNIT_ID,
					Constants.API_ORGANIZATIONUNIT_ID_USERS,
					Constants.API_PREFERENCES,
					Constants.API_MESSAGE,
					Constants.API_MESSAGES_ID,
					Constants.API_VERIFYNAME,
					Constants.API_MESSAGEHISTORY,
					Constants.API_MESSAGE_MARK_AS_READ,
					Constants.API_MESSAGE_MARK_AS_DELETED,
					Constants.API_CREATEDATASET,
					Constants.API_DELETEDATASET,
					Constants.API_CHECK_HABILITATION,
					Constants.API_HEALTH_CHECK)
			.permitAll();
		}
	}

	/**
	 * This method configure the authentication manager for DEV and TEST accesses
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (isDevelopment()) {
			switch (this.applicationProperties.getMode()) {
			case basic:
				auth.inMemoryAuthentication().withUser("INTW1").password("{noop}intw1").roles(adminRole, interviewerRole)
						.and()
						.withUser("ABC").password("{noop}abc").roles(adminRole, userLocalRole, userNationalRole)
						.and()
						.withUser("JKL").password("{noop}jkl").roles(adminRole, userLocalRole, userNationalRole)
						.and()
						.withUser("noWrite").password("{noop}a").roles();
				break;
			case noauth:
				break;
			default:
				break;
			}
		}
	}

	@Bean
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Bean
	@ConditionalOnMissingBean(HttpSessionManager.class)
	protected HttpSessionManager httpSessionManager() {
		return new HttpSessionManager();
	}

	/**
	 * This method configure the unauthorized accesses
	 */
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> {
			response.addHeader("WWW-Authenticate", "BasicCustom realm=\"MicroService\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
		};
	}

}
