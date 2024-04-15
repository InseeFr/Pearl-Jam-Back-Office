package fr.insee.pearljam.api.authKeycloak;

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
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.controller.WsText;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.Comment;
import fr.insee.pearljam.api.domain.CommentType;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.Medium;
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
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.phonenumber.PhoneNumberDto;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitInterviewerLinkDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.SurveyUnitException;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.MessageService;
import fr.insee.pearljam.api.service.ReferentService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import liquibase.Liquibase;

/* Test class for Keycloak Authentication */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestAuthKeyCloak {

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
	VisibilityRepository visibilityRepository;

	@Autowired
	MessageRepository messageRepository;

	@Autowired
	OrganizationUnitRepository organizationUnitRepository;

	@Autowired
	InterviewerRepository interviewerRepository;

	@Autowired
	ClosingCauseRepository closingCauseRepository;

	@Autowired
	ReferentService referentservice;

	@Autowired
	MessageService messageService;

	@Autowired
	private MockMvc mockMvc;

	@LocalServerPort
	private int port;

	private static ClientAndServer clientAndServer;
	private static MockServerClient mockServerClient;

	public Liquibase liquibase;

	public static final String CLIENT_SECRET = "8951f422-44dd-45b4-a6ac-dde6748075d7";
	public static final String CLIENT = "client-web";

	static Authentication MANAGER = AuthenticatedUserTestHelper.AUTH_MANAGER;
	static Authentication INTERVIEWER = AuthenticatedUserTestHelper.AUTH_INTERVIEWER;

	/**
	 * This method set up the dataBase content
	 * 
	 * @throws Exception
	 * 
	 */
	@BeforeEach
	public void setUp() throws Exception {

		mockMvc.perform(post("/api/create-dataset").accept(MediaType.APPLICATION_JSON)
				.with(authentication(MANAGER)));
	}

	@BeforeAll
	static void init() {

		clientAndServer = ClientAndServer.startClientAndServer(8088);
		mockServerClient = new MockServerClient("127.0.0.1", 8088);
		String expectedBody = """
				{
					"surveyUnitOK" : [
						{
							"id":"23",
							"stateData":{
								"state": null,
								"date": null,
								"currentPage": null
							}
						},
						{
							"id":"20",
							"stateData":{
								"state": null,
								"date": null,
								"currentPage": null
							}
						}
					],
					"surveyUnitNOK" : [
						{
							"id":"21"
						}
					]
				}
				""";

		mockServerClient
				.when(request().withPath(Constants.API_QUEEN_SURVEYUNITS_STATEDATA).withBody("[\"20\",\"21\",\"23\"]"))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "application/json; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(expectedBody));
	}

	/**
	 * This method is used to kill the container
	 * 
	 * @throws Exception
	 */
	@AfterAll
	public static void cleanUp() throws Exception {

		if (mockServerClient != null) {
			mockServerClient.close();
		}
		if (clientAndServer != null) {
			clientAndServer.close();
		}

	}

	/**
	 * This method is use to check if the dates are correct
	 * 
	 * @param dateType
	 * @param date
	 * @return
	 */
	private boolean testingDates(String dateType, long date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		LocalDate localDateNow = LocalDate.now();
		boolean check = false;
		LocalDate value = LocalDate.parse(df.format(date));
		switch (dateType) {
			case ("managementStartDate"):
				if (value.equals(localDateNow.minusDays(4))) {
					check = true;
				}
				break;
			case ("interviewerStartDate"):
				if (value.equals(localDateNow.minusDays(3))) {
					check = true;
				}
				break;
			case ("identificationPhaseStartDate"):
				if (value.equals(localDateNow.minusDays(2))) {
					check = true;
				}
				break;
			case ("collectionStartDate"):
				if (value.equals(localDateNow.minusDays(2))) {
					check = true;
				}
				break;
			case ("collectionEndDate"):
				if (value.equals(localDateNow.plusMonths(1))) {
					check = true;
				}
				break;
			case ("endDate"):
				if (value.equals(localDateNow.plusMonths(2))) {
					check = true;
				}
				break;
			default:
				return check;
		}
		return check;
	}

	/***
	 * This method retreive the access token of the keycloak client
	 * 
	 * @param clientId
	 * @param clientSecret
	 * @param username
	 * @param password
	 * @return
	 * @throws JSONException
	 */
	public String resourceOwnerLogin(String clientId, String clientSecret, String username, String password)
			throws JSONException {
		return "YoloToken";
	}

	/* UserController */

	/**
	 * Test that the GET endpoint "api/user"
	 * return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(1)
	void testGetUser() throws Exception {

		mockMvc.perform(get("/api/user").accept(MediaType.APPLICATION_JSON)
				.with(authentication(MANAGER)))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.id").value("ABC"),
						jsonPath("$.firstName").value("Melinda"),
						jsonPath("$.lastName").value("Webb"),
						jsonPath("$.organizationUnit.id").value("OU-NORTH"),
						jsonPath("$.organizationUnit.label").value("North region organizational unit"),
						jsonPath("$.localOrganizationUnits[0].id").value("OU-NORTH"),
						jsonPath("$.localOrganizationUnits[0].label").value("North region organizational unit"));
	}

	@Test
	@Order(1)
	void testGetCampaignInterviewerStateCountNotAttributed() throws Exception {

		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/not-attributed/state-count")
				.accept(MediaType.APPLICATION_JSON)
				.with(authentication(MANAGER)))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.nvmCount").value("0"),
						jsonPath("$.nnsCount").value("0"),
						jsonPath("$.anvCount").value("0"),
						jsonPath("$.vinCount").value("0"),
						jsonPath("$.vicCount").value("0"),
						jsonPath("$.prcCount").value("0"),
						jsonPath("$.aocCount").value("0"),
						jsonPath("$.apsCount").value("0"),
						jsonPath("$.insCount").value("0"),
						jsonPath("$.wftCount").value("0"),
						jsonPath("$.wfsCount").value("0"),
						jsonPath("$.tbrCount").value("1"),
						jsonPath("$.finCount").value("0"),
						jsonPath("$.cloCount").value("0"),
						jsonPath("$.nvaCount").value("0"),
						jsonPath("$.npaCount").value("0"),
						jsonPath("$.npiCount").value("0"),
						jsonPath("$.npxCount").value("0"),
						jsonPath("$.rowCount").value("0"),
						jsonPath("$.total").value("1"));

	}

	@Test
	@Order(1)
	void testGetContactOutcomeCountNotattributed() throws Exception {
		mockMvc
				.perform(get(
						"https://localhost:8080/api/campaign/SIMPSONS2020X00/survey-units/not-attributed/contact-outcomes")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.inaCount").value("0"),
						jsonPath("$.refCount").value("0"),
						jsonPath("$.impCount").value("0"),
						jsonPath("$.ucdCount").value("0"),
						jsonPath("$.utrCount").value("0"),
						jsonPath("$.alaCount").value("0"),
						jsonPath("$.dcdCount").value("0"),
						jsonPath("$.nuhCount").value("0"),
						jsonPath("$.dukCount").value("1"),
						jsonPath("$.duuCount").value("0"),
						jsonPath("$.noaCount").value("0"));

	}

	/**
	 * Test that the GET endpoint "api/user"
	 * return null
	 * 
	 * @throws InterruptedException
	 * @throws NotFoundException
	 * @throws JSONException
	 */
	@Test
	@Order(2)
	void testGetUserNotFound() {
		assertEquals(Optional.empty(), userService.getUser("test"));
	}

	private ResultMatcher checkJsonPath(String formattablePath, String... args) {
		return jsonPath(String.format(formattablePath, (Object[]) args)).exists();
	}

	/* CampaignController */

	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(3)
	void testGetCampaign() throws Exception {
		String campaignJsonPath = "$.[?(@.id == 'SIMPSONS2020X00' && @.%s == '%s')]";

		mockMvc
				.perform(get(Constants.API_CAMPAIGNS)
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						checkJsonPath(campaignJsonPath, "label", "Survey on the Simpsons tv show 2020"),
						checkJsonPath(campaignJsonPath, "allocated", "4"),
						checkJsonPath(campaignJsonPath, "toProcessInterviewer", "0"),
						checkJsonPath(campaignJsonPath, "toAffect", "0"),
						checkJsonPath(campaignJsonPath, "toFollowUp", "0"),
						checkJsonPath(campaignJsonPath, "finalized", "0"),
						checkJsonPath(campaignJsonPath, "email", "first.email@test.com"),
						checkJsonPath(campaignJsonPath, "identificationConfiguration",
								IdentificationConfiguration.IASCO.name()),
						checkJsonPath(campaignJsonPath, "contactAttemptConfiguration",
								ContactAttemptConfiguration.F2F.name()),
						checkJsonPath(campaignJsonPath, "contactOutcomeConfiguration",
								ContactOutcomeConfiguration.F2F.name()));
		// dig this to complete test:
		// checkJsonPath(campaignJsonPath, "preference", "true"),
		// checkJsonPath(campaignJsonPath, "toReview", "0"),
		// checkJsonPath(campaignJsonPath, "communicationRequestConfiguration",
		// "false"),

		// // Testing dates => need a refactor of service to provide a Clock (fixed
		// clock for tests)
		// assertTrue(testingDates("managementStartDate",
		// given().port(port).auth().oauth2(accessToken).when().get(Constants.API_CAMPAIGNS)
		// .path("managementStartDate[0]")));
		// assertTrue(testingDates("interviewerStartDate",
		// given().port(port).auth().oauth2(accessToken).when().get(Constants.API_CAMPAIGNS)
		// .path("interviewerStartDate[0]")));
		// assertTrue(testingDates("identificationPhaseStartDate",
		// given().port(port).auth().oauth2(accessToken).when()
		// .get(Constants.API_CAMPAIGNS).path("identificationPhaseStartDate[0]")));
		// assertTrue(testingDates("collectionStartDate",
		// given().port(port).auth().oauth2(accessToken).when().get(Constants.API_CAMPAIGNS)
		// .path("collectionStartDate[0]")));
		// assertTrue(testingDates("collectionEndDate",
		// given().port(port).auth().oauth2(accessToken).when().get(Constants.API_CAMPAIGNS)
		// .path("collectionEndDate[0]")));
		// assertTrue(testingDates("endDate",
		// given().port(port).auth().oauth2(accessToken).when().get(Constants.API_CAMPAIGNS).path("endDate[0]")));

	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/interviewers"
	 * return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(4)
	void testGetCampaignInterviewer() throws Exception {
		String interviewerJsonPath = "$.[?(@.id == 'INTW1' && @.%s == '%s')]";
		mockMvc
				.perform(get("/api/campaign/SIMPSONS2020X00/interviewers")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						checkJsonPath(interviewerJsonPath, "interviewerFirstName", "Margie"),
						checkJsonPath(interviewerJsonPath, "interviewerLastName", "Lucas"),
						checkJsonPath(interviewerJsonPath, "surveyUnitCount", "2"));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/interviewers"
	 * return 404 when campaign Id is false
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(5)
	void testGetCampaignInterviewerNotFound() throws Exception {
		mockMvc
				.perform(get("/api/campaign/SIMPSONS2020X000000/interviewers")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/state-count"
	 * return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(6)
	void testGetCampaignStateCount() throws Exception {
		String ouJsonPath = "$.organizationUnits.[?(@.idDem == 'OU-NORTH' && @.%s == '%s')]";

		mockMvc
				.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/state-count")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						checkJsonPath(ouJsonPath, "nvmCount", "0"),
						checkJsonPath(ouJsonPath, "nnsCount", "0"),
						checkJsonPath(ouJsonPath, "anvCount", "0"),
						checkJsonPath(ouJsonPath, "vinCount", "1"),
						checkJsonPath(ouJsonPath, "vicCount", "0"),
						checkJsonPath(ouJsonPath, "prcCount", "0"),
						checkJsonPath(ouJsonPath, "aocCount", "0"),
						checkJsonPath(ouJsonPath, "apsCount", "0"),
						checkJsonPath(ouJsonPath, "insCount", "0"),
						checkJsonPath(ouJsonPath, "wftCount", "0"),
						checkJsonPath(ouJsonPath, "wfsCount", "0"),
						checkJsonPath(ouJsonPath, "tbrCount", "4"),
						checkJsonPath(ouJsonPath, "finCount", "0"),
						checkJsonPath(ouJsonPath, "cloCount", "0"),
						checkJsonPath(ouJsonPath, "nvaCount", "0"),
						checkJsonPath(ouJsonPath, "npaCount", "0"),
						checkJsonPath(ouJsonPath, "npiCount", "0"),
						checkJsonPath(ouJsonPath, "rowCount", "0"),
						checkJsonPath(ouJsonPath, "total", "5"));

	}

	@Test
	@Order(7)
	void testPutClosingCauseNoPreviousClosingCause()
			throws Exception {
		mockMvc
				.perform(put("/api/survey-unit/11/closing-cause/NPI")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
		Assert.assertEquals(ClosingCauseType.NPI, closingCauses.get(0).getType());

	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/state-count"
	 * return 404 when campaign Id is false
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(7)
	void testGetCampaignStateCountNotFound() throws Exception {
		mockMvc
				.perform(get("/api/campaign/test/survey-units/state-count")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(8)
	void testPutClosingCausePreviousClosingCause() throws Exception {
		mockMvc
				.perform(put("/api/survey-unit/11/closing-cause/NPA")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
		Assert.assertEquals(ClosingCauseType.NPA, closingCauses.get(0).getType());

	}

	@Test
	@Disabled("WIP")
	@Order(9)
	void testPutCloseSU() throws Exception {
		String ouJsonPath = "$.organizationUnits.[?(@.idDem == 'OU-NORTH' && @.%s == '%s')]";
		mockMvc
				.perform(put("/api/survey-unit/14/close/ROW")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		mockMvc
				.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/state-count")
						.with(authentication(MANAGER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						checkJsonPath(ouJsonPath, "tbrCount", "3"),
						checkJsonPath(ouJsonPath, "rowCount", "1")

				);
	}

	/**
	 * Test that the GET endpoint
	 * "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Disabled("WIP")
	@Order(8)
	void testGetCampaignInterviewerStateCount() throws Exception {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when()
				.get("api/campaign/SIMPSONS2020X00/survey-units/interviewer/INTW1/state-count").then().statusCode(200)
				.and()
				.assertThat().body("idDem", equalTo(null)).and()
				.assertThat().body("nvmCount", equalTo(0)).and()
				.assertThat().body("nnsCount", equalTo(0)).and()
				.assertThat().body("anvCount", equalTo(0)).and()
				.assertThat().body("vinCount", equalTo(1)).and()
				.assertThat().body("vicCount", equalTo(0)).and()
				.assertThat().body("prcCount", equalTo(0)).and()
				.assertThat().body("aocCount", equalTo(0)).and()
				.assertThat().body("apsCount", equalTo(0)).and()
				.assertThat().body("insCount", equalTo(0)).and()
				.assertThat().body("wftCount", equalTo(0)).and()
				.assertThat().body("wfsCount", equalTo(0)).and()
				.assertThat().body("tbrCount", equalTo(1)).and()
				.assertThat().body("finCount", equalTo(0)).and()
				.assertThat().body("cloCount", equalTo(0)).and()
				// .assertThat().body("qnaFinCount",equalTo(0)).and()
				.assertThat().body("nvaCount", equalTo(0)).and()
				.assertThat().body("npaCount", equalTo(0)).and()
				.assertThat().body("npiCount", equalTo(0)).and()
				.assertThat().body("rowCount", equalTo(0)).and()
				.assertThat().body("total", equalTo(2));
	}

	/**
	 * Test that the GET endpoint
	 * "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when campaign Id is false
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(9)
	void testGetCampaignInterviewerStateCountNotFoundCampaign() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when()
				.get("api/campaign/SIMPSONS2020X000000/survey-units/interviewer/INTW1/state-count").then()
				.statusCode(404);
	}

	/**
	 * Test that the GET endpoint
	 * "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when interviewer Id is false
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(10)
	void testGetCampaignInterviewerStateCountNotFoundIntw() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when()
				.get("api/campaign/SIMPSONS2020X00/survey-units/interviewer/test/state-count").then().statusCode(404);
	}

	/* SurveyUnitController */

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 200.
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(11)
	void testGetSurveyUnitDetail() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/survey-unit/11").then().statusCode(200).and()
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
				.assertThat().body("campaign", equalTo("SIMPSONS2020X00")).and()
				.assertThat().body("contactOutcome", nullValue()).and()
				.assertThat().body("comments", empty()).and()
				.assertThat().body("states[0].type", equalTo("VIN")).and()
				.assertThat().body("contactAttempts", empty()).and()
				.assertThat().body("identification.identification", equalTo("IDENTIFIED")).and()
				.assertThat().body("identification.access", equalTo("ACC")).and()
				.assertThat().body("identification.situation", equalTo("ORDINARY")).and()
				.assertThat().body("identification.category", equalTo("PRIMARY")).and()
				.assertThat().body("identification.occupant", equalTo("IDENTIFIED"));
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(12)
	void testGetAllSurveyUnit() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		String accessToken2 = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/survey-units").then().statusCode(200).and()
				.assertThat().body("id", hasItem("11")).and()
				.assertThat().body("campaign", hasItem("SIMPSONS2020X00")).and()
				.assertThat().body("campaignLabel", hasItem("Survey on the Simpsons tv show 2020"));

		Response resp = given().port(port).auth().oauth2(accessToken2).when().get(Constants.API_CAMPAIGNS);
		// Testing dates
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
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(13)
	void testGetSurveyUnitDetailNotFound() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/survey-unit/123456789")
				.then()
				.statusCode(404);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 200
	 * 
	 * @throws InterruptedException
	 * @throws NotFoundException
	 * @throws SurveyUnitException
	 */
	@Test
	@Disabled("WIP")
	@Order(14)
	void testPutSurveyUnitDetail() throws InterruptedException, JsonProcessingException, JSONException,
			SurveyUnitException, NotFoundException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitDetail("GUEST", "20");
		surveyUnitDetailDto.getPersons().get(0).getPhoneNumbers().get(0).setNumber("test");
		surveyUnitDetailDto.getAddress().setL1("test");
		surveyUnitDetailDto.getAddress().setL2("test");
		surveyUnitDetailDto.getAddress().setL3("test");
		surveyUnitDetailDto.getAddress().setL4("test");
		surveyUnitDetailDto.getAddress().setL5("test");
		surveyUnitDetailDto.getAddress().setL6("test");
		surveyUnitDetailDto.getAddress().setL7("test");
		surveyUnitDetailDto.getAddress().setBuilding("testBuilding");
		surveyUnitDetailDto.getAddress().setDoor("testDoor");
		surveyUnitDetailDto.getAddress().setFloor("testFloor");
		surveyUnitDetailDto.getAddress().setStaircase("testStaircase");
		surveyUnitDetailDto.getAddress().setElevator(true);
		surveyUnitDetailDto.getAddress().setCityPriorityDistrict(true);
		surveyUnitDetailDto.setComments(List.of(new CommentDto(CommentType.INTERVIEWER, "test"),
				new CommentDto(CommentType.MANAGEMENT, "test")));
		surveyUnitDetailDto.setStates(List.of(new StateDto(1L, 1590504459838L, StateType.NNS)));
		surveyUnitDetailDto.setContactAttempts(List.of(new ContactAttemptDto(1589268626000L, Status.NOC, Medium.TEL),
				new ContactAttemptDto(1589268800000L, Status.INA, Medium.TEL)));
		surveyUnitDetailDto.setContactOutcome(new ContactOutcomeDto(1589268626000L, ContactOutcomeType.IMP, 2));
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(surveyUnitDetailDto))
				.when()
				.put("api/survey-unit/20")
				.then()
				.statusCode(200);
		Response response = given().port(port).auth().oauth2(accessToken).when().get("api/survey-unit/20");
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
				.assertThat().body("address.building", equalTo("testBuilding")).and()
				.assertThat().body("address.door", equalTo("testDoor")).and()
				.assertThat().body("address.floor", equalTo("testFloor")).and()
				.assertThat().body("address.staircase", equalTo("testStaircase")).and()
				.assertThat().body("address.elevator", equalTo(true)).and()
				.assertThat().body("address.cityPriorityDistrict", equalTo(true)).and()
				.assertThat().body("contactOutcome.type", equalTo(ContactOutcomeType.IMP.toString())).and()
				.assertThat().body("contactOutcome.totalNumberOfContactAttempts", is(2)).and()
				.assertThat().body("comments[1].value", equalTo("test")).and()
				.assertThat()
				.body("comments[1].type",
						is(oneOf(CommentType.MANAGEMENT.toString(), CommentType.INTERVIEWER.toString())))
				.and()
				.assertThat()
				.body("contactAttempts[1].status", is(oneOf(Status.NOC.toString(), Status.INA.toString())));

		// Tests with Junit for Long values
		assertEquals(Long.valueOf(1589268626000L), response.then().extract().jsonPath().getLong("contactOutcome.date"));

	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state/{state}"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(15)
	void testPutSurveyUnitState() throws InterruptedException, JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.when()
				.put("api/survey-unit/12/state/WFT")
				.then()
				.statusCode(200);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 400 with unknown state
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(16)
	void testPutSurveyUnitStateStateFalse() throws InterruptedException, JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.when()
				.put("api/survey-unit/11/state/test")
				.then()
				.statusCode(400);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 403 when not allowed to pass to this state
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(17)
	void testPutSurveyUnitStateNoSu() throws InterruptedException, JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<String> listSu = new ArrayList<>();
		listSu.add("");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.when()
				.put("api/survey-unit/11/state/AOC")
				.then()
				.statusCode(403);
	}

	/**
	 * Test that the PUT endpoint "api/preferences"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(18)
	void testPutPreferences() throws InterruptedException, JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<String> listPreferences = new ArrayList<>();
		listPreferences.add("SIMPSONS2020X00");
		given().port(port).auth().oauth2(accessToken)
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
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(19)
	void testPutPreferencesWrongCampaignId() throws InterruptedException, JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<String> listPreferences = new ArrayList<>();
		listPreferences.add("");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(listPreferences))
				.when()
				.put("api/preferences")
				.then()
				.statusCode(404);
	}

	/**
	 * Test that the GET endpoint
	 * "/campaign/{id}/survey-units/interviewer/{idep}/closing-causes" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(19)
	void testGetCampaignInterviewerClosingCauseCount() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");

		given().port(port).auth().oauth2(accessToken)
				.when().get("api/campaign/SIMPSONS2020X00/survey-units/interviewer/INTW1/closing-causes").then()
				.statusCode(200).and()
				.assertThat().body("npaCount", equalTo(1)).and()
				.assertThat().body("npiCount", equalTo(0)).and()
				.assertThat().body("rowCount", equalTo(0)).and()
				.assertThat().body("total", equalTo(2));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(20)
	void testGetNbSuAbandoned() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().get("api/campaign/SIMPSONS2020X00/survey-units/abandoned")
				.then()
				.statusCode(200).and()
				.assertThat().body("count", equalTo(0));
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/contact-outcomes[?date={date}]" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(20)
	void testGetContactOutcomeCountByCampaign() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when()
				.get("api/campaign/SIMPSONS2020X00/survey-units/contact-outcomes")
				.then().statusCode(200)
				.and().assertThat().body("organizationUnits[0].idDem", equalTo("OU-NORTH"))
				.and().assertThat().body("organizationUnits[0].labelDem", equalTo("North region organizational unit"))
				.and().assertThat().body("organizationUnits[0].inaCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].refCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].impCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].ucdCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].utrCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].alaCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].dcdCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].nuhCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].dukCount", equalTo(1))
				.and().assertThat().body("organizationUnits[0].duuCount", equalTo(0))
				.and().assertThat().body("organizationUnits[0].noaCount", equalTo(0));
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/survey-units/contact-outcomes[?date={date}]" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(20)
	void testGetContactOutcomeCountAllCampaign() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/campaigns/survey-units/contact-outcomes").then()
				.statusCode(200)
				.and().assertThat().body("[0].campaign.id", equalTo("SIMPSONS2020X00"))
				.and().assertThat().body("[0].campaign.label", equalTo("Survey on the Simpsons tv show 2020"))
				.and().assertThat().body("[0].inaCount", equalTo(0))
				.and().assertThat().body("[0].refCount", equalTo(0))
				.and().assertThat().body("[0].impCount", equalTo(0))
				.and().assertThat().body("[0].ucdCount", equalTo(0))
				.and().assertThat().body("[0].utrCount", equalTo(0))
				.and().assertThat().body("[0].alaCount", equalTo(0))
				.and().assertThat().body("[0].dcdCount", equalTo(0))
				.and().assertThat().body("[0].nuhCount", equalTo(0))
				.and().assertThat().body("[0].nuhCount", equalTo(0))
				.and().assertThat().body("[0].dukCount", equalTo(1))
				.and().assertThat().body("[0].duuCount", equalTo(0))
				.and().assertThat().body("[0].noaCount", equalTo(0));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(21)
	void testGetNbSuAbandonedNotFound() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().get("api/campaign/test/survey-units/abandoned")
				.then()
				.statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/not-attributed"
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(22)
	void testGetNbSuNotAttributed() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().get("api/campaign/SIMPSONS2020X00/survey-units/not-attributed")
				.then()
				.statusCode(200).and()
				.assertThat().body("count", equalTo(0));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/not-attributed"
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(23)
	void testGetNbSuNotAttributedNotFound() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().get("api/campaign/test/survey-units/not-attributed")
				.then()
				.statusCode(404);
	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying all dates
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(29)
	void testPutVisibilityModifyAllDates() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body("{\"managementStartDate\": 1575937000000 , "
						+ "\"interviewerStartDate\": 1576801000000,"
						+ "\"identificationPhaseStartDate\": 1577233000000,"
						+ "\"collectionStartDate\": 1577837800000,"
						+ "\"collectionEndDate\": 1640996200000,"
						+ "\"endDate\": 1641514600000}")
				.when()
				.put("api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.then()
				.statusCode(200);
		Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("SIMPSONS2020X00",
				"OU-NORTH");
		assertEquals(true, visi.isPresent());
		assertEquals(1575937000000L, visi.get().getManagementStartDate());
		assertEquals(1576801000000L, visi.get().getInterviewerStartDate());
		assertEquals(1577233000000L, visi.get().getIdentificationPhaseStartDate());
		assertEquals(1577837800000L, visi.get().getCollectionStartDate());
		assertEquals(1640996200000L, visi.get().getCollectionEndDate());
		assertEquals(1641514600000L, visi.get().getEndDate());
	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying start date
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(30)
	void testPutVisibilityModifyCollectionStartDate()
			throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body("{\"collectionStartDate\": 1577847800000}")
				.when()
				.put("api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.then()
				.statusCode(200);
		Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("SIMPSONS2020X00",
				"OU-NORTH");
		assertEquals(true, visi.isPresent());
		assertEquals(1577847800000L, visi.get().getCollectionStartDate());
	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying end date
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(31)
	void testPutVisibilityModifyCollectionEndDate()
			throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body("{\"collectionEndDate\": 1577857800000}")
				.when()
				.put("api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.then()
				.statusCode(200);
		Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("SIMPSONS2020X00",
				"OU-NORTH");
		assertEquals(true, visi.isPresent());
		assertEquals(1577857800000L, visi.get().getCollectionEndDate());
	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 400 when empty body
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(32)
	void testPutVisibilityEmptyBody() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body("{}")
				.when()
				.put("api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.then()
				.statusCode(400);
	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 400 when bad format
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(33)
	void testPutVisibilityBadFormat() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body("{\"managementStartDate\": 1640996200000, "
						+ "\"interviewerStartDate\": \"10/10/2020\","
						+ "\"identificationPhaseStartDate\": 1641514600000,"
						+ "\"collectionStartDate\": 1577233000000,"
						+ "\"collectionEndDate\": 1576801000000,"
						+ "\"endDate\": 1575937000000}")
				.when()
				.put("api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.then()
				.statusCode(400);
	}

	/**
	 * Test that the POST endpoint "api/message" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(34)
	void testPostMessage() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<String> recipients = new ArrayList<String>();
		recipients.add("SIMPSONS2020X00");
		MessageDto message = new MessageDto("TEST", recipients);
		message.setSender("abc");
		given().port(port).auth().oauth2(accessToken).contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(message)).when().post("api/message").then().statusCode(200);
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
	@Disabled("WIP")
	@Order(35)
	void testPostMessageBadFormat() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(null)).when().post("api/message").then().statusCode(400);
	}

	/**
	 * Test that the GET endpoint
	 * "api/messages/{id}" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(36)
	void testGetMessage() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/messages/INTW1").then().statusCode(200).and()
				.assertThat().body("text", hasItem("TEST"));
	}

	/**
	 * Test that the GET endpoint
	 * "api/messages/{id}" return empty body with a wrong id
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(37)
	void testGetMessageWrongId() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/messages/123456789").then().statusCode(200).and()
				.assertThat().body("isEmpty()", Matchers.is(true));
	}

	/**
	 * Test that the put endpoint "api/message/{id}/interviewer/{idep}/read"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(38)
	void testPutMessageAsRead() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "intw1", "a");
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		given().port(port).auth().oauth2(accessToken).contentType("application/json").when()
				.put("api/message/" + messageId + "/interviewer/INTW1/read").then().statusCode(200);
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
	@Disabled("WIP")
	@Order(39)
	void testPutMessageAsDelete() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "intw1", "a");
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		given().port(port).auth().oauth2(accessToken).contentType("application/json").when()
				.put("api/message/" + messageId + "/interviewer/INTW1/delete").then().statusCode(200);
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
	@Disabled("WIP")
	@Order(40)
	void testPutMessageAsReadWrongId() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "intw1", "a");
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		given().port(port).auth().oauth2(accessToken).contentType("application/json").when()
				.put("api/message/" + messageId + "/interviewer/Test/read").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint
	 * "/message-history" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(41)
	void testGetMessageHistory() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/message-history").then().statusCode(200).and()
				.assertThat().body("text", hasItem("TEST"));
	}

	/**
	 * Test that the POST endpoint
	 * "/verify-name" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(42)
	void testPostVerifyName() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		WsText message = new WsText("simps");
		given().port(port).auth().oauth2(accessToken).contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(message)).when().post("api/verify-name").then()
				.statusCode(200).and().assertThat().body("id", hasItem("SIMPSONS2020X00"));
	}

	/**
	 * Test that the POST endpoint "api/message" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(43)
	void testPostMessageSysteme() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "intw1", "a");
		List<String> recipients = new ArrayList<String>();
		recipients.add("SIMPSONS2020X00");
		MessageDto message = new MessageDto("Synchronisation", recipients);
		given().port(port).auth().oauth2(accessToken).contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(message)).when().post("api/message").then().statusCode(200);
		List<MessageDto> messages = messageRepository
				.findMessagesDtoByIds(messageRepository.getMessageIdsByInterviewer("INTW1"));
		assertEquals("TEST", messages.get(0).getText());
	}

	/**
	 * Test that the Get endpoint
	 * "/interviewers" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(44)
	void testGetInterviewer() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/interviewers").then().statusCode(200)
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
	@Disabled("WIP")
	@Order(45)
	void testGetInterviewerForCampaign() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/interviewer/INTW1/campaigns").then()
				.statusCode(200)
				.and().assertThat().body("id", hasItem("SIMPSONS2020X00"))
				.and().assertThat().body("label", hasItem("Survey on the Simpsons tv show 2020"));
		assertTrue(testingDates("managementStartDate", given().port(port).auth().oauth2(accessToken).when()
				.get("api/interviewer/INTW1/campaigns").path("managementStartDate[0]")));
		assertTrue(testingDates("endDate",
				given().port(port).auth().oauth2(accessToken).when().get("api/interviewer/INTW1/campaigns")
						.path("endDate[0]")));
	}

	/**
	 * Test that the Get endpoint
	 * "/interviewer/{id}/campaigns" return 404
	 * when interviewer not exist
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(46)
	void testGetInterviewerNotExistForCampaign() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/interviewer/INTW123/campaigns").then()
				.statusCode(404);
	}

	/**
	 * Test that the Get endpoint
	 * "/survey-units/closable" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(47)
	void testGetSUCloasable() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");

		Optional<Visibility> visiOpt = visibilityRepository.findVisibilityByCampaignIdAndOuId("VQS2021X00", "OU-NORTH");
		if (visiOpt.isPresent()) {
			Visibility visi = visiOpt.get();
			Long collectionEndDate = visi.getCollectionEndDate();
			Long endDate = visi.getEndDate();

			visi.setCollectionEndDate(System.currentTimeMillis() - 86400000);
			visi.setEndDate(System.currentTimeMillis() + 86400000);
			visibilityRepository.save(visi);

			given().port(port).auth().oauth2(accessToken).when()
					.get("api/survey-units/closable").then().statusCode(200)
					.and().assertThat().body("id", hasItem("21"))
					.and().assertThat().body("ssech", hasItem(1));

			visi.setCollectionEndDate(collectionEndDate);
			visi.setEndDate(endDate);
			visibilityRepository.save(visi);

		} else {
			Assert.fail("No visibility found for VQS2021X00  and OU-NORTH");
		}
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(48)
	void testGetContactOutcomeCountByCampaignAndInterviewer()
			throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when()
				.get("api/campaign/SIMPSONS2020X00/survey-units/interviewer/INTW1/contact-outcomes").then()
				.statusCode(200)
				.and().assertThat().body("inaCount", equalTo(0))
				.and().assertThat().body("refCount", equalTo(0))
				.and().assertThat().body("impCount", equalTo(0))
				.and().assertThat().body("ucdCount", equalTo(0))
				.and().assertThat().body("utrCount", equalTo(0))
				.and().assertThat().body("alaCount", equalTo(0))
				.and().assertThat().body("dcdCount", equalTo(0))
				.and().assertThat().body("nuhCount", equalTo(0))
				.and().assertThat().body("dukCount", equalTo(0))
				.and().assertThat().body("duuCount", equalTo(0))
				.and().assertThat().body("noaCount", equalTo(0));
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]"
	 * return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(49)
	void testGetContactOutcomeCountByCampaignNotExistAndInterviewer()
			throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when()
				.get("api/campaign/SIMPSONS2020X000000/survey-units/interviewer/INTW1/contact-outcomes").then()
				.statusCode(404);
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]"
	 * return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(50)
	void testGetContactOutcomeCountByCampaignAndInterviewerNotExist()
			throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when()
				.get("api/campaign/SIMPSONS2020X00/survey-units/interviewer/INTW123/contact-outcomes").then()
				.statusCode(404);
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/contact-outcomes[?date={date}]" return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(52)
	void testGetContactOutcomeCountByCampaignNotExist()
			throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when()
				.get("api/campaign/SIMPSONS2020X000000/survey-units/contact-outcomes")
				.then().statusCode(404);
	}

	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(54)
	void testPutCommentOnSu() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		String accessToken2 = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "intw1", "a");
		SurveyUnit su = new SurveyUnit();
		su.setId("11");
		Comment comment = new Comment(111L, CommentType.MANAGEMENT, "Test of comment", su);
		given()
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(new CommentDto(comment)))
				.when()
				.put("api/survey-unit/11/comment").then().statusCode(200);
		given().port(port).auth().oauth2(accessToken2).when().get("api/survey-unit/11").then().statusCode(200)
				.and().assertThat().body("comments[0].type", equalTo(CommentType.MANAGEMENT.toString()))
				.and().assertThat().body("comments[0].value", equalTo("Test of comment"));
	}

	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/comment" return 404
	 * when id not exist
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(55)
	void testPutCommentSuNotExist() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnit su = new SurveyUnit();
		su.setId("11111111111");
		Comment comment = new Comment(111L, CommentType.MANAGEMENT, "Test of comment", su);
		given()
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(new CommentDto(comment)))
				.when()
				.put("api/survey-unit/11111111111/comment").then().statusCode(404);
	}

	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/viewed" return 200 and viewed attribut set to true
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(56)
	void testPutSuViewed() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnit su = new SurveyUnit();
		su.setId("24");
		given()
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.when()
				.put("api/survey-unit/24/viewed").then().statusCode(200);
		assertEquals(true, surveyUnitRepository.findById("24").get().getViewed());
	}

	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/viewed" return 404
	 * when id not exist
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(57)
	void testPutSuViewedNotExist() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnit su = new SurveyUnit();
		su.setId("11111111111");
		given()
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.when()
				.put("api/survey-unit/11111111111/viewed").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Disabled("WIP")
	@Order(58)
	void testGetOrganizationUnits() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken).when().get("api/organization-units").then().statusCode(200).and()
				.assertThat().body("id", hasItem("OU-NORTH")).and()
				.assertThat().body("label", hasItem("North region organizational unit")).and()
				.assertThat().body("id", hasItem("OU-SOUTH")).and()
				.assertThat().body("label", hasItem("South region organizational unit")).and()
				.assertThat().body("id", hasItem("OU-NATIONAL")).and()
				.assertThat().body("label", hasItem("National organizational unit"));
	}

	/**
	 * Test that the Post endpoint
	 * "/campaign" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(100)
	void testPostCampaignContext() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		CampaignContextDto campDto = new CampaignContextDto();
		campDto.setCampaign("campId");
		campDto.setCampaignLabel("An other campaign");
		campDto.setVisibilities(new ArrayList<>());
		campDto.setReferents(Arrays.asList(new ReferentDto("Bob", "Marley", "0123456789", "PRIMARY")));
		campDto.setIdentificationConfiguration(IdentificationConfiguration.IASCO);
		campDto.setContactOutcomeConfiguration(ContactOutcomeConfiguration.F2F);
		campDto.setContactAttemptConfiguration(ContactAttemptConfiguration.F2F);

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
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campDto))
				.when()
				.post(Constants.API_CAMPAIGN)
				.then()
				.statusCode(200);

		Optional<Campaign> campOpt = campaignRepository.findById("CAMPID");
		assertTrue(campOpt.isPresent());
		assertEquals("An other campaign", campOpt.get().getLabel());

		Optional<Visibility> visi1Opt = visibilityRepository.findVisibilityByCampaignIdAndOuId("CAMPID", "OU-NORTH");
		Optional<Visibility> visi2Opt = visibilityRepository.findVisibilityByCampaignIdAndOuId("CAMPID", "OU-SOUTH");

		assertTrue(visi1Opt.isPresent());
		assertTrue(visi2Opt.isPresent());

		Visibility visi = visi1Opt.get();
		assertEquals(1111L, visi.getCollectionStartDate());
		assertEquals(2222L, visi.getCollectionEndDate());
		assertEquals(3333L, visi.getIdentificationPhaseStartDate());
		assertEquals(4444L, visi.getInterviewerStartDate());
		assertEquals(5555L, visi.getManagementStartDate());
		assertEquals(6666L, visi.getEndDate());

		List<ReferentDto> refs = referentservice.findByCampaignId("CAMPID");
		assertEquals(1, refs.size());
		assertEquals("Bob", refs.get(0).getFirstName());
		assertEquals("Marley", refs.get(0).getLastName());
		assertEquals("0123456789", refs.get(0).getPhoneNumber());
		assertEquals("PRIMARY", refs.get(0).getRole());

		assertEquals(IdentificationConfiguration.IASCO, campOpt.get().getIdentificationConfiguration());
		assertEquals(ContactAttemptConfiguration.F2F, campOpt.get().getContactAttemptConfiguration());
		assertEquals(ContactOutcomeConfiguration.F2F, campOpt.get().getContactOutcomeConfiguration());
		assertEquals(false, campOpt.get().isCommunicationConfiguration());

	}

	/**
	 * Test that the Post endpoint
	 * "/campaign" returns 400
	 * when an attribute is missing
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(101)
	void testPostCampaignContextNoLabel() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
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
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campDto))
				.when()
				.post(Constants.API_CAMPAIGN)
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
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(102)
	void testPostCampaignContextMissingOU() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
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
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campDto))
				.when()
				.post(Constants.API_CAMPAIGN)
				.then()
				.statusCode(400);

		// Campaign should not have been created
		Optional<Campaign> campOpt = campaignRepository.findById("campId3");
		assertTrue(!campOpt.isPresent());
	}

	/**
	 * Test that the Post endpoint
	 * "/organization-unit/context" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(103)
	void testPostOrganizationUnitContext() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		ArrayList<OrganizationUnitContextDto> listOU = new ArrayList<>();

		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto();
		ou1.setId("OU-NORTH2");
		ou1.setLabel("North region OU 2");
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
		ou2.setId("OU-NATIONAL2");
		ou2.setLabel("National OU 2");
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
				.auth().oauth2(accessToken)
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
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(104)
	void testPostOrganizationUnitContextDuplicateUser()
			throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		ArrayList<OrganizationUnitContextDto> listOU = new ArrayList<>();

		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto();
		ou1.setId("OU-NORTH3");
		ou1.setLabel("North region OU 3");
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
		ou2.setId("OU-NATIONAL3");
		ou2.setLabel("National OU 3");
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
				.auth().oauth2(accessToken)
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
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(105)
	void testPostOrganizationUnitContextNoOU() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		ArrayList<OrganizationUnitContextDto> listOU = new ArrayList<>();

		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto();
		ou1.setId("OU-NORTH3");
		ou1.setLabel("North region OU 3");
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
		ou2.setId("OU-NATIONAL3");
		ou2.setLabel("National OU 3");
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
				.auth().oauth2(accessToken)
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
	 * "api/interviewers" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(106)
	void testPostInterviewers() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<InterviewerContextDto> listInterviewers = new ArrayList<>();

		InterviewerContextDto interv1 = new InterviewerContextDto(
				"INTERV1",
				"Pierre",
				"Legrand",
				"pierre.legrand@insee.fr",
				"06 XX XX XX XX",
				Title.MISTER);

		InterviewerContextDto interv2 = new InterviewerContextDto(
				"INTERV2",
				"Clara",
				"Legouanec",
				"clara.legouanec@insee.fr",
				"06 XX XX XX XX",
				Title.MISS);

		listInterviewers.add(interv1);
		listInterviewers.add(interv2);

		given()
				.auth().oauth2(accessToken)
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
		assertEquals(Title.MISTER, interv1Opt.get().getTitle());

		assertEquals("Clara", interv2Opt.get().getFirstName());
		assertEquals("Legouanec", interv2Opt.get().getLastName());
		assertEquals("clara.legouanec@insee.fr", interv2Opt.get().getEmail());
		assertEquals("06 XX XX XX XX", interv2Opt.get().getPhoneNumber());
		assertEquals(Title.MISS, interv2Opt.get().getTitle());

	}

	/**
	 * Test that the Post endpoint
	 * "api/interviewers" returns 400
	 * when an email is missing
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(107)
	void testPostInterviewersMissingEmail() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<InterviewerContextDto> listInterviewers = new ArrayList<>();

		InterviewerContextDto interv1 = new InterviewerContextDto(
				"INTERV4",
				"Pierre",
				"Legrand",
				"pierre.legrand@insee.fr",
				"06 XX XX XX XX",
				Title.MISTER);

		InterviewerContextDto interv2 = new InterviewerContextDto(
				"INTERV5",
				"Clara",
				"Legouanec",
				null,
				"06 XX XX XX XX",
				Title.MISS);

		listInterviewers.add(interv1);
		listInterviewers.add(interv2);

		given()
				.auth().oauth2(accessToken)
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
	 * "api/interviewers" returns 400
	 * when an iterviewer id is present twice
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(108)
	void testPostInterviewersDuplicateId() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<InterviewerContextDto> listInterviewers = new ArrayList<>();

		InterviewerContextDto interv1 = new InterviewerContextDto(
				"INTERV3",
				"Pierre",
				"Legrand",
				"pierre.legrand@insee.fr",
				"06 XX XX XX XX",
				Title.MISTER);

		InterviewerContextDto interv2 = new InterviewerContextDto(
				"INTERV3",
				"Clara",
				"Legouanec",
				"clara.legouanec@insee.fr",
				"06 XX XX XX XX",
				Title.MISS);

		listInterviewers.add(interv1);
		listInterviewers.add(interv2);

		given()
				.auth().oauth2(accessToken)
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
	 * Test that the Post endpoint
	 * "api/interviewers" returns 200
	 * and provide default title MISTER if no title provided
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(109)
	void testPostInterviewersWithNoTitle() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<InterviewerContextDto> listInterviewers = new ArrayList<>();

		InterviewerContextDto intervNoTitle = new InterviewerContextDto(
				"INTERV_WITHOUT_TITLE",
				"Pierre",
				"Legrand",
				"pierre.legrand@insee.fr",
				"06 XX XX XX XX",
				null);

		listInterviewers.add(intervNoTitle);

		given()
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(listInterviewers))
				.when()
				.post("api/interviewers")
				.then()
				.statusCode(200);

		// Interviewers should have been added
		Optional<Interviewer> intervNoTitleOpt = interviewerRepository.findById("INTERV_WITHOUT_TITLE");
		assertTrue(intervNoTitleOpt.isPresent());
		assertEquals(Title.MISTER, intervNoTitleOpt.get().getTitle());
	}

	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 200
	 * 
	 * @throws JsonProcessingException
	 * @throws JSONException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(111)
	void testPostSurveyUnits() throws JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("8");
		su.setCampaign("SIMPSONS2020X00");
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
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su)))
				.post("api/survey-units")
				.then().statusCode(200);
		Assert.assertTrue(surveyUnitRepository.findById("8").isPresent());
	}

	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when id dupliate in DB
	 * 
	 * @throws JsonProcessingException
	 * @throws JSONException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(112)
	void testPostSurveyUnitsDuplicateInDB() throws JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("8");
		su.setCampaign("SIMPSONS2020X00");
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
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su)))
				.post("api/survey-units")
				.then().statusCode(400);
	}

	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when id dupliate in body
	 * 
	 * @throws JsonProcessingException
	 * @throws JSONException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(113)
	void testPostSurveyUnitsDuplicateInBody() throws JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("9");
		su.setCampaign("SIMPSONS2020X00");
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
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su, su)))
				.post("api/survey-units")
				.then().statusCode(400);
	}

	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when OrganizationUnitId does not exist
	 * 
	 * @throws JsonProcessingException
	 * @throws JSONException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(114)
	void testPostSurveyUnitsOUNotExist() throws JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("9");
		su.setCampaign("SIMPSONS2020X00");
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
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su)))
				.post("api/survey-units")
				.then().statusCode(400);
	}

	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when campaignId does not exist
	 * 
	 * @throws JsonProcessingException
	 * @throws JSONException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(115)
	void testPostSurveyUnitsCampaignNotExist() throws JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("9");
		su.setCampaign("campaignTest");
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
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su)))
				.post("api/survey-units")
				.then().statusCode(400);
	}

	/**
	 * Test that the POST endpoint
	 * "/survey-units returns 400 when surveyUnit body is not valid
	 * 
	 * @throws JsonProcessingException
	 * @throws JSONException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(117)
	void testPostSurveyUnitsSUNotValid() throws JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId("");
		su.setCampaign("SIMPSONS2020X00");
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
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su)))
				.post("api/survey-units")
				.then().statusCode(400);
		// Campaign null
		su.setId("9");
		su.setCampaign("");
		given()
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su)))
				.post("api/survey-units")
				.then().statusCode(400);
		// Persons Null
		su.setPersons(List.of());
		su.setCampaign("SIMPSONS2020X00");
		given()
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su)))
				.post("api/survey-units")
				.then().statusCode(400);
	}

	/**
	 * Test that the Post endpoint
	 * "/survey-units/interviewers" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(118)
	void testPostAssignements() throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<SurveyUnitInterviewerLinkDto> list = new ArrayList<>();
		addUnattributedSU("101");
		addUnattributedSU("102");
		SurveyUnitInterviewerLinkDto assign1 = new SurveyUnitInterviewerLinkDto("101", "INTW4");
		SurveyUnitInterviewerLinkDto assign2 = new SurveyUnitInterviewerLinkDto("102", "INTW3");

		list.add(assign1);
		list.add(assign2);

		given()
				.auth().oauth2(accessToken)
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
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(119)
	void testPostAssignementsNoInterviewerDoesntExist()
			throws InterruptedException, JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		List<SurveyUnitInterviewerLinkDto> list = new ArrayList<>();
		addUnattributedSU("103");
		addUnattributedSU("104");
		SurveyUnitInterviewerLinkDto assign1 = new SurveyUnitInterviewerLinkDto("103", "INTW4");
		SurveyUnitInterviewerLinkDto assign2 = new SurveyUnitInterviewerLinkDto("104", "INTWDOESNTEXIST");

		list.add(assign1);
		list.add(assign2);

		given()
				.auth().oauth2(accessToken)
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

	/**
	 * Test that the Post endpoint
	 * "/orgaization-unit/id/users" returns 200
	 * 
	 * @throws JSONException
	 * @throws JsonProcessingException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(120)
	void testPostUsersByOU() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given()
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper()
						.writeValueAsString(List.of(new UserContextDto("TEST", "test", "test", null, null),
								new UserContextDto("TEST2", "test2", "test2", null, null))))
				.when()
				.post("api/organization-unit/OU-NORTH/users")
				.then()
				.statusCode(200);

		// SU should have been attributted to interviewers
		Optional<User> user = userRepository.findById("TEST");
		assertEquals("OU-NORTH", user.get().getOrganizationUnit().getId());
	}

	/**
	 * Test that the Post endpoint
	 * "/orgaization-unit/id/users" returns 200
	 * 
	 * @throws JSONException
	 * @throws JsonProcessingException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("WIP")
	@Order(121)
	void testPostUsersByOUThatDoesNotExist() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given()
				.auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper()
						.writeValueAsString(List.of(new UserContextDto("TEST2", "test2", "test2", null, null),
								new UserContextDto("TEST2", "test2", "test2", null, null))))
				.when()
				.post("api/organization-unit/OU-TEST/users")
				.then()
				.statusCode(400);

		// SU should have been attributted to interviewers
		Optional<User> user = userRepository.findById("TEST");
		assertEquals("OU-NORTH", user.get().getOrganizationUnit().getId());
	}

	@Test
	@Disabled("WIP")
	@Order(200)
	void testDeleteSurveyUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/survey-unit/11")
				.then().statusCode(200);
		assertTrue(surveyUnitRepository.findById("11").isEmpty());
	}

	@Test
	@Disabled("WIP")
	@Order(201)
	void testDeleteSurveyUnitNotExist() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/survey-unit/toto")
				.then().statusCode(404);
	}

	@Test
	@Disabled("WIP")
	@Order(202)
	void testDeleteCampaign() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/campaign/XCLOSEDX00")
				.then().statusCode(200);
		assertTrue(campaignRepository.findById("XCLOSEDX00").isEmpty());

		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/campaign/SIMPSONS2020X00")
				.then().statusCode(409);

		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/campaign/SIMPSONS2020X00?force=false")
				.then().statusCode(409);

		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/campaign/SIMPSONS2020X00?force=true")
				.then().statusCode(200);
		assertTrue(campaignRepository.findById("SIMPSONS2020X00").isEmpty());
	}

	@Test
	@Disabled("WIP")
	@Order(203)
	void testDeleteCampaignNotExist() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/campaign/SIMPSONS2020XTT")
				.then().statusCode(404);
	}

	@Test
	@Disabled("WIP")
	@Order(204)
	void testDeleteUser() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/user/JKL")
				.then().statusCode(200);
		assertTrue(userRepository.findById("JKL").isEmpty());
	}

	@Test
	@Disabled("WIP")
	@Order(205)
	void testDeleteUserNotExist() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/user/USER")
				.then().statusCode(404);
	}

	@Test
	@Disabled("WIP")
	@Order(206)
	void testDeleteOrganizationUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		// delete all SU before delete OU
		surveyUnitRepository.findByOrganizationUnitIdIn(List.of("OU-NORTH"))
				.stream().forEach(su -> surveyUnitRepository.delete(su));
		// delete all Users before delete OU
		userRepository.findAllByOrganizationUnitId("OU-NORTH")
				.stream().forEach(u -> {
					messageService.deleteMessageByUserId(u.getId());
					userService.delete(u.getId());
				});

		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/organization-unit/OU-NORTH")
				.then().statusCode(200);
		assertTrue(organizationUnitRepository.findById("OU-NORTH").isEmpty());
	}

	@Test
	@Disabled("WIP")
	@Order(207)
	void testDeleteOrganizationUnitWithUsersOrSurveyUnits() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/organization-unit/OU-SOUTH")
				.then().statusCode(400);
	}

	@Test
	@Disabled("WIP")
	@Order(208)
	void testDeleteOrganizationUnitNotExist() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().delete("api/organization-unit/TEST")
				.then().statusCode(404);
	}

	@Test
	@Disabled("WIP")
	@Order(209)
	void testCreateValidUser() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		UserDto user = generateValidUser();
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().post("api/user")
				.then().statusCode(201);
	}

	@Test
	@Disabled("WIP")
	@Order(210)
	void testCreateAreadyPresentUser() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		UserDto user = generateValidUser();
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().post("api/user")
				.then().statusCode(409);
	}

	@Test
	@Disabled("WIP")
	@Order(211)
	void testCreateInvalidUser() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");

		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(null))
				.when().post("api/user")
				.then().statusCode(400);

		UserDto user = generateValidUser();
		user.setFirstName(null);
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().post("api/user")
				.then().statusCode(400);

		user = generateValidUser();
		user.setLastName(null);
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().post("api/user")
				.then().statusCode(400);

		user = generateValidUser();
		user.setId(null);
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().post("api/user")
				.then().statusCode(400);

		user = generateValidUser();
		OrganizationUnitDto missingOU = user.getOrganizationUnit();
		missingOU.setId("WHERE_IS_CHARLIE");
		user.setOrganizationUnit(missingOU);
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().post("api/user")
				.then().statusCode(400);
	}

	@Test
	@Disabled("WIP")
	@Order(212)
	void testUpdateMissingUser() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		UserDto user = generateValidUser();
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().put("api/user/TEST")
				.then().statusCode(404);
	}

	@Test
	@Disabled("WIP")
	@Order(213)
	void testUpdateWrongUser() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		UserDto user = generateValidUser();
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().put("api/user/GHI")
				.then().statusCode(409);
	}

	@Test
	@Disabled("WIP")
	@Order(214)
	void testUpdateUser() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		UserDto user = generateValidUser();
		user.setId("GHI");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(user))
				.when().put("api/user/GHI")
				.then().statusCode(200).and()
				.assertThat().body("id", equalTo("GHI")).and()
				.assertThat().body("firstName", equalTo("Bob")).and()
				.assertThat().body("lastName", equalTo("Lennon")).and()
				.assertThat().body("organizationUnit.id", equalTo("OU-SOUTH")).and()
				.assertThat().body("organizationUnit.label", equalTo("South region organizational unit"));
	}

	@Test
	@Disabled("WIP")
	@Order(215)
	void testAssignUser() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().put("api/user/GHI/organization-unit/OU-SOUTH")
				.then().statusCode(200).and()
				.assertThat().body("id", equalTo("GHI")).and()
				.assertThat().body("organizationUnit.id", equalTo("OU-SOUTH")).and()
				.assertThat().body("organizationUnit.label", equalTo("South region organizational unit"));
	}

	@Test
	@Disabled("WIP")
	@Order(216)
	void testAssignUserMissingUser() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().put("api/user/MISSING/organization-unit/OU-SOUTH")
				.then().statusCode(404);
	}

	@Test
	@Disabled("WIP")
	@Order(217)
	void testAssignUserMissingOu() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().put("api/user/GHI/organization-unit/MISSING")
				.then().statusCode(404);
	}

	@Test
	@Disabled("WIP")
	@Order(218)
	void testOngoing() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().get("/campaigns/ZCLOSEDX00/ongoing")
				.then().statusCode(200).and()
				.assertThat().body("ongoing", equalTo(false));
		given().port(port).auth().oauth2(accessToken)
				.when().get("/campaigns/VQS2021X00/ongoing")
				.then().statusCode(200).and()
				.assertThat().body("ongoing", equalTo(true));
	}

	@Test
	@Disabled("WIP")
	@Order(219)
	void testOngoingNotFound() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		given().port(port).auth().oauth2(accessToken)
				.when().get("/campaigns/MISSING/ongoing")
				.then().statusCode(404);
	}

	@Test
	@Disabled("WIP")
	@Order(220)
	void testPutCampaign() throws JSONException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");

		CampaignContextDto campaignContext = new CampaignContextDto();
		campaignContext.setCampaign("ZCLOSEDX00");
		campaignContext.setCampaignLabel("Everyday life and health survey 2021");

		VisibilityContextDto wvcd = generateVisibilityContextDto("OU-WEST", "ZCLOSEDX00");
		wvcd.setOrganizationalUnit("OU-WEST");
		wvcd.setEndDate(wvcd.getEndDate() + 1);
		VisibilityContextDto svcd = generateVisibilityContextDto("OU-SOUTH", "ZCLOSEDX00");
		svcd.setOrganizationalUnit("OU-SOUTH");
		VisibilityContextDto emptyVcd = new VisibilityContextDto();
		emptyVcd.setOrganizationalUnit("OU-WEST");

		VisibilityContextDto missingVcd = generateVisibilityContextDto("OU-SOUTH", "ZCLOSEDX00");
		missingVcd.setOrganizationalUnit("OU-TEAPOT");

		VisibilityContextDto invalidVcd = generateVisibilityContextDto("OU-WEST", "ZCLOSEDX00");
		invalidVcd.setOrganizationalUnit("OU-WEST");
		invalidVcd.setInterviewerStartDate(invalidVcd.getIdentificationPhaseStartDate());

		// path variable campaignId not found in DB
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campaignContext))
				.when().put("api/campaign/MISSING")
				.then().statusCode(404);

		// BAD REQUESTS
		campaignContext.setCampaignLabel(null);
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campaignContext))
				.when().put("api/campaign/ZCLOSEDX00")
				.then().statusCode(400);

		campaignContext.setCampaignLabel("  ");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campaignContext))
				.when().put("api/campaign/ZCLOSEDX00")
				.then().statusCode(400);

		campaignContext.setCampaignLabel("");
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campaignContext))
				.when().put("api/campaign/ZCLOSEDX00")
				.then().statusCode(400);

		campaignContext.setCampaignLabel("Everyday life and health survey 2021");
		campaignContext.setVisibilities(List.of(emptyVcd));
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campaignContext))
				.when().put("api/campaign/ZCLOSEDX00")
				.then().statusCode(400);

		// NOT FOUND VISIBILITY
		campaignContext.setVisibilities(List.of(missingVcd));
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campaignContext))
				.when().put("api/campaign/ZCLOSEDX00")
				.then().statusCode(404);

		// CONFLICT due to visibilities
		campaignContext.setVisibilities(List.of(invalidVcd));
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campaignContext))
				.when().put("api/campaign/ZCLOSEDX00")
				.then().statusCode(409);

		// 200
		campaignContext.setVisibilities(List.of(wvcd, svcd));
		campaignContext.setEmail("updated.email@test.com");
		campaignContext.setContactAttemptConfiguration(ContactAttemptConfiguration.TEL);
		campaignContext.setContactOutcomeConfiguration(ContactOutcomeConfiguration.TEL);
		campaignContext.setIdentificationConfiguration(IdentificationConfiguration.NOIDENT);
		given().port(port).auth().oauth2(accessToken)
				.contentType("application/json")
				.body(new ObjectMapper().writeValueAsString(campaignContext))
				.when().put("api/campaign/ZCLOSEDX00")
				.then().statusCode(200);
		assertEquals(wvcd.getEndDate(),
				visibilityRepository.findVisibilityByCampaignIdAndOuId("ZCLOSEDX00", "OU-WEST").get().getEndDate());
		CampaignDto updatedCampaign = campaignRepository.findDtoById("ZCLOSEDX00");
		assertEquals("updated.email@test.com", updatedCampaign.getEmail());
		assertEquals(ContactAttemptConfiguration.TEL, updatedCampaign.getContactAttemptConfiguration());
		assertEquals(ContactOutcomeConfiguration.TEL, updatedCampaign.getContactOutcomeConfiguration());
		assertEquals(IdentificationConfiguration.NOIDENT, updatedCampaign.getIdentificationConfiguration());
	}

	@Test
	@Disabled("WIP")
	@Order(221)
	void testUpdateVisibilityByOu() throws Exception {
		VisibilityDto visibility = generateVisibilityContextDto("OU-WEST", "ZCLOSEDX00");
		visibility.setEndDate(visibility.getEndDate() + 1);
		VisibilityDto invalidVcd = generateVisibilityContextDto("OU-WEST", "ZCLOSEDX00");
		invalidVcd.setInterviewerStartDate(invalidVcd.getIdentificationPhaseStartDate());

		String visibilityJson = asJsonString(visibility);
		String emptyVisibilityJson = asJsonString(new VisibilityDto());
		String invalidVisibilityJson = asJsonString(invalidVcd);

		// BAD REQUEST
		updateVisibility("ZCLOSEDX00", "OU-WEST", emptyVisibilityJson).andExpect(status().isBadRequest());

		// NOT FOUND
		updateVisibility("ZCLOSEDX00", "MISSING", visibilityJson).andExpect(status().isNotFound());

		// CONFLICT
		updateVisibility("ZCLOSEDX00", "OU-WEST", invalidVisibilityJson).andExpect(status().isConflict());

		// OK
		updateVisibility("ZCLOSEDX00", "OU-WEST", visibilityJson).andExpect(status().isOk());
		assertEquals(visibility.getEndDate(),
				visibilityRepository.findVisibilityByCampaignIdAndOuId("ZCLOSEDX00", "OU-WEST").get().getEndDate());
	}

	private ResultActions updateVisibility(String campaignId, String OuId, String visibility) throws Exception {
		return mockMvc.perform(
				put(updateVisibilityUrl(campaignId, OuId))
						.with(authentication(MANAGER))
						.content(visibility)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
	}

	private static String updateVisibilityFormat = "/api/campaign/%s/organizational-unit/%s/visibility";

	private String updateVisibilityUrl(String campaignId, String OuId) {
		return String.format(updateVisibilityFormat, campaignId, OuId);
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private VisibilityContextDto generateVisibilityContextDto(String OuId, String campaignId) {
		Visibility vis = visibilityRepository.findVisibilityByCampaignIdAndOuId(campaignId, OuId).get();
		VisibilityContextDto vcd = new VisibilityContextDto();
		vcd.setOrganizationalUnit(vis.getOrganizationUnit().getId());
		vcd.setCollectionEndDate(vis.getCollectionEndDate());
		vcd.setCollectionStartDate(vis.getCollectionStartDate());
		vcd.setEndDate(vis.getEndDate());
		vcd.setIdentificationPhaseStartDate(vis.getIdentificationPhaseStartDate());
		vcd.setInterviewerStartDate(vis.getInterviewerStartDate());
		vcd.setManagementStartDate(vis.getManagementStartDate());
		return vcd;
	}

	private UserDto generateValidUser() {
		OrganizationUnitDto ou = organizationUnitRepository.findDtoByIdIgnoreCase("OU-SOUTH").get();
		UserDto user = new UserDto("XYZ", "Bob", "Lennon", ou, null);
		return user;
	}

	private void addUnattributedSU(String suId) throws JsonProcessingException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "abc", "a");
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId(suId);
		su.setCampaign("SIMPSONS2020X00");
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
		with()
				.auth().oauth2(accessToken)
				.contentType(ContentType.JSON)
				.body(new ObjectMapper().writeValueAsString(List.of(su)))
				.post("api/survey-units");
	}

}
