package fr.insee.pearljam.api.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
        String host,
        @NotEmpty(message = "cors origins must be specified (application.corsOrigins)") List<String> corsOrigins,
        @NotEmpty(message = "Folder where temp files will be created cannot be empty.") String tempFolder) {
}