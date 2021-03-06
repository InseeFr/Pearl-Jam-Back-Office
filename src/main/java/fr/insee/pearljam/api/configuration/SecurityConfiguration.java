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
			// configuration for endpoints
			.antMatchers(Constants.API_SURVEYUNITS).hasAnyRole(interviewerRole,userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_SURVEYUNITS_INTERVIEWERS).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_SURVEYUNITS_CLOSABLE).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_SURVEYUNIT_ID).hasAnyRole(interviewerRole)	
			.antMatchers(Constants.API_SURVEYUNIT_ID_STATE).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_SURVEYUNIT_ID_STATES).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_SURVEYUNIT_ID_COMMENT).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_SURVEYUNIT_ID_VIEWED).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_SURVEYUNIT_ID_CLOSE).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_SURVEYUNIT_ID_CLOSINGCAUSE).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGNS).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGNS_SU_STATECOUNT).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGNS_SU_CONTACTOUTCOMES).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_COLLECTION_DATES).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_INTERVIEWERS).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SURVEYUNITS).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_ABANDONED).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_STATECOUNT).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_CONTACTOUTCOMES).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CONTACTOUTCOMES).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_CONTACTOUTCOMES).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES	).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_INTERVIEWERS).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_INTERVIEWERS_SU_STATECOUNT).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_INTERVIEWER_ID_CAMPAIGNS).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_USER).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_GEOGRAPHICALLOCATIONS).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_ORGANIZATIONUNITS).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_PREFERENCES).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_MESSAGE).hasAnyRole(interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_MESSAGES_ID).hasAnyRole(interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_VERIFYNAME).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_MESSAGEHISTORY).hasAnyRole(userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_MESSAGE_MARK_AS_READ).hasAnyRole(interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_MESSAGE_MARK_AS_DELETED).hasAnyRole(interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CREATEDATASET).hasAnyRole(interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_DELETEDATASET).hasAnyRole(interviewerRole, userLocalRole, userNationalRole)	
			.antMatchers(Constants.API_CHECK_HABILITATION).hasAnyRole(userLocalRole, userNationalRole)
			.anyRequest().denyAll();
		}else{
			http.httpBasic().disable();
			http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
			// configuration for endpoints
			.antMatchers(Constants.API_SURVEYUNITS,
					Constants.API_SURVEYUNITS_INTERVIEWERS,
					Constants.API_SURVEYUNITS_CLOSABLE,
					Constants.API_SURVEYUNIT_ID,
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
					Constants.API_INTERVIEWERS,
					Constants.API_INTERVIEWERS_SU_STATECOUNT,
					Constants.API_INTERVIEWER_ID_CAMPAIGNS,
					Constants.API_USER,
					Constants.API_GEOGRAPHICALLOCATIONS,
					Constants.API_ORGANIZATIONUNITS,
					Constants.API_PREFERENCES,
					Constants.API_MESSAGE,
					Constants.API_MESSAGES_ID,
					Constants.API_VERIFYNAME,
					Constants.API_MESSAGEHISTORY,
					Constants.API_MESSAGE_MARK_AS_READ,
					Constants.API_MESSAGE_MARK_AS_DELETED,
					Constants.API_CREATEDATASET,
					Constants.API_DELETEDATASET,
					Constants.API_CHECK_HABILITATION)
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
				auth.inMemoryAuthentication().withUser("INTW1").password("{noop}intw1").roles(interviewerRole)
						.and()
						.withUser("ABC").password("{noop}abc").roles(userLocalRole, userNationalRole)
						.and()
						.withUser("JKL").password("{noop}jkl").roles(userLocalRole, userNationalRole)
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
