package fr.insee.pearljam.infrastructure.http.datacollection.config;

import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class DataCollectionRestConfiguration {
    private final DataCollectionHttpProperties dataCollectionHttpProperties;

    @Bean
    protected RestClient.Builder dataCollectionRestClient(AuthenticatedUserService authenticationHelper) {
        return RestClient.builder()
                .baseUrl(dataCollectionHttpProperties.datacollectionUrl())
                .requestInterceptor(new DataCollectionTokenInterceptor(authenticationHelper))
                .defaultHeaders(h -> h.setContentType(MediaType.APPLICATION_JSON));
    }
}
