package fr.insee.pearljam.api;

import static io.restassured.RestAssured.get;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
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

import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import io.restassured.RestAssured;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

/**
 * Class for testing the application
 * @author scorcaud
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "test" })
@ContextConfiguration(initializers = { ApiApplicationRestAssureTests.Initializer.class })
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
class ApiApplicationRestAssureTests {
	
	@Autowired
	SurveyUnitService surveyUnitService;
	
	@Autowired
	InterviewerService interviewerService;
	
	@Autowired
	SurveyUnitRepository surveyUnitRepository;
	
	private Liquibase liquibase; 
    
	@LocalServerPort
	int port;

	/**
	 * This method set up the port of the PostgreSqlContainer
	 * @throws SQLException
	 * @throws LiquibaseException
	 */
	@BeforeEach
	public void setUp() throws SQLException, LiquibaseException {
		RestAssured.port = port;
	}

	/**
	 * Defines the configuration of the PostgreSqlContainer
	 */
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

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 200.
	 * @throws InterruptedException
	 */
	@Test
	@Order(1)
	public void testGetSurveyUnitDetail() throws InterruptedException {
		get("api/survey-unit/12")
		.then()
		.statusCode(200);
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(2)
	public void testGetAllSurveyUnit() throws InterruptedException {
		get("api/survey-units/")
		.then()
		.statusCode(200);
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 404.
	 * @throws InterruptedException
	 */
	@Test
	@Order(3)
	public void testGetSurveyUnitDetailNotFound() throws InterruptedException {
		get("api/survey-unit/123456789")
		.then()
		.statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 404
	 * @throws InterruptedException
	 */
	@Test
	@Order(4)
	public void testGetAllSurveyUnitNotFound() throws InterruptedException, SQLException, LiquibaseException {
		PGSimpleDataSource ds = new PGSimpleDataSource();
		// Datasource initialization
		ds.setUrl(postgreSQLContainer.getJdbcUrl());
		ds.setUser(postgreSQLContainer.getUsername());
		ds.setPassword(postgreSQLContainer.getPassword());
		DatabaseConnection dbconn = new JdbcConnection(ds.getConnection());
		ResourceAccessor ra = new FileSystemResourceAccessor("src/test/resources/sql/changelog");
		liquibase = new Liquibase("000_init.xml", ra, dbconn);
		liquibase.dropAll();
		liquibase.update(new Contexts());
		get("api/survey-units/")
		.then()
		.statusCode(404);
	}
}
