package fr.insee.pearljam.api.configuration;

import java.util.function.BiFunction;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SmtpServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BiFunction<String, String, HttpStatus> sendMail() {
        log.info("Placeholder for external Smtp");
        return (s1, s2) -> HttpStatus.OK;
    }

}
