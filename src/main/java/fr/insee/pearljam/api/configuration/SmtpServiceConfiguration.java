package fr.insee.pearljam.api.configuration;

import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class SmtpServiceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpServiceConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public BiFunction<String, String, HttpStatus> sendMail() {
        LOGGER.info("Placeholder for external Smtp");
        return (s1, s2) -> HttpStatus.OK;
    }

}
