package fr.insee.pearljam.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.insee.pearljam.api.repository.SurveyUnitRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = SurveyUnitRepository.class)
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
