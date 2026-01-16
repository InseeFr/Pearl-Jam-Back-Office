package fr.insee.pearljam.infrastructure.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.roles")
public record RoleProperties(
                String interviewer,
                String local_user,
                String national_user,
                String admin,
                String webclient) {
}
