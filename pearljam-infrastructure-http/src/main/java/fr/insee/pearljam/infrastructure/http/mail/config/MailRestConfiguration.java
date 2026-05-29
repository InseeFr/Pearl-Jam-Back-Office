package fr.insee.pearljam.infrastructure.http.mail.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class MailRestConfiguration {
    private final MailProperties mailProperties;

    @Bean
    public RestClient.Builder mailRestClient() {
        return RestClient.builder()
                .baseUrl(mailProperties.url())
                .defaultHeaders(headers -> {
                            headers.setBasicAuth(mailProperties.login(),  mailProperties.password());
                            headers.setContentType(MediaType.APPLICATION_JSON);
                });
    }
}
