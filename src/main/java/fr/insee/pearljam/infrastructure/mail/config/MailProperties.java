package fr.insee.pearljam.infrastructure.mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feature.mail-service")
public record MailProperties(
        String url,
        String login,
        String password,
        String mailRecipients,
        String mailSender) {}
