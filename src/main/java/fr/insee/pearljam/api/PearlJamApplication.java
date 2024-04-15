package fr.insee.pearljam.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableTransactionManagement
@ConfigurationPropertiesScan
@EnableJpaRepositories
@Slf4j
public class PearlJamApplication {

	public static void main(String[] args) {
		configure(new SpringApplicationBuilder()).build().run(args);
	}

	protected static SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PearlJamApplication.class).listeners(new PropertiesLogger());
	}

	// public static void setProperties() {
	// System.setProperty("spring.config.location",
	// "classpath:/,"
	// + "file:///${catalina.base}/webapps/pearljambo.properties");

	// System.setProperty("spring.config.additional-location",
	// "classpath:/,"
	// + "file:///${catalina.base}/webapps/sabcolbo.properties");

	// }

	@EventListener
	public void handleApplicationReady(ApplicationReadyEvent event) {
		log.info("=============== Pearl-Jam Back-Office has successfully started. ===============");
	}

}
