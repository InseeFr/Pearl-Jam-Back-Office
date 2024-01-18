package fr.insee.pearljam.api.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.roles")
public record RoleProperties(
                String interviewer,
                String local_user,
                String national_user,
                String admin,
                String webclient) {
}
