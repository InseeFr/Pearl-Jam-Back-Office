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
import java.util.Optional;

import org.hamcrest.Matchers;
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

import fr.insee.pearljam.api.controller.WsText;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.CommentType;
import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.Message;
import fr.insee.pearljam.api.domain.MessageStatusType;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.Status;
import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
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
	UserService userService;
	
	@Autowired
	SurveyUnitRepository surveyUnitRepository;
  
	@Autowired
	CampaignRepository campaignRepository;
  
	@Autowired
	VisibilityRepository visibilityRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
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
//	@Test
//	@Order(1)
//	public void testGetUser() throws InterruptedException {
//		given().when().get("api/user").then().statusCode(200).and()
//		.assertThat().body("id", equalTo("")).and()
//		.assertThat().body("firstName", equalTo("Guest")).and()
//		.assertThat().body("lastName", equalTo("")).and()
//		.assertThat().body("organizationUnit.id", equalTo("OU-NATIONAL")).and()
//		.assertThat().body("organizationUnit.label", equalTo("National organizational unit")).and()
//		.assertThat().body("localOrganizationUnits[0].id", equalTo("OU-NORTH"));
//	}
	
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return String 'Guest'.
	 * @throws InterruptedException
	 */
//	@Test
//	@Order(2)
//	public void testGetUserNotFound() throws InterruptedException {
//		UserDto user = userService.getUser("test");
//		assertTrue(user.getFirstName().equals("Guest"));
//	}

	/*CampaignController*/
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 404
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(3)
//	public void testGetCampaign() throws InterruptedException, JSONException {
//		given().when().get("api/campaigns").then().statusCode(200).and().assertThat().body("id", hasItem("simpsons2020x00")).and()
//		.assertThat().body("label", hasItem("Survey on the Simpsons tv show 2020")).and()
//		.assertThat().body("collectionStartDate",hasItem(1577836800000L)).and()
//		.assertThat().body("collectionEndDate", hasItem(1622035845000L)).and()
//		.assertThat().body("treatmentEndDate",hasItem(1622025045000L)).and()
//		.assertThat().body("allocated",hasItem(4)).and()
//		.assertThat().body("toAffect",hasItem(0)).and()
//		.assertThat().body("toFollowUp",hasItem(0)).and()
//		.assertThat().body("toReview",hasItem(0)).and()
//		.assertThat().body("finalized",hasItem(0)).and()
//		.assertThat().body("toProcessInterviewer",hasItem(0)).and()
//		.assertThat().body("preference",hasItem(true));
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/interviewers"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(4)
//	public void testGetCampaignInterviewer() throws InterruptedException, JSONException {
//		given().when().get("api/campaign/simpsons2020x00/interviewers").then().statusCode(200).and()
//		.assertThat().body("id", hasItem("INTW1")).and()
//		.assertThat().body("interviewerFirstName",hasItem("Margie")).and()
//		.assertThat().body("interviewerLastName", hasItem("Lucas")).and()
//		.assertThat().body("surveyUnitCount",hasItem(2));
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/interviewers"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(5)
//	public void testGetCampaignInterviewerNotFound() throws InterruptedException, JSONException {
//		given().when().get("api/campaign/simpsons2020x000000/interviewers").then().statusCode(404);
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/state-count"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(6)
//	public void testGetCampaignStateCount() throws InterruptedException, JSONException {
//		given().when().get("api/campaign/simpsons2020x00/survey-units/state-count").then().statusCode(200).and()
//    .assertThat().body("organizationUnits.idDem", hasItem("OU-NORTH")).and()
//    .assertThat().body("organizationUnits[0].nnsCount",equalTo(0)).and()
//    .assertThat().body("organizationUnits[0].ansCount",equalTo(4)).and()
//		.assertThat().body("organizationUnits[0].vicCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].prcCount", equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].aocCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].apsCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].insCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].wftCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].wfsCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].tbrCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].finCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].nviCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].nvmCount",equalTo(0)).and()
//		.assertThat().body("organizationUnits[0].total",equalTo(4));
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/state-count"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(7)
//	public void testGetCampaignStateCountNotFound() throws InterruptedException, JSONException {
//		given().when().get("api/campaign/test/survey-units/state-count").then().statusCode(404);
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(8)
//	public void testGetCampaignInterviewerStateCount() throws InterruptedException, JSONException {
//		given().when().get("api/campaign/simpsons2020x00/survey-units/interviewer/INTW1/state-count").then().statusCode(200).and()
//		.assertThat().body("idDem", equalTo(null)).and()
//    .assertThat().body("nnsCount", equalTo(0)).and()
//    .assertThat().body("ansCount", equalTo(2)).and()
//    .assertThat().body("vicCount", equalTo(0)).and()
//		.assertThat().body("prcCount", equalTo(0)).and()
//		.assertThat().body("aocCount",equalTo(0)).and()
//		.assertThat().body("apsCount",equalTo(0)).and()
//		.assertThat().body("insCount",equalTo(0)).and()
//		.assertThat().body("wftCount",equalTo(0)).and()
//		.assertThat().body("wfsCount",equalTo(0)).and()
//		.assertThat().body("tbrCount",equalTo(0)).and()
//		.assertThat().body("finCount",equalTo(0)).and()
//		.assertThat().body("nviCount",equalTo(0)).and()
//		.assertThat().body("nvmCount",equalTo(0)).and()
//		.assertThat().body("total",equalTo(2));
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(9)
//	public void testGetCampaignInterviewerStateCountNotFoundCampaign() throws InterruptedException, JSONException {
//		given().when().get("api/campaign/simpsons2020x000000/survey-units/interviewer/INTW1/state-count").then().statusCode(404);
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when interviewer Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(10)
//	public void testGetCampaignInterviewerStateCountNotFoundIntw() throws InterruptedException, JSONException {
//		given().when().get("api/campaign/simpsons2020x00/survey-units/interviewer/test/state-count").then().statusCode(404);
//	}
	
	/*SurveyUnitController*/
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 200.
	 * @throws InterruptedException
	 */
	@Test
	@Order(11)
	public void testGetSurveyUnitDetail() throws InterruptedException {
		get("api/survey-unit/11").then().statusCode(200).and()
		.assertThat().body("id", equalTo("11")).and()
		.assertThat().body("firstName", equalTo("Ted")).and()
		.assertThat().body("lastName", equalTo("Farmer")).and()
		.assertThat().body("priority", is(true)).and()
		.assertThat().body("phoneNumbers", hasItems("+3351231231230")).and()
		.assertThat().body("address.l1", equalTo("Ted Farmer")).and()
		.assertThat().body("address.l2", equalTo("")).and()
		.assertThat().body("address.l3", equalTo("")).and()
		.assertThat().body("address.l4", equalTo("1 rue de la gare")).and()
		.assertThat().body("address.l5", equalTo("")).and()
		.assertThat().body("address.l6", equalTo("29270 Carhaix")).and()
		.assertThat().body("address.l7", equalTo("France")).and()
		.assertThat().body("geographicalLocation.id", equalTo("29024")).and()
		.assertThat().body("geographicalLocation.label", equalTo("CARHAIX PLOUGUER")).and()
		.assertThat().body("campaign", equalTo("simpsons2020x00")).and()
		.assertThat().body("contactOutcome", nullValue()).and()
		.assertThat().body("comments", empty()).and()
		.assertThat().body("states[0].type", equalTo("NNS")).and()
		.assertThat().body("contactAttempts", empty());
		
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
		.assertThat().body("collectionStartDate",hasItem(1577836800000L));
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
		surveyUnitDetailDto.setStates(List.of(new StateDto(1L, 1590504459838L, StateType.NNS)));
		surveyUnitDetailDto.setContactAttempts(List.of(new ContactAttemptDto(1589268626000L, Status.NOC), new ContactAttemptDto(1589268800000L, Status.INA)));
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
		.assertThat().body("contactAttempts[0].status", equalTo(Status.NOC.toString())).and()
		.assertThat().body("contactAttempts[1].status", equalTo(Status.INA.toString()));
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
	 * Test that the PUT endpoint "api/survey-unit/{id}/state/{state}"
	 * return 200
 	 * @throws InterruptedException
	 */
//	@Test
//	@Order(20)
//	public void testPutSurveyUnitState() throws InterruptedException, JSONException, JsonProcessingException {
//		 given()
//     .contentType("application/json")
//     .when()
//       .put("api/survey-unit/12/state/NVI")
//     .then()
//       .statusCode(200);
//	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state/{state}"
	 * return 400 with unknown state
 	 * @throws InterruptedException
	 */
//	@Test
//	@Order(21)
//	public void testPutSurveyUnitStateStateFalse() throws InterruptedException, JSONException, JsonProcessingException {
//		 given()
//      .contentType("application/json")
//      .when()
//        .put("api/survey-unit/11/state/test")
//      .then()
//        .statusCode(400);
//	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state/{state}"
	 * return 403 when not allowed to pass to this state
 	 * @throws InterruptedException
	 */
//	@Test
//	@Order(22)
//	public void testPutSurveyUnitStateNoSu() throws InterruptedException, JSONException, JsonProcessingException {
//		 given()
//     .contentType("application/json")
//     .when()
//       .put("api/survey-unit/11/state/AOC")
//     .then()
//       .statusCode(403);
//	}
	

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
//	@Test
//	@Order(24)
//	public void testPutPreferencesWrongCampaignId() throws InterruptedException, JSONException, JsonProcessingException {
//		List<String> listPreferences = new ArrayList<>();
//		listPreferences.add("");
//		 given()
//		 	.contentType("application/json")
//			.body(new ObjectMapper().writeValueAsString(listPreferences))
//		.when()
//			.put("api/preferences")
//		.then()
//			.statusCode(404);
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(25)
//	public void testGetNbSuAbandoned() throws InterruptedException, JSONException {
//		get("api/campaign/simpsons2020x00/survey-units/abandoned")
//		.then()
//		.statusCode(200).and()
//		.assertThat().body("count", equalTo(0));
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(26)
//	public void testGetNbSuAbandonedNotFound() throws InterruptedException, JSONException {
//		get("api/campaign/test/survey-units/abandoned")
//		.then()
//		.statusCode(404);
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/not-attributed"
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(27)
//	public void testGetNbSuNotAttributed() throws InterruptedException, JSONException {
//		get("api/campaign/simpsons2020x00/survey-units/not-attributed")
//		.then()
//		.statusCode(200).and()
//		.assertThat().body("count", equalTo(0));
//	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
//	@Test
//	@Order(28)
//	public void testGetNbSuNotAttributedNotFound() throws InterruptedException, JSONException {
//		get("api/campaign/test/survey-units/not-attributed")
//		.then()
//		.statusCode(404);
//  }
  

  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 200 when modifying both dates
	 * @throws InterruptedException
	 */
	@Test
	@Order(29)
	public void testPutCollectionDatesModifyBothDates() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{\"startDate\": 162849200000, \"endDate\": 170849200000}")
		.when()
			.put("api/campaign/simpsons2020x00/collection-dates")
		.then()
      .statusCode(200);
    Optional<Campaign> simpsons = campaignRepository.findByIdIgnoreCase("simpsons2020x00");
    assertEquals(simpsons.isPresent(), true);
    assertEquals(simpsons.get().getStartDate(), 162849200000L);
    assertEquals(simpsons.get().getEndDate(), 170849200000L);
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 200 when modifying start date
	 * @throws InterruptedException
	 */
	@Test
	@Order(30)
	public void testPutCollectionDatesModifyStartDate() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{\"startDate\": 162849200000}")
		.when()
			.put("api/campaign/simpsons2020x00/collection-dates")
		.then()
      .statusCode(200);
    Optional<Campaign> simpsons = campaignRepository.findByIdIgnoreCase("simpsons2020x00");
    assertEquals(simpsons.isPresent(), true);
    assertEquals(simpsons.get().getStartDate(), 162849200000L);
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 200 when modifying end date
	 * @throws InterruptedException
	 */
	@Test
	@Order(31)
	public void testPutCollectionDatesModifyEndDate() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{\"endDate\": 170849200000}")
		.when()
			.put("api/campaign/simpsons2020x00/collection-dates")
		.then()
      .statusCode(200);
    Optional<Campaign> simpsons = campaignRepository.findByIdIgnoreCase("simpsons2020x00");
    assertEquals(simpsons.isPresent(), true);
    assertEquals(simpsons.get().getEndDate(), 170849200000L);
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 400 when empty body
	 * @throws InterruptedException
	 */
	@Test
	@Order(32)
	public void testPutCollectionDatesEmptyBody() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{}")
		.when()
			.put("api/campaign/simpsons2020x00/collection-dates")
		.then()
      .statusCode(400);
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 400 when bad format
	 * @throws InterruptedException
	 */
	@Test
	@Order(33)
	public void testPutCollectionDatesBadFormat() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{\"startDate\": 162849200000, \"endDate\": \"23/05/2020\"}")
		.when()
			.put("api/campaign/simpsons2020x00/collection-dates")
		.then()
      .statusCode(400);
  }

	/**
	 * Test that the PUT endpoint "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying all dates
	 * @throws InterruptedException
	 */
	@Test
	@Order(34)
	public void testPutVisibilityModifyAllDates() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{\"managementStartDate\": 1640996200000, "
					+ "\"interviewerStartDate\": 1577837800000,"
					+ "\"identificationPhaseStartDate\": 1641514600000,"
					+ "\"collectionStartDate\": 1577233000000,"
					+ "\"collectionEndDate\": 1576801000000,"
					+ "\"endDate\": 1575937000000}")
		.when()
			.put("api/campaign/simpsons2020x00/organizational-unit/OU-NORTH/visibility")
		.then()
      .statusCode(200);
    Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("simpsons2020x00", "OU-NORTH");
    assertEquals(true, visi.isPresent());
    assertEquals(1640996200000L, visi.get().getManagementStartDate());
    assertEquals(1577837800000L, visi.get().getInterviewerStartDate());
    assertEquals(1641514600000L, visi.get().getIdentificationPhaseStartDate());
    assertEquals(1577233000000L, visi.get().getCollectionStartDate());
    assertEquals(1576801000000L, visi.get().getCollectionEndDate());
    assertEquals(1575937000000L, visi.get().getEndDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying start date
	 * @throws InterruptedException
	 */
	@Test
	@Order(35)
	public void testPutVisibilityModifyCollectionStartDate() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{\"collectionStartDate\": 1577233000000}")
		.when()
    .put("api/campaign/simpsons2020x00/organizational-unit/OU-NORTH/visibility")
		.then()
      .statusCode(200);
    Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("simpsons2020x00", "OU-NORTH");
    assertEquals(true, visi.isPresent());
    assertEquals(1577233000000L, visi.get().getCollectionStartDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying end date
	 * @throws InterruptedException
	 */
	@Test
	@Order(36)
	public void testPutVisibilityModifyCollectionEndDate() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{\"collectionEndDate\": 1577233000000}")
		.when()
			.put("api/campaign/simpsons2020x00/organizational-unit/OU-NORTH/visibility")
		.then()
      .statusCode(200);
    Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("simpsons2020x00", "OU-NORTH");
    assertEquals(true, visi.isPresent());
    assertEquals(1577233000000L, visi.get().getCollectionEndDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 400 when empty body
	 * @throws InterruptedException
	 */
	@Test
	@Order(37)
	public void testPutVisibilityEmptyBody() throws InterruptedException, JsonProcessingException, JSONException {
		given()
		 	.contentType("application/json")
			.body("{}")
		.when()
    .put("api/campaign/simpsons2020x00/organizational-unit/OU-NORTH/visibility")
		.then()
      .statusCode(400);
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 400 when bad format
	 * @throws InterruptedException
	 */
	@Test
	@Order(38)
	public void testPutVisibilityBadFormat() throws InterruptedException, JsonProcessingException, JSONException {
		given()
	 	.contentType("application/json")
		.body("{\"managementStartDate\": 1640996200000, "
				+ "\"interviewerStartDate\": \"10/10/2020\","
				+ "\"identificationPhaseStartDate\": 1641514600000,"
				+ "\"collectionStartDate\": 1577233000000,"
				+ "\"collectionEndDate\": 1576801000000,"
				+ "\"endDate\": 1575937000000}")
		.when()
      .put("api/campaign/simpsons2020x00/organizational-unit/OU-NORTH/visibility")
		.then()
      .statusCode(400);
	}
	
	/**
	 * Test that the POST endpoint
	 * "api/message" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(39)
	public void testPostMessage() throws InterruptedException, JsonProcessingException, JSONException {
		List<String> recipients = new ArrayList<String>();
		recipients.add("INTW1");
		MessageDto message = new MessageDto("TEST", recipients);
		given().contentType("application/json").body(new ObjectMapper().writeValueAsString(message)).when()
				.post("api/message").then().statusCode(200);
		List<MessageDto> messages = messageRepository.findMessagesDtoByIds(messageRepository.getMessageIdsByInterviewer("INTW1"));
		assertEquals("TEST", messages.get(0).getText());
	}
	
	/**
	 * Test that the POST endpoint
	 * "api/message" return 400 when bad format
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(40)
	public void testPostMessageBadFormat() throws InterruptedException, JsonProcessingException, JSONException {
		given().contentType("application/json").body(new ObjectMapper().writeValueAsString(null)).when()
				.post("api/message").then().statusCode(400);
	}
	
	/**
	 * Test that the GET endpoint
	 * "api/messages/{id}" return empty body in NoAuth Mode
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(41)
	public void testGetMessageGuest() throws InterruptedException, JsonProcessingException, JSONException {
		given().when().get("api/messages/GUEST").then().statusCode(200).and()
		.assertThat().body("isEmpty()", Matchers.is(true));		
	}
	
	/**
	 * Test that the GET endpoint
	 * "api/messages/{id}" return empty body with a wrong id
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(42)
	public void testGetMessageWrongId() throws InterruptedException, JsonProcessingException, JSONException {
		given().when().get("api/messages/123456789").then().statusCode(200).and()
		.assertThat().body("isEmpty()", Matchers.is(true));		
	}
	
	/**
	 * Test that the PUT endpoint
	 * "/message/{id}/interviewer/{idep}/read" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(43)
	public void testPutMessageAsRead() throws InterruptedException, JsonProcessingException, JSONException {
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		given().contentType("application/json").when()
				.put("api/message/"+messageId+"/interviewer/INTW1/read").then().statusCode(200);
		Optional<Message> message = messageRepository.findById(messageId);
		assertEquals(message.get().getMessageStatus().get(0).getStatus(), MessageStatusType.REA);
	}
	
	/**
	 * Test that the PUT endpoint
	 * "/message/{id}/interviewer/{idep}/read" return 404 with a wrong Id
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(44)
	public void testPutMessageAsReadWrongId() throws InterruptedException, JsonProcessingException, JSONException {
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		given().contentType("application/json").when()
				.put("api/message/"+messageId+"/interviewer/Test/read").then().statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint
	 * "/message-history" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(45)
	public void testGetMessageHistory() throws InterruptedException, JsonProcessingException, JSONException {
		given().when().get("api/message-history").then().statusCode(200).and()
		.assertThat().body("text", hasItem("TEST"));		
	}
	
	/**
	 * Test that the POST endpoint
	 * "/verify-name" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(46)
	public void testPostVerifyName() throws InterruptedException, JsonProcessingException, JSONException {
		WsText message = new WsText("INTW1");
		given().contentType("application/json").body(new ObjectMapper().writeValueAsString(message)).when()
				.post("api/verify-name").then().statusCode(200).and()
				.assertThat().body("id", hasItem("INTW1"));		
	}
}
