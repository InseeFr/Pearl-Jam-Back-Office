package fr.insee.pearljam.api.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import fr.insee.pearljam.api.constants.Constants;

/**
 * This class defines the KeyCloak configuration
 * @author scorcaud
 *
 */
@Configuration
@ConditionalOnExpression( "'${fr.insee.pearljam.application.mode}' == 'KeyCloak'")
@ComponentScan(
        basePackageClasses = KeycloakSecurityComponents.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.keycloak.adapters.springsecurity.management.HttpSessionManager"))
@EnableWebSecurity
public class KeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {
	
	@Value("${fr.insee.pearljam.interviewer.role:#{null}}")
	private String interviewerRole;
	
	@Value("${fr.insee.pearljam.user.local.role:#{null}}")
	private String userLocalRole;
	
	@Value("${fr.insee.pearljam.user.national.role:#{null}}")
	private String userNationalRole;
	
	/**
	 * Configure the accessible URI without any roles or permissions
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		// disable csrf because of API mode
		.csrf().disable()
		.sessionManagement()
        // use previously declared bean
           .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
           .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // keycloak filters for securisation
           .and()
               .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
               .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
               .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
           
        // delegate logout endpoint to spring security

           .and()
               .logout()
               .addLogoutHandler(keycloakLogoutHandler())
               .logoutUrl("/logout").logoutSuccessHandler(
               // logout handler for API
               (HttpServletRequest request, HttpServletResponse response, Authentication authentication) ->
                       response.setStatus(HttpServletResponse.SC_OK)
    		   )
           .and()
               	// manage routes securisation
               	.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
               	// configuration for Swagger
   				.antMatchers("/swagger-ui.html/**", "/v2/api-docs","/csrf", "/", "/webjars/**", "/swagger-resources/**").permitAll()
   				.antMatchers("/environnement", "/healthcheck").permitAll()
               	// configuration for endpoints
				.antMatchers(Constants.API_SURVEYUNITS).hasAnyRole(interviewerRole)
				.antMatchers(Constants.API_SURVEYUNIT_ID).hasAnyRole(interviewerRole)

		        .antMatchers(Constants.API_SURVEYUNITS_STATE).hasAnyRole(userLocalRole, userNationalRole)
				.antMatchers(Constants.API_SURVEYUNIT_ID_STATES).hasAnyRole(userLocalRole, userNationalRole)

				.antMatchers(Constants.API_CAMPAIGNS).hasAnyRole(userLocalRole, userNationalRole)
				.antMatchers(Constants.API_CAMPAIGN_ID_INTERVIEWERS).hasAnyRole(userLocalRole, userNationalRole)
				.antMatchers(Constants.API_CAMPAIGN_ID_SURVEYUNITS).hasAnyRole(userLocalRole, userNationalRole)
		        .antMatchers(Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT).hasAnyRole(userLocalRole, userNationalRole)
		        .antMatchers(Constants.API_CAMPAIGN_ID_SU_STATECOUNT).hasAnyRole(userLocalRole, userNationalRole)
		        .antMatchers(Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED).hasAnyRole(userLocalRole, userNationalRole)
		        .antMatchers(Constants.API_CAMPAIGN_ID_SU_ABANDONED).hasAnyRole(userLocalRole, userNationalRole)
		        
		        .antMatchers(Constants.API_USER).hasAnyRole(userLocalRole, userNationalRole)
		        .antMatchers(Constants.API_PREFERENCES).hasAnyRole(userLocalRole, userNationalRole)
				.anyRequest().denyAll(); 
	}
	
	/**
	 * Registers the KeycloakAuthenticationProvider with the authentication
	 * manager.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}
	
	/**
     * Required to handle spring boot configurations
     * @return
     */
    @ConditionalOnExpression( "'${fr.insee.pearljam.application.mode}' == 'KeyCloak'")
    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
   }
    
    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @ConditionalOnExpression( "'${fr.insee.pearljam.application.mode}' == 'KeyCloak'")
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        // required for bearer-only applications.
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}
}