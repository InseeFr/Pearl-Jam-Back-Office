package fr.insee.pearljam.config;

import fr.insee.pearljam.domain.campaign.port.userside.DateService;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@Profile("test")
public class FixedDateServiceConfiguration {

    @Bean
    @Primary
    public DateService dateService() {
        return new FixedDateService();
    }
}
