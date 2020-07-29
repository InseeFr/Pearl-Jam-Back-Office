package fr.insee.pearljam.api.noAuth;

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
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
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
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UserService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

/* Test class for no Authentication */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({ "test" })
@ContextConfiguration(initializers = { TestNoAuth.Initializer.class })
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties= {"fr.insee.pearljam.application.mode = NoAuth"})
public class TestNoAuth {

	@Autowired
	SurveyUnitService surveyUnitService;

	@Autowired
	InterviewerService interviewerService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	SurveyUnitRepository surveyUnitRepository;
	
	@LocalServerPort
	int port;

	public Liquibase liquibase;

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
	@ClassRule
	public static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
			.withDatabaseName("pearljam").withUsername("pearljam").withPassword("pearljam");

	
	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues
					.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
							"spring.datasource.username=" + postgreSQLContainer.getUsername(),
							"spring.datasource.password=" + postgreSQLContainer.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
		
	/*UserController*/
	
	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(1)
	public void testGetUser() throws InterruptedException {
		given().when().get("api/user").then().statusCode(200).and()
		.assertThat().body("id", equalTo("")).and()
		.assertThat().body("firstName", equalTo("Guest")).and()
		.assertThat().body("lastName", equalTo("")).and()
		.assertThat().body("organizationUnits.id", equalTo(null)).and()
		.assertThat().body("organizationUnits.label", equalTo(null)).and()
		.assertThat().body("localOrganisationUnits", equalTo(null));
	}
	
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return String 'Guest'.
	 * @throws InterruptedException
	 */
	@Test
	@Order(2)
	public void testGetUserNotFound() throws InterruptedException {
		UserDto user = userService.getUser("test");
		assertTrue(user.getFirstName().equals("Guest"));
	}

	/*CampaignController*/
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 404
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(3)
	public void testGetCampaign() throws InterruptedException, JSONException {
		given().when().get("api/campaigns").then().statusCode(200).and().assertThat().body("id", hasItem("simpsons2020x00")).and()
		.assertThat().body("label", hasItem("Survey on the Simpsons tv show 2020")).and()
		.assertThat().body("collectionStartDate",hasItem(1577836800000L)).and()
		.assertThat().body("collectionEndDate", hasItem(1622035845000L)).and()
		.assertThat().body("[0].visibilityStartDate",equalTo(null)).and()
		.assertThat().body("[0].treatmentEndDate",equalTo(null)).and()
		.assertThat().body("affected",hasItem(4)).and()
		.assertThat().body("toAffect",hasItem(0)).and()
		.assertThat().body("inProgress",hasItem(0)).and()
		.assertThat().body("toControl",hasItem(0)).and()
		.assertThat().body("terminated",hasItem(0)).and()
		.assertThat().body("toFollowUp",hasItem(0)).and()
		.assertThat().body("preference",hasItem(false));
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/interviewers"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(4)
	public void testGetCampaignInterviewer() throws InterruptedException, JSONException {
		given().when().get("api/campaigns/simpsons2020x00/interviewers").then().statusCode(200).and()
		.assertThat().body("id", hasItem("INTW1")).and()
		.assertThat().body("interviewerFirstName",hasItem("Margie")).and()
		.assertThat().body("interviewerLastName", hasItem("Lucas")).and()
		.assertThat().body("surveyUnitCount",hasItem(2));
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/interviewers"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(5)
	public void testGetCampaignInterviewerNotFound() throws InterruptedException, JSONException {
		given().when().get("api/campaigns/simpsons2020x000000/interviewers").then().statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units/state-count"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(6)
	public void testGetCampaignStateCount() throws InterruptedException, JSONException {
		given().when().get("api/campaigns/simpsons2020x00/survey-units/state-count").then().statusCode(200).and()
		.assertThat().body("organizationUnits.idDem", hasItem("OU-NORTH")).and()
		.assertThat().body("organizationUnits[1].ansCount",equalTo(4)).and()
		.assertThat().body("organizationUnits[1].prcCount", equalTo(0)).and()
		.assertThat().body("organizationUnits[1].aocCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].apsCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].insCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].wftCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].wfsCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].tbrCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].finCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].nviCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].nvmCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[1].total",equalTo(4));
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units/state-count"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(7)
	public void testGetCampaignStateCountNotFound() throws InterruptedException, JSONException {
		given().when().get("api/campaigns/test/survey-units/state-count").then().statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units/interviewer/{id}/state-count"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(8)
	public void testGetCampaignInterviewerStateCount() throws InterruptedException, JSONException {
		given().when().get("api/campaigns/simpsons2020x00/survey-units/interviewer/INTW1/state-count").then().statusCode(200).and()
		.assertThat().body("idDem", equalTo(null)).and()
		.assertThat().body("ansCount", equalTo(2)).and()
		.assertThat().body("prcCount", equalTo(0)).and()
		.assertThat().body("aocCount",equalTo(0)).and()
		.assertThat().body("apsCount",equalTo(0)).and()
		.assertThat().body("insCount",equalTo(0)).and()
		.assertThat().body("wftCount",equalTo(0)).and()
		.assertThat().body("wfsCount",equalTo(0)).and()
		.assertThat().body("tbrCount",equalTo(0)).and()
		.assertThat().body("finCount",equalTo(0)).and()
		.assertThat().body("nviCount",equalTo(0)).and()
		.assertThat().body("nvmCount",equalTo(0)).and()
		.assertThat().body("total",equalTo(2));
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(9)
	public void testGetCampaignInterviewerStateCountNotFoundCampaign() throws InterruptedException, JSONException {
		given().when().get("api/campaigns/simpsons2020x000000/survey-units/interviewer/INTW1/state-count").then().statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when interviewer Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(10)
	public void testGetCampaignInterviewerStateCountNotFoundIntw() throws InterruptedException, JSONException {
		given().when().get("api/campaigns/simpsons2020x00/survey-units/interviewer/test/state-count").then().statusCode(404);
	}
	
	/*SurveyUnitController*/
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 200.
	 * @throws InterruptedException
	 */
	@Test
	@Order(11)
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
	@Order(12)
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
	 * return 404 when survey-unit is false
	 * @throws InterruptedException
	 */
	@Test
	@Order(13)
	public void testGetSurveyUnitDetailNotFound() throws InterruptedException {
		get("api/survey-unit/123456789")
		.then()
		.statusCode(404);
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(14)
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
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 404 with false survey-unit Id
 	 * @throws InterruptedException
	 */
	@Test
	@Order(15)
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
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 404 with state null
 	 * @throws InterruptedException
	 */
	@Test
	@Order(16)
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
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 404 with address null
 	 * @throws InterruptedException
	 */
	@Test
	@Order(17)
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
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 404 with first name empty
 	 * @throws InterruptedException
	 */
	@Test
	@Order(18)
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
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 404 with last name empty
 	 * @throws InterruptedException
	 */
	@Test
	@Order(19)
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
	 * Test that the PUT endpoint "api/survey-unit/state/{state}"
	 * return 200
 	 * @throws InterruptedException
	 */
	@Test
	@Order(20)
	public void testGetPutSurveyUnitState() throws InterruptedException, JSONException, JsonProcessingException {
		List<String> listSu = new ArrayList<>();
		listSu.add("11");
		 given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listSu))
		.when()
			.put("api/survey-units/state/NVM")
		.then()
			.statusCode(200);
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 404 with false state
 	 * @throws InterruptedException
	 */
	@Test
	@Order(21)
	public void testGetPutSurveyUnitStateStateFalse() throws InterruptedException, JSONException, JsonProcessingException {
		List<String> listSu = new ArrayList<>();
		listSu.add("11");
		 given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listSu))
		.when()
			.put("api/survey-units/state/test")
		.then()
			.statusCode(400);
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 404 with no survey-unit
 	 * @throws InterruptedException
	 */
	@Test
	@Order(22)
	public void testGetPutSurveyUnitStateNoSu() throws InterruptedException, JSONException, JsonProcessingException {
		List<String> listSu = new ArrayList<>();
		listSu.add("");
		 given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listSu))
		.when()
			.put("api/survey-units/state/NVM")
		.then()
			.statusCode(400);
	}
	

	/**
	 * Test that the PUT endpoint "api/preferences"
	 * return 200
 	 * @throws InterruptedException
	 */
	@Test
	@Order(23)
	public void testPutPreferences() throws InterruptedException, JSONException, JsonProcessingException {
		List<String> listPreferences = new ArrayList<>();
		listPreferences.add("simpsons2020x00");
		 given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listPreferences))
		.when()
			.put("api/preferences")
		.then()
			.statusCode(404);
	}
	
	/**
	 * Test that the PUT endpoint "api/preferences"
	 * return 200
 	 * @throws InterruptedException
	 */
	@Test
	@Order(24)
	public void testPutPreferencesWrongCampaignId() throws InterruptedException, JSONException, JsonProcessingException {
		List<String> listPreferences = new ArrayList<>();
		listPreferences.add("");
		 given()
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listPreferences))
		.when()
			.put("api/preferences")
		.then()
			.statusCode(404);
	}
}
