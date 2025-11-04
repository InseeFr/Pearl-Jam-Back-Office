package fr.insee.pearljam.configuration;

import fr.insee.pearljam.campaign.domain.port.userside.DateService;
import fr.insee.pearljam.campaign.domain.service.dummy.FixedDateService;
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
