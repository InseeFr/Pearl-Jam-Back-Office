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
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
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

import dasniko.testcontainers.keycloak.KeycloakContainer;
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
import fr.insee.pearljam.api.service.UserService;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties= {"fr.insee.pearljam.application.mode = KeyCloak"})
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
	UserService userService;
	
	@Autowired
	SurveyUnitRepository surveyUnitRepository;

	@Container
	public static KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("realm.json");
	
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
							"spring.datasource.password=" + postgreSQLContainer.getPassword(),
					"keycloak.auth-server-url=" + keycloak.getAuthServerUrl())
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
		.assertThat().body("lastState.type", equalTo(StateType.ANS.toString())).and()
		.assertThat().body("contactOutcome", nullValue()).and()
		.assertThat().body("comments", empty()).and()
		.assertThat().body("states", empty()).and()
		.assertThat().body("contactAttempts", empty());
		assertEquals(Long.valueOf(1590504468838L), get("api/survey-unit/12").then().extract().jsonPath().getLong("lastState.date"));
		
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
		.assertThat().body("collectionStartDate",hasItem(1577836800000L)).and()
		.assertThat().body("collectionEndDate", hasItem(1622035845000L));
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
		surveyUnitDetailDto.setContactAttempts(List.of(new ContactAttemptDto(1589268626000L, Status.COM), new ContactAttemptDto(1589268800000L, Status.BUL)));
		surveyUnitDetailDto.setContactOutcome(new ContactOutcomeDto(1589268626000L, ContactOutcomeType.INI, 2));
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
		.assertThat().body("contactOutcome.type", equalTo(ContactOutcomeType.INI.toString())).and()
		.assertThat().body("contactOutcome.totalNumberOfContactAttempts", is(2)).and()
		.assertThat().body("comments[0].value", equalTo("test")).and()
		.assertThat().body("comments[0].type", equalTo(CommentType.INTERVIEWER.toString())).and()
		.assertThat().body("comments[1].value", blankOrNullString()).and()
		.assertThat().body("comments[1].type", equalTo(CommentType.MANAGEMENT.toString())).and()
		.assertThat().body("contactAttempts[0].status", equalTo(Status.COM.toString())).and()
		.assertThat().body("contactAttempts[1].status", equalTo(Status.BUL.toString()));
		//Tests with Junit for Long values
		assertEquals(Long.valueOf(1589268626000L), response.then().extract().jsonPath().getLong("contactOutcome.date"));
		assertEquals(Long.valueOf(1589268626000L), response.then().extract().jsonPath().getLong("contactAttempts[0].date"));
		assertEquals(Long.valueOf(1589268800000L), response.then().extract().jsonPath().getLong("contactAttempts[1].date"));

	}
	
	@Test
	@Order(5)
	public void testPutSurveyUnitDetailErrorOnIds() throws InterruptedException, JsonProcessingException {
		SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitDetail("GUEST", "11");
		surveyUnitDetailDto.setStates(List.of(new StateDto(null, 1589268626L, StateType.AOC),new StateDto(null, 1589268800L, StateType.APS)));
		given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(surveyUnitDetailDto))
		.when()
			.put("api/survey-unit/12")
		.then()
			.statusCode(400);
	}
	
	@Test
	@Order(6)
	public void testPutSurveyUnitDetailErrorOnStates() throws InterruptedException, JsonProcessingException {
		SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitDetail("GUEST", "11");
		surveyUnitDetailDto.setStates(List.of());
		given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(surveyUnitDetailDto))
		.when()
			.put("api/survey-unit/11")
		.then()
			.statusCode(400);
	}
	
	@Test
	@Order(7)
	public void testPutSurveyUnitDetailErrorOnAddress() throws InterruptedException, JsonProcessingException {
		SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitDetail("GUEST", "11");
		surveyUnitDetailDto.setAddress(null);
		surveyUnitDetailDto.setStates(List.of(new StateDto(null, 1589268626L, StateType.AOC),new StateDto(null, 1589268800L, StateType.APS)));
		given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(surveyUnitDetailDto))
		.when()
			.put("api/survey-unit/11")
		.then()
			.statusCode(400);
	}
	
	@Test
	@Order(8)
	public void testPutSurveyUnitDetailErrorOnFirstName() throws InterruptedException, JsonProcessingException {
		SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitDetail("GUEST", "11");
		surveyUnitDetailDto.setFirstName("");
		surveyUnitDetailDto.setStates(List.of(new StateDto(null, 1589268626L, StateType.AOC),new StateDto(null, 1589268800L, StateType.APS)));
		given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(surveyUnitDetailDto))
		.when()
			.put("api/survey-unit/11")
		.then()
			.statusCode(400);
	}
	
	@Test
	@Order(9)
	public void testPutSurveyUnitDetailErrorOnLastName() throws InterruptedException, JsonProcessingException {
		SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitDetail("GUEST", "11");
		surveyUnitDetailDto.setLastName("");
		surveyUnitDetailDto.setStates(List.of(new StateDto(null, 1589268626L, StateType.AOC)));
		given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(surveyUnitDetailDto))
		.when()
			.put("api/survey-unit/11")
		.then()
			.statusCode(400);
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(2)
	public void testGetUser() throws InterruptedException {
		assertTrue(userService.getUser("jkl")!=null);
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 404.
	 * @throws InterruptedException
	 */
	@Test
	@Order(3)
	public void testGetUserNotFound() throws InterruptedException {
		assertTrue(userService.getUser("test")==null);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 404
	 * @throws InterruptedException
	 */
	@Test
	@Order(11)
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
	
	private String resourceOwnerLogin(String clientId, String clientSecret, String username, String password) throws JSONException {
        Response response =
                given().auth().preemptive().basic(clientId, clientSecret)   
                        .formParam("grant_type", "password")
                        .formParam("username", username)
                        .formParam("password", password)
                        .when()
                        .post( keycloak.getAuthServerUrl() + "/realms/insee-realm/protocol/openid-connect/token");
        

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        return accessToken;
     }
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(10)
	public void testGetCampaign() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin("pearljam-web", "212dae88-bdff-43e8-a038-8d99792c165e", "abc", "abc");
		given().auth().oauth2(accessToken).when().get("api/campaigns").then().statusCode(200).log().all();
//		given().header("Authorization", "Bearer " + accessToken, "Content-Type",  ContentType.JSON, "Accept",
//	              ContentType.JSON).when().get("api/campaigns").then().statusCode(200).log().all();

		/*
		.assertThat().body("id", hasItem("simpsons2020x00")).and()
		.assertThat().body("label", hasItem("Survey on the Simpsons tv show 2020")).and()
		.assertThat().body("collectionStartDate",hasItem(1577836800000L)).and()
		.assertThat().body("collectionEndDate", hasItem(1622035845000L)).and()
		.assertThat().body("visibilityStartDate",hasItem(1590504561350L)).and()
		.assertThat().body("treatmentEndDate",hasItem(null)).and(
		.assertThat().body("affected",hasItem(4)).and()
		.assertThat().body("toAffect",hasItem(0)).and()
		.assertThat().body("inProgress",hasItem(0)).and()
		.assertThat().body("toControl",hasItem(1590504561350L)).and()
		.assertThat().body("terminated",hasItem(1590504561350L)).and()
		.assertThat().body("toFollowUp",hasItem(1590504561350L)).and()
		.assertThat().body("preference",hasItem(true));*/

	}
}
