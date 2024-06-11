package fr.insee.pearljam.api.configuration.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class PublicSecurityFilterChain {

        private static final String H2_CONSOLE_PATH = "/h2-console/**";
        private final MvcRequestMatcher.Builder mvc;

        public PublicSecurityFilterChain(HandlerMappingIntrospector introspector) {
                this.mvc = new MvcRequestMatcher.Builder(introspector);
        }

        @Bean
        MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
                return new MvcRequestMatcher.Builder(introspector);
        }

        SecurityFilterChain buildSecurityPublicFilterChain(HttpSecurity http,
                        String[] publicUrls) throws Exception {
                return buildSecurityPublicFilterChain(http, mvc, publicUrls, H2_CONSOLE_PATH);
        }

        SecurityFilterChain buildSecurityPublicFilterChain(HttpSecurity http,
                        String[] publicUrls, String authorizedConnectionHost) throws Exception {
                return buildSecurityPublicFilterChain(http, mvc, publicUrls, authorizedConnectionHost);
        }

        SecurityFilterChain buildSecurityPublicFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc,
                        String[] publicUrls,
                        String authorizedConnectionHost) throws Exception {
                return http
                                .securityMatcher(publicUrls)
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers(
                                                                AntPathRequestMatcher.antMatcher(H2_CONSOLE_PATH)))
                                .cors(Customizer.withDefaults())
                                .headers(headers -> headers
                                                .xssProtection(xssConfig -> xssConfig.headerValue(
                                                                XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                                                .contentSecurityPolicy(cspConfig -> cspConfig
                                                                .policyDirectives("default-src 'none' ; " +
                                                                                "connect-src 'self' ; " +
                                                                                "img-src 'self' data:; " +
                                                                                "style-src 'self' "
                                                                                + authorizedConnectionHost + " ; " +
                                                                                "script-src 'self' 'unsafe-inline'"))
                                                .referrerPolicy(referrerPolicy -> referrerPolicy
                                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))

                                .authorizeHttpRequests(auth -> auth
                                                // .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                                .requestMatchers(new AntPathRequestMatcher(H2_CONSOLE_PATH))
                                                .permitAll()
                                                .requestMatchers(mvc.pattern("/**")).permitAll()
                                                .anyRequest()
                                                .authenticated())
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .build();
        }
}
