package fr.insee.pearljam;

import fr.insee.pearljam.api.configuration.log.PropertiesLogger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableTransactionManagement
@ConfigurationPropertiesScan
@EnableJpaRepositories
@EnableScheduling
@Slf4j
public class PearlJamApplication {

	static void main(String[] args) {
		configure(new SpringApplicationBuilder()).build().run(args);
	}

	protected static SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PearlJamApplication.class).listeners(new PropertiesLogger());
	}

	@EventListener
	public void handleApplicationReady(ApplicationReadyEvent event) {
		log.info("=============== Pearl-Jam Back-Office has successfully started. ===============");
	}
}
