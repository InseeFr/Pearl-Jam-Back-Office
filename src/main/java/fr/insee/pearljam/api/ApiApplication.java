package fr.insee.pearljam.api;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.service.impl.DataSetInjectorServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(scanBasePackages = "fr.insee.pearljam.api")
@EnableJpaRepositories(basePackageClasses = SurveyUnitRepository.class)
@RequiredArgsConstructor
@Slf4j
public class ApiApplication extends SpringBootServletInitializer {

	private final DataSetInjectorServiceImpl injector;

	@Value("${spring.profiles.active}")
	private String profile;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ApiApplication.class);
		app.run(args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		setProperties();
		return application.sources(ApiApplication.class);
	}

	public static void setProperties() {
		System.setProperty("spring.config.location",
				"classpath:/,"
						+ "file:///${catalina.base}/webapps/pearljambo.properties");

		System.setProperty("spring.config.additional-location",
				"classpath:/,"
						+ "file:///${catalina.base}/webapps/sabcolbo.properties");

	}

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {

		final Environment env = event.getApplicationContext().getEnvironment();
		log.info("================================ Properties =================================");
		final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
		StreamSupport.stream(sources.spliterator(), false)
				.filter(EnumerablePropertySource.class::isInstance)
				.map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
				.flatMap(Arrays::stream)
				.distinct()
				.filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
				.filter(prop -> prop.startsWith("fr.insee") || prop.startsWith("logging") || prop.startsWith("keycloak")
						|| prop.startsWith("spring") || prop.startsWith("application"))
				.sorted()
				.forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
		log.info("============================================================================");
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		if (profile.contains("dev") && !profile.contains("test")) {
			injector.createDataSet();
		}
	}

}
