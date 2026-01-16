package fr.insee.pearljam.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpHeaders.*;

import java.util.List;
import java.util.stream.Stream;

import fr.insee.pearljam.api.configuration.properties.ApplicationProperties;

@Configuration
public class CorsConfig {

    @Bean
    protected CorsConfigurationSource corsConfigurationSource(ApplicationProperties applicationProperties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(applicationProperties.corsOrigins());
        config.setAllowedHeaders(List.of(AUTHORIZATION, CONTENT_TYPE));
        config.setAllowedMethods(Stream.of(GET, PUT, DELETE, POST, OPTIONS).map(HttpMethod::toString).toList());
        config.addExposedHeader(CONTENT_DISPOSITION);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}