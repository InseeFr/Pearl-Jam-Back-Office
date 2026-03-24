package fr.insee.pearljam.infrastructure.persistence;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "fr.insee.pearljam.infrastructure.persistence")
@EnableTransactionManagement
@ConfigurationPropertiesScan("fr.insee.pearljam")
@EnableJpaRepositories("fr.insee.pearljam.infrastructure.persistence")
class PersistenceTestApplication {
}
