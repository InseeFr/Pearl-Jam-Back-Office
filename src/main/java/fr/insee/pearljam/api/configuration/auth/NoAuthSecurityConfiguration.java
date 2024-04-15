package fr.insee.pearljam.api.configuration.auth;

import fr.insee.pearljam.api.configuration.properties.ApplicationProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@ConditionalOnProperty(name = "application.auth", havingValue = "NOAUTH")
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class NoAuthSecurityConfiguration {
        private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

        /**
         * Configure spring security filter chain when no authentication
         *
         * @param http Http Security Object
         * @return the spring security filter
         * @throws Exception exception
         */
        @Bean
        @Order(2)
        protected SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
                        throws Exception {
                MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

                return http
                                .securityMatcher("/**")
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .headers(headers -> headers
                                                .xssProtection(xssConfig -> xssConfig.headerValue(
                                                                XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                                                .contentSecurityPolicy(cspConfig -> cspConfig
                                                                .policyDirectives("default-src 'self' ; " +
                                                                                "connect-src 'self' ; " +
                                                                                "img-src 'self' data:; " +
                                                                                "style-src 'self'  ; " +
                                                                                "script-src 'self' 'unsafe-inline'"))
                                                .referrerPolicy(referrerPolicy -> referrerPolicy
                                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                                                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                                                XFrameOptionsMode.SAMEORIGIN)))

                                .anonymous(anonymousConfig -> anonymousConfig
                                                .authorities(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.ADMIN))
                                .authorizeHttpRequests(
                                                authorize -> authorize
                                                                .requestMatchers(mvcMatcherBuilder.pattern("/api/**"))
                                                                .permitAll()
                                                                .requestMatchers(antMatcher("/h2-console/**"))
                                                                .permitAll())
                                .build();
        }

        @Bean
        @Order(1)
        protected SecurityFilterChain filterPublicUrlsChain(HttpSecurity http,
                        ApplicationProperties applicationProperties, MvcRequestMatcher.Builder mvc) throws Exception {
                return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http,
                                applicationProperties.publicUrls());
        }

}