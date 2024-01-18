package fr.insee.pearljam.infrastructure.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@ConditionalOnProperty(name = "feature.swagger.enabled", havingValue = "true")
public class SpringDocSecurityFilterChain {

        SecurityFilterChain buildSecurityFilterChain(HttpSecurity http) throws Exception {
                return buildSecurityFilterChain(http, "");
        }

        SecurityFilterChain buildSecurityFilterChain(HttpSecurity http,
                                                     String authorizedConnectionHost) throws Exception {
                String[] swaggerUrls = new String[]{"/swagger-ui/**","/v3/api-docs/**"};
                return http
                        .securityMatcher(swaggerUrls)
                        .cors(Customizer.withDefaults())
                        .headers(headers -> headers
                                .xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                                .contentSecurityPolicy(cspConfig -> cspConfig
                                        .policyDirectives("default-src 'none'; " +
                                                "connect-src 'self'" + authorizedConnectionHost + "; " +
                                                "img-src 'self' data:; " +
                                                "style-src 'self'; " +
                                                "script-src 'self' 'unsafe-inline'")
                                )
                                .referrerPolicy(referrerPolicy ->
                                        referrerPolicy
                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                                ))
                        .authorizeHttpRequests(auth -> auth
                                .anyRequest()
                                .permitAll()
                        )
                        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .build();
        }
}
