package fr.insee.pearljam.api;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.with;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "test" })
@ContextConfiguration(initializers = { ApiApplicationRestAssureTests.Initializer.class })
@Testcontainers
class ApiApplicationRestAssureTests {
	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
	}

	@SuppressWarnings("rawtypes")
	@Container
	public static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
			.withDatabaseName("pearljam").withUsername("pearljam").withPassword("pearljam");

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues
					.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
							"spring.datasource.username=" + postgreSQLContainer.getUsername(),
							"spring.datasource.password=" + postgreSQLContainer.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}

	@Test
	public void testGetSurveyUnitDetail() throws InterruptedException {
		get("api/survey-unit/12")
		.then()
		.statusCode(200);
	}
	
	@Test
	public void testGetAllSurveyUnit() throws InterruptedException {
	  get("api/survey-units/")
	  .then()
	  .statusCode(200);
	}
	
//	@Test
//	public void testPutSurveyUnitDetail() throws InterruptedException {
//		SurveyUnitDetailDto surveyUnitDetailDto = new SurveyUnitDetailDto();
//		with()
//			.contentType(ContentType.JSON)
//			.body(surveyUnitDetailDto)
//		.put("api/survey-unit/21")
//			.then()
//			.statusCode(200);
//		Response response = get("api/survey-unit/21");
//		response.then().statusCode(200);
//	}
}
