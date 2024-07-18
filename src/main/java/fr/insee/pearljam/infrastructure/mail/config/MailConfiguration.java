package fr.insee.pearljam.infrastructure.mail.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class MailConfiguration {
    private final MailProperties mailProperties;

    @Bean
    protected RestTemplate mailRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new RestTemplateMailInterceptor(mailProperties.login(), mailProperties.password()));
        return restTemplate;
    }
}
