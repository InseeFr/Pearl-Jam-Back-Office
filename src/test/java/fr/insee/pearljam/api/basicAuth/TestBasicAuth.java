package fr.insee.pearljam.api.basicAuth;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
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
import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.Comment;
import fr.insee.pearljam.api.domain.CommentType;
import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.Message;
import fr.insee.pearljam.api.domain.MessageStatusType;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.OrganizationUnitType;
import fr.insee.pearljam.api.domain.Source;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.Status;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.campaign.CampaignContextDto;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.geographicallocation.GeographicalLocationDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.phonenumber.PhoneNumberDto;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitInterviewerLinkDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.GeographicalLocationRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

/* Test class for Basic Authentication */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({ "test" })
@ContextConfiguration(initializers = { TestBasicAuth.Initializer.class })
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties= {"fr.insee.pearljam.application.mode = basic"})
class TestBasicAuth {
	
	@Autowired
	SurveyUnitService surveyUnitService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SurveyUnitRepository surveyUnitRepository;
	
	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	GeographicalLocationRepository geographicalLocationRepository;
  
	@Autowired
	VisibilityRepository visibilityRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	OrganizationUnitRepository organizationUnitRepository;

	@Autowired
	InterviewerRepository interviewerRepository;

	@Autowired
	ClosingCauseRepository closingCauseRepository;
	
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
		given().auth().preemptive().basic("ABC", "abc").when().post("api/create-dataset");
	}
	
	/**
	 * This method is used to kill the container
	 */
	@AfterAll
	public static void  cleanUp() {
		if(postgreSQLContainer!=null) {
			postgreSQLContainer.close();
		}
	}

	/**
	 * Defines the configuration of the PostgreSqlContainer
	 */
	@SuppressWarnings("rawtypes")
	@Container
	@ClassRule
	static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
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

	/**
	 * This method is use to check if the dates are correct
	 * @param dateType
	 * @param date
	 * @return
	 */
	private boolean testingDates(String dateType, long date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		LocalDate localDateNow = LocalDate.now();
		boolean check = false;
		LocalDate value = LocalDate.parse(df.format(date));
		switch(dateType) {
			case ("managementStartDate") :
				if(value.equals(localDateNow.minusDays(4))) {
					check = true;
				}
				break;
			case ("interviewerStartDate") :
				if(value.equals(localDateNow.minusDays(3))) {
					check = true;
				}
				break;
			case ("identificationPhaseStartDate") :
				if(value.equals(localDateNow.minusDays(2))) {
					check = true;
				}
				break;
			case ("collectionStartDate") :
				if(value.equals(localDateNow.minusDays(2))) {
					check = true;
				}
				break;
			case ("collectionEndDate") :
				if(value.equals(localDateNow.plusMonths(1))) {
					check = true;
				}
				break;
			case ("endDate") :
				if(value.equals(localDateNow.plusMonths(2))) {
					check = true;
				}
				break;
			default:
				return check;
		}
		return check;
	}
	
	/*UserController*/
	
	/**
	 * Test that the GET endpoint "api/user"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(1)
	void testGetUser() throws InterruptedException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/user").then().statusCode(200).and()
		.assertThat().body("id", equalTo("ABC")).and()
		.assertThat().body("firstName", equalTo("Melinda")).and()
		.assertThat().body("lastName", equalTo("Webb")).and()
		.assertThat().body("organizationUnit.id", equalTo("OU-NORTH")).and()
		.assertThat().body("organizationUnit.label", equalTo("North region organizational unit")).and()
		.assertThat().body("localOrganizationUnits[0].id", equalTo("OU-NORTH")).and()
		.assertThat().body("localOrganizationUnits[0].label", equalTo("North region organizational unit"));
	}
	
	@Test
	@Order(1)
	void testGetContactOutcomeCountNotattributed() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when()
		.get("api/campaign/simpsons2020x00/survey-units/not-attributed/contact-outcomes").then().statusCode(200)
		.and().assertThat().body("inaCount", equalTo(0))
		.and().assertThat().body("refCount", equalTo(0))
		.and().assertThat().body("impCount", equalTo(0))
		.and().assertThat().body("iniCount", equalTo(0))
		.and().assertThat().body("alaCount", equalTo(0))
		.and().assertThat().body("wamCount", equalTo(1))
		.and().assertThat().body("oosCount", equalTo(0));
	}
	
	@Test
	@Order(1)
	void testGetCampaignInterviewerStateCountNotAttributed() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/survey-units/not-attributed/state-count").then().statusCode(200).and()
		.assertThat().body("idDem", equalTo(null)).and()
		.assertThat().body("nvmCount",equalTo(0)).and()
		.assertThat().body("nnsCount",equalTo(0)).and()
    .assertThat().body("anvCount",equalTo(0)).and()
		.assertThat().body("vinCount",equalTo(0)).and()
		.assertThat().body("vicCount",equalTo(0)).and()
		.assertThat().body("prcCount",equalTo(0)).and()
		.assertThat().body("aocCount",equalTo(0)).and()
		.assertThat().body("apsCount",equalTo(0)).and()
		.assertThat().body("insCount",equalTo(0)).and()
		.assertThat().body("wftCount",equalTo(0)).and()
		.assertThat().body("wfsCount",equalTo(0)).and()
    .assertThat().body("tbrCount",equalTo(1)).and()
		.assertThat().body("finCount",equalTo(0)).and()
		.assertThat().body("qnaCount",equalTo(0)).and()
		.assertThat().body("qnaFinCount",equalTo(0)).and()
    .assertThat().body("nvaCount",equalTo(0)).and()
    .assertThat().body("npaCount",equalTo(0)).and()
		.assertThat().body("npiCount",equalTo(0)).and()
		.assertThat().body("rowCount",equalTo(0)).and()
		.assertThat().body("total",equalTo(1));
	}
	
	/**
	 * Test that the GET endpoint "api/user"
	 * return null
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(2)
	void testGetUserNotFound() throws InterruptedException {
		assertEquals(null, userService.getUser("test"));
	}
	
	
	/*CampaignController*/
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(3)
	void testGetCampaign() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaigns").then().statusCode(200).and()
		.assertThat().body("id", hasItem("simpsons2020x00")).and()
		.assertThat().body("label", hasItem("Survey on the Simpsons tv show 2020")).and()
		.assertThat().body("allocated",hasItem(4)).and()
		.assertThat().body("toAffect",hasItem(0)).and()
		.assertThat().body("toFollowUp",hasItem(0)).and()
		.assertThat().body("toReview",hasItem(0)).and()
		.assertThat().body("finalized",hasItem(0)).and()
		.assertThat().body("toProcessInterviewer",hasItem(0)).and()
		.assertThat().body("preference",hasItem(true));
		
		//Testing dates
		assertTrue(testingDates("managementStartDate", given().auth().preemptive().basic("ABC", "abc").get("api/campaigns").path("managementStartDate[0]")));
		assertTrue(testingDates("interviewerStartDate", given().auth().preemptive().basic("ABC", "abc").get("api/campaigns").path("interviewerStartDate[0]")));
		assertTrue(testingDates("identificationPhaseStartDate", given().auth().preemptive().basic("ABC", "abc").get("api/campaigns").path("identificationPhaseStartDate[0]")));
		assertTrue(testingDates("collectionStartDate", given().auth().preemptive().basic("ABC", "abc").get("api/campaigns").path("collectionStartDate[0]")));
		assertTrue(testingDates("collectionEndDate", given().auth().preemptive().basic("ABC", "abc").get("api/campaigns").path("collectionEndDate[0]")));
		assertTrue(testingDates("endDate", given().auth().preemptive().basic("ABC", "abc").get("api/campaigns").path("endDate[0]")));
}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/interviewers"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(4)
	void testGetCampaignInterviewer() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/interviewers").then().statusCode(200).and()
		.assertThat().body("id", hasItem("INTW1")).and()
		.assertThat().body("interviewerFirstName",hasItem("Margie")).and()
		.assertThat().body("interviewerLastName", hasItem("Lucas")).and()
		.assertThat().body("surveyUnitCount",hasItem(2));
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/interviewers"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(5)
	void testGetCampaignInterviewerNotFound() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x000000/interviewers").then().statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/state-count"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(6)
	void testGetCampaignStateCount() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/survey-units/state-count").then().statusCode(200).and()
		.assertThat().body("organizationUnits.idDem", hasItem("OU-NORTH")).and()
		.assertThat().body("organizationUnits[0].nvmCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].nnsCount",equalTo(0)).and()
    .assertThat().body("organizationUnits[0].anvCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].vinCount",equalTo(1)).and()
		.assertThat().body("organizationUnits[0].vicCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].prcCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].aocCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].apsCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].insCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].wftCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].wfsCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].tbrCount",equalTo(4)).and()
		.assertThat().body("organizationUnits[0].finCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].qnaCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].qnaFinCount",equalTo(0)).and()
    .assertThat().body("organizationUnits[0].nvaCount",equalTo(0)).and()
    .assertThat().body("organizationUnits[0].npaCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].npiCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].rowCount",equalTo(0)).and()
		.assertThat().body("organizationUnits[0].total",equalTo(5));
	}
	
	@Test
	@Order(7)
	void testPutClosingCauseNoPreviousClosingCause() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().put("api/survey-unit/11/closing-cause/NPI")
		.then().statusCode(200);
		
    List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
    Assert.assertEquals(ClosingCauseType.NPI, closingCauses.get(0).getType());
    
  }
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/state-count"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(7)
	void testGetCampaignStateCountNotFound() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/test/survey-units/state-count").then().statusCode(404);
	}
	
	
	@Test
	@Order(8)
	void testPutClosingCausePreviousClosingCause() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().put("api/survey-unit/11/closing-cause/NPA")
		.then().statusCode(200);
		
    List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
    Assert.assertEquals(ClosingCauseType.NPA, closingCauses.get(0).getType());

	}
	
	@Test
	@Order(9)
	void testPutCloseSU() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().put("api/survey-unit/14/close/ROW")
		.then().statusCode(200);
		
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/survey-units/state-count")
		.then().statusCode(200).and()
		.assertThat().body("organizationUnits[0].tbrCount",equalTo(3)).and()
		.assertThat().body("organizationUnits[0].rowCount",equalTo(1));
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(8)
	void testGetCampaignInterviewerStateCount() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/survey-units/interviewer/INTW1/state-count").then().statusCode(200).and()
		.assertThat().body("idDem", equalTo(null)).and()
		.assertThat().body("nvmCount",equalTo(0)).and()
		.assertThat().body("nnsCount",equalTo(0)).and()
    .assertThat().body("anvCount",equalTo(0)).and()
		.assertThat().body("vinCount",equalTo(1)).and()
		.assertThat().body("vicCount",equalTo(0)).and()
		.assertThat().body("prcCount",equalTo(0)).and()
		.assertThat().body("aocCount",equalTo(0)).and()
		.assertThat().body("apsCount",equalTo(0)).and()
		.assertThat().body("insCount",equalTo(0)).and()
		.assertThat().body("wftCount",equalTo(0)).and()
		.assertThat().body("wfsCount",equalTo(0)).and()
    .assertThat().body("tbrCount",equalTo(1)).and()
		.assertThat().body("finCount",equalTo(0)).and()
		.assertThat().body("qnaCount",equalTo(0)).and()
		.assertThat().body("qnaFinCount",equalTo(0)).and()
    .assertThat().body("nvaCount",equalTo(0)).and()
    .assertThat().body("npaCount",equalTo(0)).and()
		.assertThat().body("npiCount",equalTo(0)).and()
		.assertThat().body("rowCount",equalTo(0)).and()
		.assertThat().body("total",equalTo(2));
	}
	
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when campaign Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(9)
	void testGetCampaignInterviewerStateCountNotFoundCampaign() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x000000/survey-units/interviewer/INTW1/state-count").then().statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when interviewer Id is false
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(10)
	void testGetCampaignInterviewerStateCountNotFoundIntw() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/survey-units/interviewer/test/state-count").then().statusCode(404);
	}
	
	
	/*SurveyUnitController*/
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 200.
	 * @throws InterruptedException
	 */
	@Test
	@Order(11)
	void testGetSurveyUnitDetail() throws InterruptedException {
		given().auth().preemptive().basic("INTW1", "intw1").when().get("api/survey-unit/11").then().statusCode(200).and()
		.assertThat().body("id", equalTo("11")).and()
		.assertThat().body("persons[2].firstName", is(oneOf("Christine", "Ted", "Louise"))).and()
		.assertThat().body("persons[2].phoneNumbers[0].number", equalTo("+33677542802")).and()
		.assertThat().body("priority", is(true)).and()
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
		.assertThat().body("states[0].type", equalTo("TBR")).and()
		.assertThat().body("contactAttempts", empty());
		
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(12)
	void testGetAllSurveyUnit() throws InterruptedException {
		given().auth().preemptive().basic("INTW1", "intw1").when().get("api/survey-units").then().statusCode(200).and()
		.assertThat().body("id", hasItem("11")).and()
		.assertThat().body("campaign", hasItem("simpsons2020x00")).and()
    .assertThat().body("campaignLabel",  hasItem("Survey on the Simpsons tv show 2020"));
    
    Response resp = given().auth().preemptive().basic("ABC", "abc").get("api/campaigns");
		//Testing dates
		assertTrue(testingDates("managementStartDate", resp.path("managementStartDate[0]")));
		assertTrue(testingDates("interviewerStartDate", resp.path("interviewerStartDate[0]")));
		assertTrue(testingDates("identificationPhaseStartDate", resp.path("identificationPhaseStartDate[0]")));
		assertTrue(testingDates("collectionStartDate", resp.path("collectionStartDate[0]")));
		assertTrue(testingDates("collectionEndDate", resp.path("collectionEndDate[0]")));
		assertTrue(testingDates("endDate", resp.path("endDate[0]")));

	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 404 when survey-unit is false
	 * @throws InterruptedException
	 */
	@Test
	@Order(13)
	void testGetSurveyUnitDetailNotFound() throws InterruptedException {
		given().auth().preemptive().basic("INTW1", "intw1").when().get("api/survey-unit/123456789")
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
	void testPutSurveyUnitDetail() throws InterruptedException, JsonProcessingException {
		SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitDetail("GUEST", "20");
		surveyUnitDetailDto.getPersons().get(0).getPhoneNumbers().get(0).setNumber("test");
		surveyUnitDetailDto.getAddress().setL1("test");
		surveyUnitDetailDto.getAddress().setL2("test");
		surveyUnitDetailDto.getAddress().setL3("test");
		surveyUnitDetailDto.getAddress().setL4("test");
		surveyUnitDetailDto.getAddress().setL5("test");
		surveyUnitDetailDto.getAddress().setL6("test");
		surveyUnitDetailDto.getAddress().setL7("test");
		surveyUnitDetailDto.setComments(List.of(new CommentDto(CommentType.INTERVIEWER, "test"),new CommentDto(CommentType.MANAGEMENT, "test")));
		surveyUnitDetailDto.setStates(List.of(new StateDto(1L, 1590504459838L, StateType.NNS)));
		surveyUnitDetailDto.setContactAttempts(List.of(new ContactAttemptDto(1589268626000L, Status.NOC), new ContactAttemptDto(1589268800000L, Status.INA)));
		surveyUnitDetailDto.setContactOutcome(new ContactOutcomeDto(1589268626000L, ContactOutcomeType.INI, 2));
    
     given().auth().preemptive().basic("INTW1", "intw1")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(surveyUnitDetailDto))
		.when()
			.put("api/survey-unit/20")
		.then()
			.statusCode(200);
      Response response = given().auth().preemptive().basic("INTW1", "intw1").when().get("api/survey-unit/20");
      response.then().statusCode(200).and()
      .assertThat().body("id", equalTo("20")).and()
      .assertThat().body("persons[0].phoneNumbers[0].number", equalTo("test")).and()
      .assertThat().body("address.l1", equalTo("test")).and()
      .assertThat().body("address.l2", equalTo("test")).and()
      .assertThat().body("address.l3", equalTo("test")).and()
      .assertThat().body("address.l4", equalTo("test")).and()
      .assertThat().body("address.l5", equalTo("test")).and()
      .assertThat().body("address.l6", equalTo("test")).and()
      .assertThat().body("address.l7", equalTo("test")).and()
      .assertThat().body("contactOutcome.type", equalTo(ContactOutcomeType.INI.toString())).and()
      .assertThat().body("contactOutcome.totalNumberOfContactAttempts", is(2)).and()
      .assertThat().body("comments[1].value", equalTo("test")).and()
      .assertThat().body("comments[1].type", is(oneOf(CommentType.MANAGEMENT.toString(),CommentType.INTERVIEWER.toString()))).and()
      .assertThat().body("contactAttempts[1].status", is(oneOf(Status.NOC.toString(),Status.INA.toString())));
      //Tests with Junit for Long values
      assertEquals(Long.valueOf(1589268626000L), response.then().extract().jsonPath().getLong("contactOutcome.date"));

	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state/{state}"
	 * return 200
 	 * @throws InterruptedException
	 */
	@Test
	@Order(15)
	void testPutSurveyUnitState() throws InterruptedException, JSONException, JsonProcessingException {
		 given().auth().preemptive().basic("ABC", "abc")
     .contentType("application/json")
     .when()
     	.put("api/survey-unit/12/state/WFT")
     .then()
       .statusCode(200);
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state/{state}"
	 * return 400 with unknown state
 	 * @throws InterruptedException
	 */
	@Test
	@Order(16)
	void testPutSurveyUnitStateStateFalse() throws InterruptedException, JSONException, JsonProcessingException {
		 given().auth().preemptive().basic("ABC", "abc")
      .contentType("application/json")
      .when()
        .put("api/survey-unit/11/state/test")
      .then()
        .statusCode(400);
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state/{state}"
	 * return 403 when not allowed to pass to this state
 	 * @throws InterruptedException
	 */
	@Test
	@Order(17)
	void testPutSurveyUnitStateNoSu() throws InterruptedException, JSONException, JsonProcessingException {
		 given().auth().preemptive().basic("ABC", "abc")
     .contentType("application/json")
     .when()
       .put("api/survey-unit/11/state/AOC")
     .then()
       .statusCode(403);
	}
	
	/**
	 * Test that the PUT endpoint "api/preferences"
	 * return 200
 	 * @throws InterruptedException
	 */
	@Test
	@Order(18)
	void testPutPreferences() throws InterruptedException, JSONException, JsonProcessingException {
		List<String> listPreferences = new ArrayList<>();
		listPreferences.add("simpsons2020x00");
		 given().auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listPreferences))
		.when()
			.put("api/preferences")
		.then()
			.statusCode(200);
	}
	
	/**
	 * Test that the PUT endpoint "api/preferences"
	 * return 200
 	 * @throws InterruptedException
	 */
	@Test
	@Order(19)
	void testPutPreferencesWrongCampaignId() throws InterruptedException, JSONException, JsonProcessingException {
		List<String> listPreferences = new ArrayList<>();
		listPreferences.add("");
		 given().auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listPreferences))
		.when()
			.put("api/preferences")
		.then()
			.statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(20)
	void testGetNbSuAbandoned() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		.when()
		.get("api/campaign/simpsons2020x00/survey-units/abandoned")
		.then()
		.statusCode(200).and()
		.assertThat().body("count", equalTo(0));
	}
	
	/**
	 * Test that the Get endpoint
	 * "/campaign/survey-units/contact-outcomes[?date={date}]" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(20)
	void testGetContactOutcomeCountAllCampaign() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaigns/survey-units/contact-outcomes").then().statusCode(200)
		.and().assertThat().body("[0].campaign.id", equalTo("simpsons2020x00"))
		.and().assertThat().body("[0].campaign.label", equalTo("Survey on the Simpsons tv show 2020"))
		.and().assertThat().body("[0].inaCount", equalTo(0))
		.and().assertThat().body("[0].refCount", equalTo(0))
		.and().assertThat().body("[0].impCount", equalTo(0))
		.and().assertThat().body("[0].iniCount", equalTo(0))
		.and().assertThat().body("[0].alaCount", equalTo(0))
		.and().assertThat().body("[0].wamCount", equalTo(1))
		.and().assertThat().body("[0].oosCount", equalTo(0));
	}
	
	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/contact-outcomes[?date={date}]" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(20)
	void testGetContactOutcomeCountByCampaign() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/survey-units/contact-outcomes").then().statusCode(200)
		.and().assertThat().body("organizationUnits[0].idDem", equalTo("OU-NORTH"))
		.and().assertThat().body("organizationUnits[0].labelDem", equalTo("North region organizational unit"))
		.and().assertThat().body("organizationUnits[0].inaCount", equalTo(0))
		.and().assertThat().body("organizationUnits[0].refCount", equalTo(0))
		.and().assertThat().body("organizationUnits[0].impCount", equalTo(0))
		.and().assertThat().body("organizationUnits[0].iniCount", equalTo(0))
		.and().assertThat().body("organizationUnits[0].alaCount", equalTo(0))
		.and().assertThat().body("organizationUnits[0].wamCount", equalTo(1))
		.and().assertThat().body("organizationUnits[0].oosCount", equalTo(0));
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(21)
	void testGetNbSuAbandonedNotFound() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		.when()
		.get("api/campaign/test/survey-units/abandoned")
		.then()
		.statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/not-attributed"
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(22)
	void testGetNbSuNotAttributed() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		.when()
		.get("api/campaign/simpsons2020x00/survey-units/not-attributed")
		.then()
		.statusCode(200).and()
		.assertThat().body("count", equalTo(0));
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/not-attributed"
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	@Order(23)
	void testGetNbSuNotAttributedNotFound() throws InterruptedException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		.when()
		.get("api/campaign/test/survey-units/not-attributed")
		.then()
		.statusCode(404);
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 200 when modifying both dates
	 * @throws InterruptedException
	 */
	@Test
	@Order(24)
	void testPutCollectionDatesModifyBothDates() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body("{\"startDate\": 162849200000, \"endDate\": 170849200000}")
		.when()
			.put("api/campaign/simpsons2020x00/collection-dates")
		.then()
      .statusCode(200);
    Optional<Campaign> simpsons = campaignRepository.findByIdIgnoreCase("simpsons2020x00");
    assertEquals(true, simpsons.isPresent());
    assertEquals(162849200000L, simpsons.get().getStartDate());
    assertEquals(170849200000L, simpsons.get().getEndDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 200 when modifying start date
	 * @throws InterruptedException
	 */
	@Test
	@Order(25)
	void testPutCollectionDatesModifyStartDate() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body("{\"startDate\": 162849200000}")
		.when()
			.put("api/campaign/simpsons2020x00/collection-dates")
		.then()
      .statusCode(200);
    Optional<Campaign> simpsons = campaignRepository.findByIdIgnoreCase("simpsons2020x00");
    assertEquals(true, simpsons.isPresent());
    assertEquals(162849200000L, simpsons.get().getStartDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 200 when modifying end date
	 * @throws InterruptedException
	 */
	@Test
	@Order(26)
	void testPutCollectionDatesModifyEndDate() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body("{\"endDate\": 170849200000}")
		.when()
			.put("api/campaign/simpsons2020x00/collection-dates")
		.then()
      .statusCode(200);
    Optional<Campaign> simpsons = campaignRepository.findByIdIgnoreCase("simpsons2020x00");
    assertEquals(true, simpsons.isPresent());
    assertEquals(170849200000L, simpsons.get().getEndDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{id}/collection-dates"
	 * return 400 when empty body
	 * @throws InterruptedException
	 */
	@Test
	@Order(27)
	void testPutCollectionDatesEmptyBody() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
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
	@Order(28)
	void testPutCollectionDatesBadFormat() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
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
	@Order(29)
	void testPutVisibilityModifyAllDates() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
		 	.body("{\"managementStartDate\": 1575937000000 , "
					+ "\"interviewerStartDate\": 1576801000000,"
					+ "\"identificationPhaseStartDate\": 1577233000000,"
					+ "\"collectionStartDate\": 1577837800000,"
					+ "\"collectionEndDate\": 1640996200000,"
					+ "\"endDate\": 1641514600000}")
		.when()
			.put("api/campaign/simpsons2020x00/organizational-unit/OU-NORTH/visibility")
		.then()
      .statusCode(200);
    Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("simpsons2020x00", "OU-NORTH");
    assertEquals(true, visi.isPresent());
    assertEquals(1575937000000L, visi.get().getManagementStartDate());
    assertEquals(1576801000000L, visi.get().getInterviewerStartDate());
    assertEquals(1577233000000L, visi.get().getIdentificationPhaseStartDate());
    assertEquals(1577837800000L, visi.get().getCollectionStartDate());
    assertEquals(1640996200000L, visi.get().getCollectionEndDate());
    assertEquals(1641514600000L, visi.get().getEndDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying start date
	 * @throws InterruptedException
	 */
	@Test
	@Order(30)
	void testPutVisibilityModifyCollectionStartDate() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body("{\"collectionStartDate\": 1577847800000}")
		.when()
    .put("api/campaign/simpsons2020x00/organizational-unit/OU-NORTH/visibility")
		.then()
      .statusCode(200);
    Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("simpsons2020x00", "OU-NORTH");
    assertEquals(true, visi.isPresent());
    assertEquals(1577847800000L, visi.get().getCollectionStartDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying end date
	 * @throws InterruptedException
	 */
	@Test
	@Order(31)
	void testPutVisibilityModifyCollectionEndDate() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body("{\"collectionEndDate\": 1577857800000}")
		.when()
			.put("api/campaign/simpsons2020x00/organizational-unit/OU-NORTH/visibility")
		.then()
      .statusCode(200);
    Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("simpsons2020x00", "OU-NORTH");
    assertEquals(true, visi.isPresent());
    assertEquals(1577857800000L, visi.get().getCollectionEndDate());
  }
  
  /**
	 * Test that the PUT endpoint "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 400 when empty body
	 * @throws InterruptedException
	 */
	@Test
	@Order(32)
	void testPutVisibilityEmptyBody() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
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
	@Order(33)
	void testPutVisibilityBadFormat() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
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
	 * Test that the POST endpoint "api/message" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(34)
	void testPostMessage() throws InterruptedException, JsonProcessingException, JSONException {
		List<String> recipients = new ArrayList<String>();
		recipients.add("INTW1");
		MessageDto message = new MessageDto("TEST", recipients);
		message.setSender("ABC");
		given().auth().preemptive().basic("ABC", "abc")
				.contentType("application/json").body(new ObjectMapper().writeValueAsString(message)).when()
				.post("api/message").then().statusCode(200);
		List<MessageDto> messages = messageRepository
				.findMessagesDtoByIds(messageRepository.getMessageIdsByInterviewer("INTW1"));
		assertEquals("TEST", messages.get(0).getText());
	}

	/**
	 * Test that the POST endpoint "api/message" return 400 when bad format
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(35)
	void testPostMessageBadFormat() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
				.contentType("application/json").body(new ObjectMapper().writeValueAsString(null)).when()
				.post("api/message").then().statusCode(400);
	}
	
	/**
	 * Test that the GET endpoint
	 * "api/messages/{id}" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(36)
	void testGetMessage() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("INTW1", "intw1").when().get("api/messages/INTW1").then().statusCode(200).and()
		.assertThat().body("text", hasItem("TEST"));		
	}
	
	/**
	 * Test that the GET endpoint
	 * "api/messages/{id}" return empty body with a wrong id
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(37)
	void testGetMessageWrongId() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("INTW1", "intw1").when().get("api/messages/123456789").then().statusCode(200).and()
		.assertThat().body("isEmpty()", Matchers.is(true));		
	}

	/**
	 * Test that the put endpoint "api/message/{id}/interviewer/{idep}/read" 
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(38)
	void testPutMessageAsRead() throws InterruptedException, JsonProcessingException, JSONException {
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		given().auth().preemptive().basic("INTW1", "intw1")
				.contentType("application/json").when().put("api/message/" + messageId + "/interviewer/INTW1/read")
				.then().statusCode(200);
		Optional<Message> message = messageRepository.findById(messageId);
		assertEquals(MessageStatusType.REA, message.get().getMessageStatus().get(0).getStatus());
	}
	
	/**
	 * Test that the put endpoint "api/message/{id}/interviewer/{idep}/delete" 
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(39)
	void testPutMessageAsDelete() throws InterruptedException, JsonProcessingException, JSONException {
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		given().auth().preemptive().basic("INTW1", "intw1")
				.contentType("application/json").when().put("api/message/" + messageId + "/interviewer/INTW1/delete")
				.then().statusCode(200);
		Optional<Message> message = messageRepository.findById(messageId);
		assertEquals(MessageStatusType.DEL, message.get().getMessageStatus().get(0).getStatus());
	}

	/**
	 * Test that the PUT endpoint
	 * "/message/{id}/interviewer/{idep}/read" return 404 with a wrong Id
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(40)
	void testPutMessageAsReadWrongId() throws InterruptedException, JsonProcessingException, JSONException {
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		given().auth().preemptive().basic("INTW1", "intw1")
				.contentType("application/json").when().put("api/message/" + messageId + "/interviewer/Test/read").then()
				.statusCode(404);
	}

	/**
	 * Test that the GET endpoint
	 * "/message-history" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(41)
	void testGetMessageHistory() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc")
				.when().get("api/message-history").then().statusCode(200).and()
				.assertThat().body("text", hasItem("TEST"));
	}

	/**
	 * Test that the POST endpoint
	 * "/verify-name" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(42)
	void testPostVerifyName() throws InterruptedException, JsonProcessingException, JSONException {
		WsText message = new WsText("INTW1");
		given().auth().preemptive().basic("ABC", "abc")
				.contentType("application/json").body(new ObjectMapper().writeValueAsString(message)).when()
				.post("api/verify-name").then().statusCode(200).and()
				.assertThat().body("id", hasItem("INTW1"));
	}
	
	/**
	 * Test that the POST endpoint "api/message" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(43)
	void testPostMessageSystem() throws InterruptedException, JsonProcessingException, JSONException {
		List<String> recipients = new ArrayList<String>();
		recipients.add("INTW2");
		MessageDto message = new MessageDto("Synchronisation", recipients);
		message.setSender("ABC");
		given().auth().preemptive().basic("INTW1", "intw1")
				.contentType("application/json").body(new ObjectMapper().writeValueAsString(message)).when()
				.post("api/message").then().statusCode(200);
		List<MessageDto> messages = messageRepository
				.findMessagesDtoByIds(messageRepository.getMessageIdsByInterviewer("INTW2"));
		assertEquals("Synchronisation", messages.get(0).getText());
	}
	
	/**
	 * Test that the Get endpoint
	 * "/interviewers" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(44)
	void testGetInterviewer() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/interviewers").then().statusCode(200)
		.and().assertThat().body("id", hasItem("INTW1"))
		.and().assertThat().body("interviewerFirstName", hasItem("Margie"))
		.and().assertThat().body("interviewerLastName", hasItem("Lucas"));
	}
	
	/**
	 * Test that the Get endpoint
	 * "/interviewer/{id}/campaigns" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(45)
	void testGetInterviewerForCampaign() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/interviewer/INTW1/campaigns").then().statusCode(200)
		.and().assertThat().body("id", hasItem("simpsons2020x00"))
		.and().assertThat().body("label", hasItem("Survey on the Simpsons tv show 2020"));
		assertTrue(testingDates("managementStartDate", given().auth().preemptive().basic("ABC", "abc").when().get("api/interviewer/INTW1/campaigns").path("managementStartDate[0]")));
		assertTrue(testingDates("endDate", given().auth().preemptive().basic("ABC", "abc").when().get("api/interviewer/INTW1/campaigns").path("endDate[0]")));
	}
	
	/**
	 * Test that the Get endpoint
	 * "/interviewer/{id}/campaigns" return 404
	 * when interviewer not exist
	 * @throws InterruptedException
	 */
	@Test
	@Order(46)
	void testGetInterviewerNotExistForCampaign() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/interviewer/INTW123/campaigns").then().statusCode(404);
	}
	
	/**
	 * Test that the Get endpoint
	 * "/survey-units/closable" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(47)
	void testGetSUCloasable() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/survey-units/closable").then().statusCode(200)
		.and().assertThat().body("id", hasItem("11"))
		.and().assertThat().body("ssech", hasItem(1))
		.and().assertThat().body("location", hasItem("29270"))
		.and().assertThat().body("city", hasItem("Carhaix"))
		.and().assertThat().body("interviewer.id", hasItem("INTW1"))
		.and().assertThat().body("interviewer.interviewerFirstName", hasItem("Margie"))
		.and().assertThat().body("interviewer.interviewerLastName", hasItem("Lucas"))
		.and().assertThat().body("comments[0]", empty());
	}
	
	
	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(48)
	void testGetContactOutcomeCountByCampaignAndInterviewer() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/survey-units/interviewer/INTW1/contact-outcomes").then().statusCode(200)
		.and().assertThat().body("inaCount", equalTo(0))
		.and().assertThat().body("refCount", equalTo(0))
		.and().assertThat().body("impCount", equalTo(0))
		.and().assertThat().body("iniCount", equalTo(0))
		.and().assertThat().body("alaCount", equalTo(0))
		.and().assertThat().body("wamCount", equalTo(0))
		.and().assertThat().body("oosCount", equalTo(0));
	}
	
	
	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]" return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(49)
	void testGetContactOutcomeCountByCampaignNotExistAndInterviewer() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x000000/survey-units/interviewer/INTW1/contact-outcomes").then().statusCode(404);
	}
	
	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]" return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(50)
	void testGetContactOutcomeCountByCampaignAndInterviewerNotExist() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x00/survey-units/interviewer/INTW123/contact-outcomes").then().statusCode(404);
	}
	
	
	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/contact-outcomes[?date={date}]" return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(52)
	void testGetContactOutcomeCountByCampaignNotExist() throws InterruptedException, JsonProcessingException, JSONException {
		given().auth().preemptive().basic("ABC", "abc").when().get("api/campaign/simpsons2020x000000/survey-units/contact-outcomes").then().statusCode(404);
	}
	
	
	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(54)
	void testPutCommentOnSu() throws InterruptedException, JsonProcessingException, JSONException {
		Comment comment = new Comment(111L, CommentType.MANAGEMENT, "Test of comment", new SurveyUnit());
		given()
		.auth().preemptive().basic("ABC", "abc")
	 	.contentType("application/json")
		.body(new ObjectMapper().writeValueAsString(new CommentDto(comment)))
		.when()
		.put("api/survey-unit/11/comment").then().statusCode(200);
		given().auth().preemptive().basic("INTW1", "intw1").when().get("api/survey-unit/11").then().statusCode(200)
		.and().assertThat().body("comments[0].type", equalTo(CommentType.MANAGEMENT.toString()))
		.and().assertThat().body("comments[0].value", equalTo("Test of comment"));
	}
	
	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/comment" return 404
	 * when id not exist
	 * @throws InterruptedException
	 */
	@Test
	@Order(55)
	void testPutCommentSuNotExist() throws InterruptedException, JsonProcessingException, JSONException {
		SurveyUnit su = new SurveyUnit();
		su.setId("11111111111");
		Comment comment = new Comment(111L, CommentType.MANAGEMENT, "Test of comment", su);
		given()
		.auth().preemptive().basic("ABC", "abc")
	 	.contentType("application/json")
		.body(new ObjectMapper().writeValueAsString(new CommentDto(comment)))
		.when()
		.put("api/survey-unit/11111111111/comment").then().statusCode(404);
	}
	
	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/viewed" return 200 and viewed attribut set to true
	 * @throws InterruptedException
	 */
	@Test
	@Order(56)
	void testPutSuViewed() throws InterruptedException, JsonProcessingException, JSONException {
		SurveyUnit su = new SurveyUnit();
		su.setId("24");
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType("application/json")
		.when()
		.put("api/survey-unit/24/viewed").then().statusCode(200);
		assertEquals(true, surveyUnitRepository.findById("24").get().isViewed());
	}
	
	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/viewed" return 404
	 * when id not exist
	 * @throws InterruptedException
	 */
	@Test
	@Order(57)
	void testPutSuViewedNotExist() throws InterruptedException, JsonProcessingException, JSONException {
		SurveyUnit su = new SurveyUnit();
		su.setId("11111111111");
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType("application/json")
		.when()
		.put("api/survey-unit/11111111111/viewed").then().statusCode(404);
	}
	
	/**
	 * Test that the Post endpoint
	 * "/campaign" returns 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(58)
	void testPostCampaignContext() throws InterruptedException, JsonProcessingException, JSONException {
		CampaignContextDto campDto = new CampaignContextDto();
		campDto.setCampaign("campId");
		campDto.setCampaignLabel("An other campaign");
		campDto.setVisibilities(new ArrayList<>());
		
		VisibilityContextDto visi1 = new VisibilityContextDto();
		visi1.setOrganizationalUnit("OU-NORTH");
		visi1.setCollectionStartDate(1111L);
		visi1.setCollectionEndDate(2222L);
		visi1.setIdentificationPhaseStartDate(3333L);
		visi1.setInterviewerStartDate(4444L);
		visi1.setManagementStartDate(5555L);
		visi1.setEndDate(6666L);
		
		VisibilityContextDto visi2 = new VisibilityContextDto();
		visi2.setOrganizationalUnit("OU-SOUTH");
		visi2.setCollectionStartDate(1111L);
		visi2.setCollectionEndDate(2222L);
		visi2.setIdentificationPhaseStartDate(3333L);
		visi2.setInterviewerStartDate(4444L);
		visi2.setManagementStartDate(5555L);
		visi2.setEndDate(6666L);
		
		campDto.getVisibilities().add(visi1);
		campDto.getVisibilities().add(visi2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(campDto))
			.when()
			.post("api/campaign")
			.then()
			.statusCode(200);
		
		Optional<Campaign> campOpt = campaignRepository.findById("campId");
		assertTrue(campOpt.isPresent());
		assertEquals("An other campaign", campOpt.get().getLabel());
		
		Optional<Visibility> visi1Opt = visibilityRepository.findVisibilityByCampaignIdAndOuId("campId", "OU-NORTH");
		Optional<Visibility> visi2Opt = visibilityRepository.findVisibilityByCampaignIdAndOuId("campId", "OU-SOUTH");

		assertTrue(visi1Opt.isPresent());
		assertTrue(visi2Opt.isPresent());
		
		Visibility visi = visi1Opt.get();
	    assertEquals(1111L, visi.getCollectionStartDate());
	    assertEquals(2222L, visi.getCollectionEndDate());
	    assertEquals(3333L, visi.getIdentificationPhaseStartDate());
	    assertEquals(4444L, visi.getInterviewerStartDate());
	    assertEquals(5555L, visi.getManagementStartDate());
	    assertEquals(6666L, visi.getEndDate());
		
	}
	
	/**
	 * Test that the Post endpoint
	 * "/campaign" returns 400
	 * when an attribute is missing
	 * @throws InterruptedException
	 */
	@Test
	@Order(59)
	void testPostCampaignContextNoLabel() throws InterruptedException, JsonProcessingException, JSONException {
		CampaignContextDto campDto = new CampaignContextDto();
		campDto.setCampaign("campId2");
		// Campaign label unset
		campDto.setVisibilities(new ArrayList<>());
		
		VisibilityContextDto visi1 = new VisibilityContextDto();
		visi1.setOrganizationalUnit("OU-NORTH");
		visi1.setCollectionStartDate(1111L);
		visi1.setCollectionEndDate(2222L);
		visi1.setIdentificationPhaseStartDate(3333L);
		visi1.setInterviewerStartDate(4444L);
		visi1.setManagementStartDate(5555L);
		visi1.setEndDate(6666L);
		
		VisibilityContextDto visi2 = new VisibilityContextDto();
		visi2.setOrganizationalUnit("OU-SOUTH");
		visi2.setCollectionStartDate(1111L);
		visi2.setCollectionEndDate(2222L);
		visi2.setIdentificationPhaseStartDate(3333L);
		visi2.setInterviewerStartDate(4444L);
		visi2.setManagementStartDate(5555L);
		visi2.setEndDate(6666L);
		
		campDto.getVisibilities().add(visi1);
		campDto.getVisibilities().add(visi2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(campDto))
			.when()
			.post("api/campaign")
			.then()
			.statusCode(400);
		
		// Campaign should not have been created
		Optional<Campaign> campOpt = campaignRepository.findById("campId2");
		assertTrue(!campOpt.isPresent());

		
	}
	
	/**
	 * Test that the Post endpoint
	 * "/campaign" returns 400
	 * when an an organizational unit does not exist
	 * @throws InterruptedException
	 */
	@Test
	@Order(60)
	void testPostCampaignContextMissingOU() throws InterruptedException, JsonProcessingException, JSONException {
		CampaignContextDto campDto = new CampaignContextDto();
		campDto.setCampaign("campId3");
		// Campaign label unset
		campDto.setVisibilities(new ArrayList<>());
		
		VisibilityContextDto visi1 = new VisibilityContextDto();
		visi1.setOrganizationalUnit("OU-NORTH");
		visi1.setCollectionStartDate(1111L);
		visi1.setCollectionEndDate(2222L);
		visi1.setIdentificationPhaseStartDate(3333L);
		visi1.setInterviewerStartDate(4444L);
		visi1.setManagementStartDate(5555L);
		visi1.setEndDate(6666L);
		
		VisibilityContextDto visi2 = new VisibilityContextDto();
		visi2.setOrganizationalUnit("AN-OU-THAT-DOESNT-EXIST");
		visi2.setCollectionStartDate(1111L);
		visi2.setCollectionEndDate(2222L);
		visi2.setIdentificationPhaseStartDate(3333L);
		visi2.setInterviewerStartDate(4444L);
		visi2.setManagementStartDate(5555L);
		visi2.setEndDate(6666L);
		
		campDto.getVisibilities().add(visi1);
		campDto.getVisibilities().add(visi2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(campDto))
			.when()
			.post("api/campaign")
			.then()
			.statusCode(400);
		
		// Campaign should not have been created
		Optional<Campaign> campOpt = campaignRepository.findById("campId3");
		assertTrue(!campOpt.isPresent());
	}
	
	/**
	 * Test that the Post endpoint
	 * "/organization-unit/context" returns 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(61)
	void testPostOrganizationUnitContext() throws InterruptedException, JsonProcessingException, JSONException {
		ArrayList<OrganizationUnitContextDto> listOU = new ArrayList<>();
		
		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto();
		ou1.setOrganisationUnit("OU-NORTH2");
		ou1.setOrganisationUnitLabel("North region OU 2");
		ou1.setType(OrganizationUnitType.LOCAL);
		ou1.setUsers(new ArrayList<UserContextDto>());
		
		UserContextDto user1 = new UserContextDto();
		user1.setFirstName("Jacques");
		user1.setLastName("Boulanger");
		user1.setId("JBOULANGER1");
		ou1.getUsers().add(user1);
		
		UserContextDto user2 = new UserContextDto();
		user2.setFirstName("Chloé");
		user2.setLastName("Berlin");
		user2.setId("CBERLIN1");
		ou1.getUsers().add(user2);
		
		OrganizationUnitContextDto ou2 = new OrganizationUnitContextDto();
		ou2.setOrganisationUnit("OU-NATIONAL2");
		ou2.setOrganisationUnitLabel("National OU 2");
		ou2.setType(OrganizationUnitType.LOCAL);
		ou2.setUsers(new ArrayList<UserContextDto>());
		ou2.setOrganisationUnitRef(new ArrayList<String>());
		ou2.getOrganisationUnitRef().add("OU-NORTH2");
		
		UserContextDto user3 = new UserContextDto();
		user3.setFirstName("Thierry");
		user3.setLastName("Fabres");
		user3.setId("TFABRES1");
		ou2.getUsers().add(user3);
		
		UserContextDto user4 = new UserContextDto();
		user4.setFirstName("Fabrice");
		user4.setLastName("Dupont");
		user4.setId("FDUPONT1");
		ou2.getUsers().add(user4);
		
		listOU.add(ou1);
		listOU.add(ou2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listOU))
			.when()
			.post("api/organization-units")
			.then()
			.statusCode(200);
		
		Optional<OrganizationUnit> ou1Opt = organizationUnitRepository.findById("OU-NORTH2");
		Optional<OrganizationUnit> ou2Opt = organizationUnitRepository.findById("OU-NATIONAL2");
		assertTrue(ou1Opt.isPresent());
		assertTrue(ou2Opt.isPresent());
		assertEquals("North region OU 2", ou1Opt.get().getLabel());
		assertEquals("OU-NATIONAL2", ou1Opt.get().getOrganizationUnitParent().getId());
		assertEquals("National OU 2", ou2Opt.get().getLabel());
		
		Optional<User> user1Opt = userRepository.findById("JBOULANGER1");
		Optional<User> user2Opt = userRepository.findById("CBERLIN1");
		Optional<User> user3Opt = userRepository.findById("TFABRES1");
		Optional<User> user4Opt = userRepository.findById("FDUPONT1");

		assertTrue(user1Opt.isPresent());
		assertEquals("Jacques", user1Opt.get().getFirstName());
		assertEquals("Boulanger", user1Opt.get().getLastName());

		assertTrue(user2Opt.isPresent());
		assertEquals("Chloé", user2Opt.get().getFirstName());
		assertEquals("Berlin", user2Opt.get().getLastName());
		
		assertTrue(user3Opt.isPresent());
		assertEquals("Thierry", user3Opt.get().getFirstName());
		assertEquals("Fabres", user3Opt.get().getLastName());
		
		assertTrue(user4Opt.isPresent());
		assertEquals("Fabrice", user4Opt.get().getFirstName());
		assertEquals("Dupont", user4Opt.get().getLastName());
		
	}
	
	/**
	 * Test that the Post endpoint
	 * "/organization-unit/context" returns 400
	 * when there is a duplicate user
	 * @throws InterruptedException
	 */
	@Test
	@Order(62)
	void testPostOrganizationUnitContextDuplicateUser() throws InterruptedException, JsonProcessingException, JSONException {
		ArrayList<OrganizationUnitContextDto> listOU = new ArrayList<>();
		
		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto();
		ou1.setOrganisationUnit("OU-NORTH3");
		ou1.setOrganisationUnitLabel("North region OU 3");
		ou1.setType(OrganizationUnitType.LOCAL);
		ou1.setUsers(new ArrayList<UserContextDto>());
		
		UserContextDto user1 = new UserContextDto();
		user1.setFirstName("Jacques");
		user1.setLastName("Boulanger");
		user1.setId("JBOULANGER2");
		ou1.getUsers().add(user1);
		
		UserContextDto user2 = new UserContextDto();
		user2.setFirstName("Chloé");
		user2.setLastName("Berlin");
		user2.setId("CBERLIN2");
		ou1.getUsers().add(user2);
		
		OrganizationUnitContextDto ou2 = new OrganizationUnitContextDto();
		ou2.setOrganisationUnit("OU-NATIONAL3");
		ou2.setOrganisationUnitLabel("National OU 3");
		ou2.setType(OrganizationUnitType.LOCAL);
		ou2.setUsers(new ArrayList<UserContextDto>());
		
		UserContextDto user3 = new UserContextDto();
		user3.setFirstName("Thierry");
		user3.setLastName("Fabres");
		user3.setId("TFABRES2");
		ou2.getUsers().add(user3);
		
		// Adding user2 on this OU as well
		ou2.getUsers().add(user2);
		

		
		listOU.add(ou1);
		listOU.add(ou2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listOU))
			.when()
			.post("api/organization-units")
			.then()
			.statusCode(400);
		
		// No OU should have been added
		Optional<OrganizationUnit> ou1Opt = organizationUnitRepository.findById("OU-NORTH3");
		Optional<OrganizationUnit> ou2Opt = organizationUnitRepository.findById("OU-NATIONAL3");
		assertTrue(!ou1Opt.isPresent());
		assertTrue(!ou2Opt.isPresent());

	}
	
	/**
	 * Test that the Post endpoint
	 * "/organization-unit/context" returns 400
	 * when a child organization unit does not exist
	 * @throws InterruptedException
	 */
	@Test
	@Order(63)
	void testPostOrganizationUnitContextNoOU() throws InterruptedException, JsonProcessingException, JSONException {
		ArrayList<OrganizationUnitContextDto> listOU = new ArrayList<>();
		
		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto();
		ou1.setOrganisationUnit("OU-NORTH3");
		ou1.setOrganisationUnitLabel("North region OU 3");
		ou1.setType(OrganizationUnitType.LOCAL);
		ou1.setUsers(new ArrayList<UserContextDto>());
		ou1.setOrganisationUnitRef(new ArrayList<String>());
		ou1.getOrganisationUnitRef().add("AN-OU-THAT-DOESNT-EXIST");
		
		UserContextDto user1 = new UserContextDto();
		user1.setFirstName("Jacques");
		user1.setLastName("Boulanger");
		user1.setId("JBOULANGER2");
		ou1.getUsers().add(user1);
		
		UserContextDto user2 = new UserContextDto();
		user2.setFirstName("Chloé");
		user2.setLastName("Berlin");
		user2.setId("CBERLIN2");
		ou1.getUsers().add(user2);
		
		OrganizationUnitContextDto ou2 = new OrganizationUnitContextDto();
		ou2.setOrganisationUnit("OU-NATIONAL3");
		ou2.setOrganisationUnitLabel("National OU 3");
		ou2.setType(OrganizationUnitType.LOCAL);
		ou2.setUsers(new ArrayList<UserContextDto>());
		
		UserContextDto user3 = new UserContextDto();
		user3.setFirstName("Thierry");
		user3.setLastName("Fabres");
		user3.setId("TFABRES2");
		ou2.getUsers().add(user3);
		
		// Adding user2 on this OU as well
		ou2.getUsers().add(user2);
		
		listOU.add(ou1);
		listOU.add(ou2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listOU))
			.when()
			.post("api/organization-units")
			.then()
			.statusCode(400);
		
		// No OU should have been added
		Optional<OrganizationUnit> ou1Opt = organizationUnitRepository.findById("OU-NORTH3");
		Optional<OrganizationUnit> ou2Opt = organizationUnitRepository.findById("OU-NATIONAL3");
		assertTrue(!ou1Opt.isPresent());
		assertTrue(!ou2Opt.isPresent());

	}
	
	/**
	 * Test that the Post endpoint
	 * "/organization-unit/context" returns 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(64)
	void testPostInterviewers() throws InterruptedException, JsonProcessingException, JSONException {
		List<InterviewerContextDto> listInterviewers = new ArrayList<>();
		
		InterviewerContextDto interv1 = new InterviewerContextDto(
				"INTERV1",
				"Pierre", 
				"Legrand", 
				"pierre.legrand@insee.fr", 
				"06 XX XX XX XX"
			);
		
		InterviewerContextDto interv2 = new InterviewerContextDto(
				"INTERV2",
				"Clara", 
				"Legouanec", 
				"clara.legouanec@insee.fr", 
				"06 XX XX XX XX"
			);
		
		listInterviewers.add(interv1);
		listInterviewers.add(interv2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listInterviewers))
			.when()
			.post("api/interviewers")
			.then()
			.statusCode(200);
		
		// Interviewers should have been added
		Optional<Interviewer> interv1Opt = interviewerRepository.findById("INTERV1");
		Optional<Interviewer> interv2Opt = interviewerRepository.findById("INTERV2");
		assertTrue(interv1Opt.isPresent());
		assertTrue(interv2Opt.isPresent());
		
		assertEquals("Pierre", interv1Opt.get().getFirstName());
		assertEquals("Legrand", interv1Opt.get().getLastName());
		assertEquals("pierre.legrand@insee.fr", interv1Opt.get().getEmail());
		assertEquals("06 XX XX XX XX", interv1Opt.get().getPhoneNumber());
		
		assertEquals("Clara", interv2Opt.get().getFirstName());
		assertEquals("Legouanec", interv2Opt.get().getLastName());
		assertEquals("clara.legouanec@insee.fr", interv2Opt.get().getEmail());
		assertEquals("06 XX XX XX XX", interv2Opt.get().getPhoneNumber());

	}
	
	/**
	 * Test that the Post endpoint
	 * "/organization-unit/context" returns 400
	 * when an email is missing
	 * @throws InterruptedException
	 */
	@Test
	@Order(65)
	void testPostInterviewersMissingEmail() throws InterruptedException, JsonProcessingException, JSONException {
		List<InterviewerContextDto> listInterviewers = new ArrayList<>();
		
		InterviewerContextDto interv1 = new InterviewerContextDto(
				"INTERV4",
				"Pierre", 
				"Legrand", 
				"pierre.legrand@insee.fr", 
				"06 XX XX XX XX"
			);
		
		InterviewerContextDto interv2 = new InterviewerContextDto(
				"INTERV5",
				"Clara", 
				"Legouanec", 
				null, 
				"06 XX XX XX XX"
			);
		
		listInterviewers.add(interv1);
		listInterviewers.add(interv2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
	 		.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listInterviewers))
			.when()
			.post("api/interviewers")
			.then()
			.statusCode(400);
		
		// Interviewers should not have been added
		Optional<Interviewer> interv1Opt = interviewerRepository.findById("INTERV4");
		Optional<Interviewer> interv2Opt = interviewerRepository.findById("INTERV5");
		assertTrue(!interv1Opt.isPresent());
		assertTrue(!interv2Opt.isPresent());

	}
	
	/**
	 * Test that the Post endpoint
	 * "/organization-unit/context" returns 400
	 * when an iterviewer id is present twice
	 * @throws InterruptedException
	 */
	@Test
	@Order(66)
	void testPostInterviewersDuplicateId() throws InterruptedException, JsonProcessingException, JSONException {
		List<InterviewerContextDto> listInterviewers = new ArrayList<>();
		
		InterviewerContextDto interv1 = new InterviewerContextDto(
				"INTERV3",
				"Pierre", 
				"Legrand", 
				"pierre.legrand@insee.fr", 
				"06 XX XX XX XX"
			);
		
		InterviewerContextDto interv2 = new InterviewerContextDto(
				"INTERV3",
				"Clara", 
				"Legouanec", 
				"clara.legouanec@insee.fr", 
				"06 XX XX XX XX"
			);
		
		listInterviewers.add(interv1);
		listInterviewers.add(interv2);
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
			.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(listInterviewers))
			.when()
			.post("api/interviewers")
			.then()
			.statusCode(400);
		
		// Interviewers should not have been added
		Optional<Interviewer> interv1Opt = interviewerRepository.findById("INTERV3");
		assertTrue(!interv1Opt.isPresent());
	}
	
	/**
	 * Test that the POST endpoint
	 * "/geographical-locations returns 200
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(67)
	void testPostGeographicalLocations() throws JsonProcessingException {
		GeographicalLocationDto gl = new GeographicalLocationDto();
		gl.setId("test");
		gl.setLabel("test");
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(gl)))
		.post("api/geographical-locations")
		.then().statusCode(200);
		Assert.assertTrue(geographicalLocationRepository.findById("test").isPresent());
	}
	
	/**
	 * Test that the POST endpoint
	 * "/geographical-locations with error returns 400
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(68)
	void testPostGeographicalLocationsError() throws JsonProcessingException {
		GeographicalLocationDto gl = new GeographicalLocationDto();
		gl.setId("");
		gl.setLabel("test");
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(gl)))
		.post("api/geographical-locations")
		.then().statusCode(400);
	}
	
	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 200
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(69)
	void testPostSurveyUnits() throws JsonProcessingException {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("8");
		su.setCampaign("simpsons2020x00");
		su.setGeographicalLocationId("test");
		su.setOrganizationUnitId("OU-NORTH");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		List<PersonDto> lstPerson = new ArrayList<>();
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		lstPerson.add(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units")
		.then().statusCode(200);
		Assert.assertTrue(surveyUnitRepository.findById("8").isPresent());
	}
	
	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when id dupliate in DB
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(70)
	void testPostSurveyUnitsDuplcateInDB() throws JsonProcessingException {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("8");
		su.setCampaign("simpsons2020x00");
		su.setGeographicalLocationId("test");
		su.setOrganizationUnitId("OU-NORTH");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		List<PersonDto> lstPerson = new ArrayList<>();
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		lstPerson.add(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units")
		.then().statusCode(400);
	}
	
	
	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when id dupliate in body
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(71)
	void testPostSurveyUnitsDuplicateInBody() throws JsonProcessingException {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("9");
		su.setCampaign("simpsons2020x00");
		su.setGeographicalLocationId("test");
		su.setOrganizationUnitId("OU-NORTH");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		List<PersonDto> lstPerson = new ArrayList<>();
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		lstPerson.add(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su, su)))
		.post("api/survey-units")
		.then().statusCode(400);
	}
	
	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when OrganizationUnitId does not exist
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(72)
	void testPostSurveyUnitsOUNotExist() throws JsonProcessingException {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("9");
		su.setCampaign("simpsons2020x00");
		su.setGeographicalLocationId("test");
		su.setOrganizationUnitId("OU-TEST");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		List<PersonDto> lstPerson = new ArrayList<>();
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		lstPerson.add(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units")
		.then().statusCode(400);
	}
	
	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when campaignId does not exist
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(73)
	void testPostSurveyUnitsCampaignNotExist() throws JsonProcessingException {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("9");
		su.setCampaign("campaignTest");
		su.setGeographicalLocationId("test");
		su.setOrganizationUnitId("OU-NORTH");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		List<PersonDto> lstPerson = new ArrayList<>();
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		lstPerson.add(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units")
		.then().statusCode(400);
	}
	
	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when GeographicalLocationId does not exist
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(74)
	void testPostSurveyUnitsGeogrphicalLocationNotExist() throws JsonProcessingException {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("9");
		su.setCampaign("simpsons2020x00");
		su.setGeographicalLocationId("testtest");
		su.setOrganizationUnitId("OU-NORTH");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		List<PersonDto> lstPerson = new ArrayList<>();
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		lstPerson.add(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units")
		.then().statusCode(400);
	}
	
	
	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when surveyUnit body is not valid
	 * @throws JsonProcessingException 
	 * @throws InterruptedException
	 */
	@Test
	@Order(75)
	void testPostSurveyUnitsSUNotValid() throws JsonProcessingException {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("");
		su.setCampaign("simpsons2020x00");
		su.setGeographicalLocationId("test");
		su.setOrganizationUnitId("OU-NORTH");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		List<PersonDto> lstPerson = new ArrayList<>();
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		lstPerson.add(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		// ID null
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units")
		.then().statusCode(400);
		// Campaign null
		su.setId("9");
		su.setCampaign("");
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units")
		.then().statusCode(400);
		// Persons Null
		su.setPersons(List.of());
		su.setCampaign("simpsons2020x00");
		given()
		.auth().preemptive().basic("ABC", "abc")
		.contentType(ContentType.JSON)
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units")
		.then().statusCode(400);
	}
	
	
	/**
	 * Test that the Post endpoint
	 * "/survey-units/interviewers" returns 200
	 * @throws InterruptedException
	 */
	@Test
	@Order(76)
	void testPostAssignements() throws InterruptedException, JsonProcessingException, JSONException {
		List<SurveyUnitInterviewerLinkDto> list = new ArrayList<>();
		addUnattributedSU("101");
		addUnattributedSU("102");
		SurveyUnitInterviewerLinkDto assign1 = new SurveyUnitInterviewerLinkDto("101", "INTW4");
		SurveyUnitInterviewerLinkDto assign2 = new SurveyUnitInterviewerLinkDto("102", "INTW3");
		
		list.add(assign1);
		list.add(assign2);
		
		
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(list))
			.when()
			.post("api/survey-units/interviewers")
			.then()
			.statusCode(200);
		
		// SU should have been attributted to interviewers
		Optional<SurveyUnit> su1 = surveyUnitRepository.findById("101");
		Optional<SurveyUnit> su2 = surveyUnitRepository.findById("102");
		assertEquals("INTW4", su1.get().getInterviewer().getId());
		assertEquals("INTW3", su2.get().getInterviewer().getId());
	}
	
	/**
	 * Test that the Post endpoint
	 * "/survey-units/interviewers" returns 400
	 * when an iterviewer is not present
	 * @throws InterruptedException
	 */
	@Test
	@Order(77)
	void testPostAssignementsNoInterviewerDoesntExist() throws InterruptedException, JsonProcessingException, JSONException {
		List<SurveyUnitInterviewerLinkDto> list = new ArrayList<>();
		addUnattributedSU("103");
		addUnattributedSU("104");
		SurveyUnitInterviewerLinkDto assign1 = new SurveyUnitInterviewerLinkDto("103", "INTW4");
		SurveyUnitInterviewerLinkDto assign2 = new SurveyUnitInterviewerLinkDto("104", "INTWDOESNTEXIST");
		
		list.add(assign1);
		list.add(assign2);
		
		given()
			.auth().preemptive().basic("ABC", "abc")
		 	.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(list))
			.when()
			.post("api/survey-units/interviewers")
			.then()
			.statusCode(400);
		
		// SU should have been attributted to interviewers
		Optional<SurveyUnit> su1 = surveyUnitRepository.findById("103");
		Optional<SurveyUnit> su2 = surveyUnitRepository.findById("104");
		assertEquals(null, su1.get().getInterviewer());
		assertEquals(null, su2.get().getInterviewer());
	}

	
	private void addUnattributedSU(String suId) throws JsonProcessingException {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId(suId);
		su.setCampaign("simpsons2020x00");
		su.setGeographicalLocationId("32221");
		su.setOrganizationUnitId("OU-NORTH");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		List<PersonDto> lstPerson = new ArrayList<>();
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		lstPerson.add(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		with().contentType(ContentType.JSON)
		.auth().preemptive().basic("ABC", "abc")
		.body(new ObjectMapper().writeValueAsString(List.of(su)))
		.post("api/survey-units");
	}
}
