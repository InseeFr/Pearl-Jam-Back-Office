package fr.insee.pearljam.api;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.sql.SQLException;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.insee.pearljam.api.domain.CommentType;
import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.Status;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
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

	@LocalServerPort
	int port;

	private Liquibase liquibase; 

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
		get("api/survey-unit/12").then().statusCode(200).and()
		.assertThat().body("id", equalTo("12")).and()
		.assertThat().body("firstName", equalTo("Cecilia")).and()
		.assertThat().body("lastName", equalTo("Ortega")).and()
		.assertThat().body("priority", is(true)).and()
		.assertThat().body("phoneNumbers", hasItems("+3351231231231")).and()
		.assertThat().body("address.l1", equalTo("Cecilia Ortega")).and()
		.assertThat().body("address.l2", equalTo("")).and()
		.assertThat().body("address.l3", equalTo("")).and()
		.assertThat().body("address.l4", equalTo("2 place de la mairie")).and()
		.assertThat().body("address.l5", equalTo("")).and()
		.assertThat().body("address.l6", equalTo("90000 Belfort")).and()
		.assertThat().body("address.l7", equalTo("France")).and()
		.assertThat().body("geographicalLocation.id", equalTo("90010")).and()
		.assertThat().body("geographicalLocation.label", equalTo("BELFORT")).and()
		.assertThat().body("campaign", equalTo("simpsons2020x00")).and()
		.assertThat().body("lastState.id", is(2)).and()
		.assertThat().body("lastState.date", is(1588149641)).and()
		.assertThat().body("lastState.type", equalTo(StateType.ANS.toString())).and()
		.assertThat().body("contactOutcome", nullValue()).and()
		.assertThat().body("comments", empty()).and()
		.assertThat().body("states", empty()).and()
		.assertThat().body("contactAttempts", empty());
		
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(2)
	public void testGetAllSurveyUnit() throws InterruptedException {
		get("api/survey-units/").then().statusCode(200).and()
		.assertThat().body("id", hasItem("11")).and()
		.assertThat().body("campaign", hasItem("simpsons2020x00")).and()
		.assertThat().body("campaignLabel",  hasItem("Survey on the Simpsons tv show 2020")).and()
		.assertThat().body("collectionStartDate",hasItem(1577836800) ).and()
		.assertThat().body("collectionEndDate", hasItem(1640995200));
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
	
	
		

	@Test
	@Order(4)
	public void testPutSurveyUnitDetail() throws InterruptedException, JsonProcessingException {
		SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitDetail("GUEST", "11");
		surveyUnitDetailDto.setFirstName("test");
		surveyUnitDetailDto.setLastName("test");
		surveyUnitDetailDto.setPhoneNumbers(List.of("+33555555555","+33666666666"));
		surveyUnitDetailDto.getAddress().setL1("test");
		surveyUnitDetailDto.getAddress().setL2("test");
		surveyUnitDetailDto.getAddress().setL3("test");
		surveyUnitDetailDto.getAddress().setL4("test");
		surveyUnitDetailDto.getAddress().setL5("test");
		surveyUnitDetailDto.getAddress().setL6("test");
		surveyUnitDetailDto.getAddress().setL7("test");
		surveyUnitDetailDto.setComments(List.of(new CommentDto(CommentType.INTERVIEWER, "test"),new CommentDto(CommentType.MANAGEMENT, "")));
		surveyUnitDetailDto.setStates(List.of(new StateDto(null, 1589268626L, StateType.AOC),new StateDto(null, 1589268800L, StateType.APS)));
		surveyUnitDetailDto.setContactAttempts(List.of(new ContactAttemptDto(1589268626L, Status.COM), new ContactAttemptDto(1589268800L, Status.BUL)));
		surveyUnitDetailDto.setContactOutcome(new ContactOutcomeDto(1589268626L, ContactOutcomeType.INI, 2));
		 given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(surveyUnitDetailDto))
		.when()
			.put("api/survey-unit/11")
		.then()
			.statusCode(200);
		Response response = get("api/survey-unit/11");
		response.then().statusCode(200).and()
		.assertThat().body("id", equalTo("11")).and()
		.assertThat().body("firstName", equalTo("test")).and()
		.assertThat().body("lastName", equalTo("test")).and()
		.assertThat().body("phoneNumbers", hasItems("+33555555555", "+33666666666")).and()
		.assertThat().body("address.l1", equalTo("test")).and()
		.assertThat().body("address.l2", equalTo("test")).and()
		.assertThat().body("address.l3", equalTo("test")).and()
		.assertThat().body("address.l4", equalTo("test")).and()
		.assertThat().body("address.l5", equalTo("test")).and()
		.assertThat().body("address.l6", equalTo("test")).and()
		.assertThat().body("address.l7", equalTo("test")).and()
		.assertThat().body("contactOutcome.date", is(1589268626)).and()
		.assertThat().body("contactOutcome.type", equalTo(ContactOutcomeType.INI.toString())).and()
		.assertThat().body("contactOutcome.totalNumberOfContactAttempts", is(2)).and()
		.assertThat().body("comments[0].value", equalTo("test")).and()
		.assertThat().body("comments[0].type", equalTo(CommentType.INTERVIEWER.toString())).and()
		.assertThat().body("comments[1].value", blankOrNullString()).and()
		.assertThat().body("comments[1].type", equalTo(CommentType.MANAGEMENT.toString())).and()
		.assertThat().body("contactAttempts[0].date", is(1589268626)).and()
		.assertThat().body("contactAttempts[0].status", equalTo(Status.COM.toString())).and()
		.assertThat().body("contactAttempts[1].date", is(1589268800)).and()
		.assertThat().body("contactAttempts[1].status", equalTo(Status.BUL.toString()));
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 404
	 * @throws InterruptedException
	 */
	@Test
	@Order(5)
	public void testGetAllSurveyUnitNotFound() throws InterruptedException, SQLException, LiquibaseException {
		PGSimpleDataSource ds = new PGSimpleDataSource();
		// Datasource initialization
		ds.setUrl(postgreSQLContainer.getJdbcUrl());
		ds.setUser(postgreSQLContainer.getUsername());
		ds.setPassword(postgreSQLContainer.getPassword());
		DatabaseConnection dbconn = new JdbcConnection(ds.getConnection());
		ResourceAccessor ra = new FileSystemResourceAccessor("src/main/resources/db/changelog");
		liquibase = new Liquibase("000_init.xml", ra, dbconn);
		liquibase.dropAll();
		liquibase.update(new Contexts());
		get("api/survey-units/")
		.then()
		.statusCode(404);
	}
}
