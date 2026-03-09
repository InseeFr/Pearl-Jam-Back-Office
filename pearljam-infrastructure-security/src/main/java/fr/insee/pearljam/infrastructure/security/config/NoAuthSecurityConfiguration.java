package fr.insee.pearljam.infrastructure.security.config;

import fr.insee.pearljam.domain.security.model.AuthorityRole;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Spring security configuration when using OIDC auth
 */
@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "false")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class NoAuthSecurityConfiguration {
        private final OidcProperties oidcProperties;

        @Bean
        @Order(1)
        @ConditionalOnProperty(name = "feature.swagger.enabled", havingValue = "true")
        protected SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http,
                                                                 SpringDocSecurityFilterChain springDocSecurityFilterChain) throws Exception {
                return springDocSecurityFilterChain.buildSecurityFilterChain(http);
        }

        /**
         * Configure spring security filter chain when no authentication
         *
         * @param http Http Security Object
         * @return the spring security filter
         * @throws Exception exception
         */
        @Bean
        @Order(2)
        protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                return http
                        .securityMatcher("/**")
                        .csrf(AbstractHttpConfigurer::disable)
                        .cors(Customizer.withDefaults())
                        .headers(headers -> headers
                                .xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                                .contentSecurityPolicy(cspConfig -> cspConfig
                                        .policyDirectives("default-src 'none'")
                                )
                                .referrerPolicy(referrerPolicy ->
                                        referrerPolicy
                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                                ))
                        .addFilterBefore(guestAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                        .authorizeHttpRequests(authorize -> authorize.anyRequest()
                                .permitAll())
                        .build();
        }

        /**
         * Filter used to have an authenticated admin user when oidc is disabled
         * @return the filter
         */
        private Filter guestAuthenticationFilter() {
                return new HttpFilter() {
                        @Override
                        protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
                                String username = "GUEST";

                                List<GrantedAuthority> authorities = new ArrayList<>();
                                authorities.add(new SimpleGrantedAuthority(AuthorityRole.ADMIN.securityRole()));
                                authorities.add(new SimpleGrantedAuthority(AuthorityRole.INTERVIEWER.securityRole()));

                                Map<String, Object> headers = Map.of("typ", "JWT");
                                Map<String, Object> claims = Map.of(oidcProperties.principalAttribute(), username, "name", username);

                                Jwt jwt = new Jwt("token-value", Instant.MIN, Instant.MAX, headers, claims);
                                JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities, username);

                                // Set the authentication token in the SecurityContextHolder
                                SecurityContextHolder.getContext().setAuthentication(authentication);

                                // Proceed with the request
                                chain.doFilter(request, response);
                        }
                };
        }
}