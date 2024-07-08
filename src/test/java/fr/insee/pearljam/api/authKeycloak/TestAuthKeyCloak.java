package fr.insee.pearljam.api.authKeycloak;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.service.*;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.controller.WsText;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
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
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
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
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import lombok.RequiredArgsConstructor;

/* Test class for Keycloak Authentication */
@ActiveProfiles("auth")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TestAuthKeyCloak {

	private final SurveyUnitService surveyUnitService;
	private final UserService userService;
	private final StateRepository stateRepository;
	private final UserRepository userRepository;
	private final SurveyUnitRepository surveyUnitRepository;
	private final CampaignRepository campaignRepository;
	private final VisibilityRepository visibilityRepository;
	private final MessageRepository messageRepository;
	private final OrganizationUnitRepository organizationUnitRepository;
	private final InterviewerRepository interviewerRepository;
	private final ClosingCauseRepository closingCauseRepository;
	private final ReferentService referentservice;
	private final MessageService messageService;
	private final PreferenceService preferenceService;

	private final MockMvc mockMvc;
	private final RestTemplate restTemplate;

	private MockRestServiceServer mockServer;

	static Authentication LOCAL_USER = AuthenticatedUserTestHelper.AUTH_LOCAL_USER;
	static Authentication INTERVIEWER = AuthenticatedUserTestHelper.AUTH_INTERVIEWER;
	static Authentication ADMIN = AuthenticatedUserTestHelper.AUTH_ADMIN;

	/**
	 * This method set up the dataBase content
	 * 
	 *
	 */
	@BeforeEach
	public void setUp() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	private ResultMatcher expectValidManagementStartDate() {
		return expectTimestampFromCurrentDate("$[0].managementStartDate", -4, ChronoUnit.DAYS);
	}

	private ResultMatcher expectValidInterviewerStartDate() {
		return expectTimestampFromCurrentDate("$[0].interviewerStartDate", -3, ChronoUnit.DAYS);
	}

	private ResultMatcher expectValidIdentificationPhaseStartDate() {
		return expectTimestampFromCurrentDate("$[0].identificationPhaseStartDate", -2, ChronoUnit.DAYS);
	}

	private ResultMatcher expectValidCollectionStartDate() {
		return expectTimestampFromCurrentDate("$[0].collectionStartDate", -1, ChronoUnit.DAYS);
	}

	private ResultMatcher expectValidCollectionEndDate() {
		return expectTimestampFromCurrentDate("$[0].collectionEndDate", 1, ChronoUnit.MONTHS);
	}

	private ResultMatcher expectValidEndDate() {
		return expectTimestampFromCurrentDate("$[0].endDate", 2, ChronoUnit.MONTHS);
	}

	private ResultMatcher expectTimestampFromCurrentDate(String expression, int unitToAdd, ChronoUnit chronoUnit) {
		return mvcResult -> {
			String content = mvcResult.getResponse().getContentAsString();
			long timestamp = JsonPath.read(content, expression);
			LocalDate localDateNow = LocalDate.now();
			Instant instant = Instant.ofEpochMilli(timestamp);
			LocalDate dateToCheck = LocalDate.ofInstant(instant, ZoneId.systemDefault());
			assertEquals(dateToCheck, localDateNow.plus(unitToAdd, chronoUnit));
		};
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
				.with(authentication(LOCAL_USER)))
				.andDo(print())
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
				.with(authentication(LOCAL_USER)))
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
		mockMvc.perform(get(
				"https://localhost:8080/api/campaign/SIMPSONS2020X00/survey-units/not-attributed/contact-outcomes")
				.with(authentication(LOCAL_USER))
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

	private ResultMatcher checkJsonPath(String formattablePath, String nodeAttribute, Object expectedValue) {
		if (expectedValue instanceof Boolean) {
			return jsonPath(String.format(formattablePath, nodeAttribute)).value((Boolean) expectedValue);
		} else if (expectedValue instanceof Long) {
			return jsonPath(String.format(formattablePath, nodeAttribute)).value(((Long) expectedValue).intValue());
		} else {
			return jsonPath(String.format(formattablePath, nodeAttribute)).value(expectedValue);
		}
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
		String campaignJsonPath = "$.[?(@.id == 'SIMPSONS2020X00')].%s";

		mockMvc.perform(get(Constants.API_CAMPAIGNS)
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						checkJsonPath(campaignJsonPath, "label", "Survey on the Simpsons tv show 2020"),
						checkJsonPath(campaignJsonPath, "allocated", 4L),
						checkJsonPath(campaignJsonPath, "toProcessInterviewer", 0L),
						checkJsonPath(campaignJsonPath, "toAffect", 0L),
						checkJsonPath(campaignJsonPath, "toFollowUp", 0L),
						checkJsonPath(campaignJsonPath, "finalized", 0L),
						checkJsonPath(campaignJsonPath, "email", "first.email@test.com"),
						checkJsonPath(campaignJsonPath, "toReview", 3L),
						checkJsonPath(campaignJsonPath, "preference", true),
						checkJsonPath(campaignJsonPath, "communicationRequestConfiguration",
								false),
						checkJsonPath(campaignJsonPath, "identificationConfiguration",
								IdentificationConfiguration.IASCO.name()),
						checkJsonPath(campaignJsonPath, "contactAttemptConfiguration",
								ContactAttemptConfiguration.F2F.name()),
						checkJsonPath(campaignJsonPath, "contactOutcomeConfiguration",
								ContactOutcomeConfiguration.F2F.name()),
						expectValidManagementStartDate(),
						expectValidIdentificationPhaseStartDate(),
						expectValidInterviewerStartDate(),
						expectValidCollectionStartDate(),
						expectValidCollectionEndDate(),
						expectValidEndDate());
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
		String interviewerJsonPath = "$.[?(@.id == 'INTW1')].%s";
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/interviewers")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						checkJsonPath(interviewerJsonPath, "interviewerFirstName", "Margie"),
						checkJsonPath(interviewerJsonPath, "interviewerLastName", "Lucas"),
						checkJsonPath(interviewerJsonPath, "surveyUnitCount", 2L));
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
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X000000/interviewers")
				.with(authentication(LOCAL_USER))
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
		String ouJsonPath = "$.organizationUnits.[?(@.idDem == 'OU-NORTH')].%s";

		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/state-count")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						checkJsonPath(ouJsonPath, "nvmCount", 0L),
						checkJsonPath(ouJsonPath, "nnsCount", 0L),
						checkJsonPath(ouJsonPath, "anvCount", 0L),
						checkJsonPath(ouJsonPath, "vinCount", 1L),
						checkJsonPath(ouJsonPath, "vicCount", 0L),
						checkJsonPath(ouJsonPath, "prcCount", 0L),
						checkJsonPath(ouJsonPath, "aocCount", 0L),
						checkJsonPath(ouJsonPath, "apsCount", 0L),
						checkJsonPath(ouJsonPath, "insCount", 0L),
						checkJsonPath(ouJsonPath, "wftCount", 0L),
						checkJsonPath(ouJsonPath, "wfsCount", 0L),
						checkJsonPath(ouJsonPath, "tbrCount", 4L),
						checkJsonPath(ouJsonPath, "finCount", 0L),
						checkJsonPath(ouJsonPath, "cloCount", 0L),
						checkJsonPath(ouJsonPath, "nvaCount", 0L),
						checkJsonPath(ouJsonPath, "npaCount", 0L),
						checkJsonPath(ouJsonPath, "npiCount", 0L),
						checkJsonPath(ouJsonPath, "rowCount", 0L),
						checkJsonPath(ouJsonPath, "total", 5L));

	}

	@Test
	@Order(7)
	void testPutClosingCauseNoPreviousClosingCause()
			throws Exception {
		mockMvc.perform(put("/api/survey-unit/11/closing-cause/NPI")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
		assertEquals(ClosingCauseType.NPI, closingCauses.get(0).getType());

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
		mockMvc.perform(get("/api/campaign/test/survey-units/state-count")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(8)
	void testPutClosingCausePreviousClosingCause() throws Exception {
		mockMvc.perform(put("/api/survey-unit/11/closing-cause/NPA")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
		assertEquals(ClosingCauseType.NPA, closingCauses.get(0).getType());

	}

	@Test
	@Order(9)
	void testPutCloseSU() throws Exception {
		String ouJsonPath = "$.organizationUnits.[?(@.idDem == 'OU-NORTH')].%s";

		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/state-count")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						checkJsonPath(ouJsonPath, "tbrCount", 4L),
						checkJsonPath(ouJsonPath, "rowCount", 0L));

		mockMvc.perform(put("/api/survey-unit/14/close/ROW")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/state-count")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						checkJsonPath(ouJsonPath, "tbrCount", 3L),
						checkJsonPath(ouJsonPath, "rowCount", 1L));
	}

	/**
	 * Test that the GET endpoint
	 * "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(8)
	void testGetCampaignInterviewerStateCount() throws Exception {
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/interviewer/INTW1/state-count")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						jsonPath("$.nvmCount").value(0L),
						jsonPath("$.nnsCount").value(0L),
						jsonPath("$.anvCount").value(0L),
						jsonPath("$.vinCount").value(1L),
						jsonPath("$.vicCount").value(0L),
						jsonPath("$.prcCount").value(0L),
						jsonPath("$.aocCount").value(0L),
						jsonPath("$.apsCount").value(0L),
						jsonPath("$.insCount").value(0L),
						jsonPath("$.wftCount").value(0L),
						jsonPath("$.wfsCount").value(0L),
						jsonPath("$.tbrCount").value(1L),
						jsonPath("$.finCount").value(0L),
						jsonPath("$.cloCount").value(0L),
						jsonPath("$.nvaCount").value(0L),
						jsonPath("$.npaCount").value(0L),
						jsonPath("$.npiCount").value(0L),
						jsonPath("$.rowCount").value(0L),
						jsonPath("$.total").value(2L));
	}

	/**
	 * Test that the GET endpoint
	 * "api/campaign/{id}/survey-units/interviewer/{id}/state-count"
	 * return 404 when campaign Id is false
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(9)
	void testGetCampaignInterviewerStateCountNotFoundCampaign() throws Exception {
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X000000/survey-units/interviewer/INTW1/state-count")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
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
	@Order(10)
	void testGetCampaignInterviewerStateCountNotFoundIntw() throws Exception {
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/interviewer/test/state-count")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
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
	@Order(11)
	void testGetSurveyUnitDetail() throws Exception {
		String personJsonPath = "$.persons.[?(@.firstName == 'Christine')].%s";

		mockMvc.perform(get("/api/survey-unit/11")
				.with(authentication(INTERVIEWER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						jsonPath("$.id").value("11"),
						jsonPath("$.priority", equalTo(true)),
						checkJsonPath(personJsonPath, "lastName", "Aguilar"),
						checkJsonPath(personJsonPath, "favoriteEmail", true),
						checkJsonPath(personJsonPath, "privileged", false),
						checkJsonPath(personJsonPath, "birthdate", 11111111L),
						checkJsonPath(personJsonPath, "phoneNumbers[0].number", "+33677542802"),
						jsonPath("$.address.l1", equalTo("Ted Farmer")),
						jsonPath("$.address.l2", equalTo("")),
						jsonPath("$.address.l3", equalTo("")),
						jsonPath("$.address.l4", equalTo("1 rue de la gare")),
						jsonPath("$.address.l5", equalTo("")),
						jsonPath("$.address.l6", equalTo("29270 Carhaix")),
						jsonPath("$.address.l7", equalTo("France")),
						jsonPath("$.address.elevator", equalTo(true)),
						jsonPath("$.address.building", equalTo("Bat. C")),
						jsonPath("$.address.floor", equalTo("Etg 4")),
						jsonPath("$.address.door", equalTo("Porte 48")),
						jsonPath("$.address.staircase", equalTo("Escalier B")),
						jsonPath("$.address.cityPriorityDistrict", equalTo(true)),
						jsonPath("$.campaign", equalTo("SIMPSONS2020X00")),
						jsonPath("$.contactOutcome").doesNotHaveJsonPath(),
						jsonPath("$.comments", empty()),
						jsonPath("$.states[0].type", equalTo("VIN")),
						jsonPath("$.contactAttempts", empty()),
						jsonPath("$.identification.identification", equalTo("IDENTIFIED")),
						jsonPath("$.identification.access", equalTo("ACC")),
						jsonPath("$.identification.situation", equalTo("ORDINARY")),
						jsonPath("$.identification.category", equalTo("PRIMARY")),
						jsonPath("$.identification.occupant", equalTo("IDENTIFIED")));
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/"
	 * return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Order(12)
	void testGetAllSurveyUnit() throws Exception {
		mockMvc.perform(get("/api/survey-units")
				.with(authentication(INTERVIEWER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.[?(@.id == '11')]").exists(),
						jsonPath("$.[?(@.id == '11')].campaign").value("SIMPSONS2020X00"),
						jsonPath("$.[?(@.id == '11')].campaignLabel").value("Survey on the Simpsons tv show 2020"));

		mockMvc.perform(get(Constants.API_CAMPAIGNS)
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						expectValidManagementStartDate(),
						expectValidIdentificationPhaseStartDate(),
						expectValidInterviewerStartDate(),
						expectValidCollectionStartDate(),
						expectValidCollectionEndDate(),
						expectValidEndDate());
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}"
	 * return 404 when survey-unit is false
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Order(13)
	void testGetSurveyUnitDetailNotFound() throws Exception {
		mockMvc.perform(get("/api/survey-unit/123456789")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(14)
	void testPutSurveyUnitDetail() throws Exception {
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

		mockMvc.perform(put("/api/survey-unit/20")
				.with(authentication(INTERVIEWER))
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(surveyUnitDetailDto))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.id", equalTo("20")),
						jsonPath("$.persons[0].phoneNumbers[0].number", equalTo("test")),
						jsonPath("$.address.l1", equalTo("test")),
						jsonPath("$.address.l2", equalTo("test")),
						jsonPath("$.address.l3", equalTo("test")),
						jsonPath("$.address.l4", equalTo("test")),
						jsonPath("$.address.l5", equalTo("test")),
						jsonPath("$.address.l6", equalTo("test")),
						jsonPath("$.address.l7", equalTo("test")),
						jsonPath("$.address.building", equalTo("testBuilding")),
						jsonPath("$.address.door", equalTo("testDoor")),
						jsonPath("$.address.floor", equalTo("testFloor")),
						jsonPath("$.address.staircase", equalTo("testStaircase")),
						jsonPath("$.address.elevator", equalTo(true)),
						jsonPath("$.address.cityPriorityDistrict", equalTo(true)),
						jsonPath("$.contactOutcome.type", equalTo(ContactOutcomeType.IMP.toString())),
						jsonPath("$.contactOutcome.date", equalTo(Long.valueOf(1589268626000L))),
						jsonPath("$.contactOutcome.totalNumberOfContactAttempts", is(2)),
						jsonPath("$.comments[1].value", equalTo("test")),
						jsonPath("comments[1].type",
								is(oneOf(CommentType.MANAGEMENT.toString(), CommentType.INTERVIEWER.toString()))),
						jsonPath("contactAttempts[1].status", is(oneOf(Status.NOC.toString(), Status.INA.toString())))

				);

	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state/{state}"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(15)
	void testPutSurveyUnitState() throws Exception {
		mockMvc.perform(put("/api/survey-unit/12/state/WFT")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		assertSame(StateType.WFT, stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc("12").getType());
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 400 with unknown state
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(16)
	void testPutSurveyUnitStateStateFalse() throws Exception {
		mockMvc.perform(put("/api/survey-unit/11/state/test")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}"
	 * return 403 when not allowed to pass to this state
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(17)
	void testPutSurveyUnitStateNoSu() throws Exception {
		mockMvc.perform(put("/api/survey-unit/11/state/AOC")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	/**
	 * Test that the PUT endpoint "api/preferences"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(18)
	void testPutPreferences() throws Exception {
		mockMvc.perform(put("/api/preferences")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of("SIMPSONS2020X00")))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	/**
	 * Test that the PUT endpoint "api/preferences"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(19)
	void testPutPreferencesWrongCampaignId() throws Exception {
		mockMvc.perform(put("/api/preferences")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of("")))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that the GET endpoint
	 * "/campaign/{id}/survey-units/interviewer/{idep}/closing-causes" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(19)
	void testGetCampaignInterviewerClosingCauseCount() throws Exception {
		// use a beforeEach method to run each test with a cleaned database

		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/interviewer/INTW1/closing-causes")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						jsonPath("$.npaCount").value("1"),
						jsonPath("$.npiCount").value("0"),
						jsonPath("$.rowCount").value("0"),
						jsonPath("$.npxCount").value("0"),
						jsonPath("$.total").value("2"));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(20)
	void testGetNbSuAbandoned() throws Exception {
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/abandoned")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						jsonPath("$.count").value("0"));
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/contact-outcomes[?date={date}]" return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(20)
	void testGetContactOutcomeCountByCampaign() throws Exception {
		String ouJsonPath = "$.organizationUnits.[?(@.idDem == 'OU-NORTH')].%s";

		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/contact-outcomes")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						checkJsonPath(ouJsonPath, "labelDem", "North region organizational unit"),
						checkJsonPath(ouJsonPath, "inaCount", 0L),
						checkJsonPath(ouJsonPath, "refCount", 0L),
						checkJsonPath(ouJsonPath, "impCount", 0L),
						checkJsonPath(ouJsonPath, "ucdCount", 0L),
						checkJsonPath(ouJsonPath, "utrCount", 0L),
						checkJsonPath(ouJsonPath, "alaCount", 0L),
						checkJsonPath(ouJsonPath, "dcdCount", 0L),
						checkJsonPath(ouJsonPath, "nuhCount", 0L),
						checkJsonPath(ouJsonPath, "dukCount", 1L),
						checkJsonPath(ouJsonPath, "duuCount", 0L),
						checkJsonPath(ouJsonPath, "noaCount", 0L));
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/survey-units/contact-outcomes[?date={date}]" return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(20)
	void testGetContactOutcomeCountAllCampaign() throws Exception {
		String ouJsonPath = "$.[?(@.campaign.id == 'SIMPSONS2020X00')].%s";

		mockMvc.perform(get("/api/campaigns/survey-units/contact-outcomes")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						checkJsonPath(ouJsonPath, "campaign.label", "Survey on the Simpsons tv show 2020"),
						checkJsonPath(ouJsonPath, "inaCount", 0L),
						checkJsonPath(ouJsonPath, "refCount", 0L),
						checkJsonPath(ouJsonPath, "impCount", 0L),
						checkJsonPath(ouJsonPath, "ucdCount", 0L),
						checkJsonPath(ouJsonPath, "utrCount", 0L),
						checkJsonPath(ouJsonPath, "alaCount", 0L),
						checkJsonPath(ouJsonPath, "dcdCount", 0L),
						checkJsonPath(ouJsonPath, "nuhCount", 0L),
						checkJsonPath(ouJsonPath, "nuhCount", 0L),
						checkJsonPath(ouJsonPath, "dukCount", 1L),
						checkJsonPath(ouJsonPath, "duuCount", 0L),
						checkJsonPath(ouJsonPath, "noaCount", 0L));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/abandoned"
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(21)
	void testGetNbSuAbandonedNotFound() throws Exception {
		mockMvc.perform(get("/api/campaign/test/survey-units/abandoned")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/not-attributed"
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(22)
	void testGetNbSuNotAttributed() throws Exception {
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/not-attributed")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						jsonPath("$.count").value("0"));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/survey-units/not-attributed"
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Order(23)
	void testGetNbSuNotAttributedNotFound() throws Exception {
		mockMvc.perform(get("/api/campaign/test/survey-units/not-attributed")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying all dates
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(29)
	void testPutVisibilityModifyAllDates() throws Exception {
		String campaignInput = """
				{
					"managementStartDate": 1575937000000,
					"interviewerStartDate": 1576801000000,
					"identificationPhaseStartDate": 1577233000000,
					"collectionStartDate": 1577837800000,
					"collectionEndDate": 1640996200000,
					"endDate": 1641514600000
				}
				""";

		mockMvc.perform(put("/api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(campaignInput))
				.andExpect(status().isOk());

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
	@Disabled("Need Clock injection refactor")
	@Order(30)
	void testPutVisibilityModifyCollectionStartDate()
			throws Exception {

		long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		String jsonContent = String.format("{\"collectionStartDate\": %d}", now);

		mockMvc.perform(put("/api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent))
				.andExpectAll(
						status().isOk(),
						expectValidCollectionStartDate());

		Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("SIMPSONS2020X00",
				"OU-NORTH");
		assertEquals(true, visi.isPresent());
		assertEquals(now, visi.get().getCollectionStartDate());

	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 200 when modifying end date
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("Need Clock injection refactor")
	@Order(31)
	void testPutVisibilityModifyCollectionEndDate()
			throws Exception {
		long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		String jsonContent = String.format("{\"collectionEndDate\": %d}", now);

		mockMvc.perform(put("/api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent))
				.andExpect(status().isOk());
		Optional<Visibility> visi = visibilityRepository.findVisibilityByCampaignIdAndOuId("SIMPSONS2020X00",
				"OU-NORTH");
		assertEquals(true, visi.isPresent());
		assertEquals(now, visi.get().getCollectionEndDate());
	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 400 when empty body
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(32)
	void testPutVisibilityEmptyBody() throws Exception {
		mockMvc.perform(put("/api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Test that the PUT endpoint
	 * "api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility"
	 * return 400 when bad format
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(33)
	void testPutVisibilityBadFormat() throws Exception {
		String requestBody = """
				{
					"managementStartDate": 1640996200000,
					"interviewerStartDate": "10/10/2020",
					"identificationPhaseStartDate": 1641514600000,
					"collectionStartDate": 1577233000000,
					"collectionEndDate": 1576801000000,
					"endDate": 1575937000000
				}""";

		mockMvc.perform(put("/api/campaign/SIMPSONS2020X00/organizational-unit/OU-NORTH/visibility")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Test that the POST endpoint "api/message" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(34)
	void testPostMessage() throws Exception {
		List<String> recipients = List.of("SIMPSONS2020X00");
		MessageDto message = new MessageDto("TEST", recipients);
		message.setSender("abc");

		mockMvc.perform(post("/api/message")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(message)))
				.andExpect(status().isOk());

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
	void testPostMessageBadFormat() throws Exception {
		mockMvc.perform(post("/api/message")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(null)))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test that the GET endpoint
	 * "api/messages/{id}" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(36)
	void testGetMessage() throws Exception {
		mockMvc.perform(get("/api/messages/INTW1")
				.with(authentication(INTERVIEWER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$[?(@.text == 'TEST')]").exists());

	}

	/**
	 * Test that the GET endpoint
	 * "api/messages/{id}" return empty body with a wrong id
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(37)
	void testGetMessageWrongId() throws Exception {
		mockMvc.perform(get("/api/messages/123456789")
				.with(authentication(INTERVIEWER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.length()").value(0));
	}

	/**
	 * Test that the put endpoint "api/message/{id}/interviewer/{idep}/read"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(38)
	void testPutMessageAsRead() throws Exception {
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		mockMvc.perform(put("/api/message/" + messageId + "/interviewer/INTW1/read")
				.with(authentication(INTERVIEWER))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	/**
	 * Test that the put endpoint "api/message/{id}/interviewer/{idep}/delete"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(39)
	void testPutMessageAsDelete() throws Exception {
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);

		String url = String.format("/api/message/%d/interviewer/INTW1/delete", messageId);

		mockMvc.perform(put(url)
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

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
	void testPutMessageAsReadWrongId() throws Exception {
		Long messageId = messageRepository.getMessageIdsByInterviewer("INTW1").get(0);
		String url = String.format("/api/message/%d/interviewer/Test/read", messageId);

		mockMvc.perform(put(url)
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	/**
	 * Test that the GET endpoint
	 * "/message-history" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(41)
	void testGetMessageHistory() throws Exception {
		mockMvc.perform(get("/api/message-history")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk(),
						jsonPath("$[?(@.text == 'TEST')]").exists());

	}

	/**
	 * Test that the POST endpoint
	 * "/verify-name" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(42)
	void testPostVerifyName() throws Exception {
		WsText message = new WsText("simps");

		mockMvc.perform(post("/api/verify-name")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(message)))
				.andExpectAll(status().isOk(),
						jsonPath("$[?(@.id == 'SIMPSONS2020X00')]").exists());

	}

	/**
	 * Test that the POST endpoint "api/message" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(43)
	void testPostMessageSysteme() throws Exception {
		MessageDto message = new MessageDto("Synchronisation", List.of("SIMPSONS2020X00"));
		mockMvc.perform(post("/api/message")
				.with(authentication(INTERVIEWER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(message)))
				.andExpectAll(status().isOk());
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
	@Order(44)
	void testGetInterviewer() throws Exception {
		String interviewerJsonPath = "$.[?(@.id == 'INTW1')].%s";

		mockMvc.perform(get("/api/interviewers")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.[?(@.id == 'INTW1')]").exists(),
						checkJsonPath(interviewerJsonPath, "interviewerFirstName", "Margie"),
						checkJsonPath(interviewerJsonPath, "interviewerLastName", "Lucas"));
	}

	/**
	 * Test that the Get endpoint
	 * "/interviewer/{id}/campaigns" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Disabled("Need Clock injection refactor")
	@Order(45)
	void testGetInterviewerRelatedCampaigns() throws Exception {
		mockMvc.perform(get("/api/interviewer/INTW1/campaigns")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.[?(@.id == 'SIMPSONS2020X00')").exists(),
						jsonPath("$.[?(@.id == 'SIMPSONS2020X00' ).label").value("Survey on the Simpsons tv show 2020"),
						expectValidManagementStartDate(),
						expectValidEndDate());
	}

	/**
	 * Test that the Get endpoint
	 * "/interviewer/{id}/campaigns" return 404
	 * when interviewer not exist
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(46)
	void testGetInterviewerNotExistForCampaign() throws Exception {
		mockMvc.perform(get("/api/interviewer/INTW123/campaigns")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that the Get endpoint
	 * "/survey-units/closable" return 200
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(47)
	void testGetSUClosable() throws Exception {

		String expectedBody = """
				{"surveyUnitOK" :
					[{ "id" : "23",
					  "stateData" : {"state" : "EXTRACTED", "date" : null, "currentPage" : null }
					},
					{ "id" : "20","stateData":{"state": null, "date": null, "currentPage": null }
					}],
				"surveyUnitNOK" : [	{"id":"21"}] }
				""";

		mockServer.expect(ExpectedCount.once(),
				requestTo(containsString(Constants.API_QUEEN_SURVEYUNITS_STATEDATA)))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(expectedBody));

		Optional<Visibility> visiOpt = visibilityRepository.findVisibilityByCampaignIdAndOuId("VQS2021X00", "OU-NORTH");
		if (visiOpt.isEmpty()) {
			fail("No visibility found for VQS2021X00  and OU-NORTH");
		}

		Visibility visi = visiOpt.get();
		Long collectionEndDate = visi.getCollectionEndDate();
		Long endDate = visi.getEndDate();

		visi.setCollectionEndDate(System.currentTimeMillis() - 86400000);
		visi.setEndDate(System.currentTimeMillis() + 86400000);
		visibilityRepository.save(visi);

		mockMvc
				.perform(get("/api/survey-units/closable")
						.with(authentication(LOCAL_USER))
						.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.[?(@.id == '21')]").exists(),
						jsonPath("$.[?(@.id == '23')]").doesNotExist(), // 23 has stateData => not closable
						jsonPath("$.[?(@.id == '21')].ssech").value(1),
						jsonPath("$.[?(@.id == '21')].questionnaireState").value("UNAVAILABLE"),
						jsonPath("$.[?(@.id == '21')].identificationState").value("FINISHED"),
						jsonPath("$.[?(@.id == '20')].identificationState").value("MISSING"),
						jsonPath("$.[?(@.id == '20')].identificationState").value("MISSING"));

		visi.setCollectionEndDate(collectionEndDate);
		visi.setEndDate(endDate);
		visibilityRepository.save(visi);
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]"
	 * return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(48)
	void testGetContactOutcomeCountByCampaignAndInterviewer()
			throws Exception {
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/survey-units/interviewer/INTW1/contact-outcomes")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.inaCount").value(0L),
						jsonPath("$.refCount").value(0L),
						jsonPath("$.impCount").value(0L),
						jsonPath("$.ucdCount").value(0L),
						jsonPath("$.utrCount").value(0L),
						jsonPath("$.alaCount").value(0L),
						jsonPath("$.dcdCount").value(0L),
						jsonPath("$.nuhCount").value(0L),
						jsonPath("$.dukCount").value(0L),
						jsonPath("$.duuCount").value(0L),
						jsonPath("$.noaCount").value(0L));
	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]"
	 * return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(49)
	void testGetContactOutcomeCountByCampaignNotExistAndInterviewer()
			throws Exception {
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X000000/survey-units/interviewer/INTW1/contact-outcomes")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isNotFound());

	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/interviewer/{id}/contact-outcomes[?date={date}]"
	 * return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(50)
	void testGetContactOutcomeCountByCampaignAndInterviewerNotExist()
			throws Exception {
		mockMvc.perform(get("/api/campaign/SIMPSONS2020X000000/survey-units/interviewer/INTW123/contact-outcomes")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isNotFound());

	}

	/**
	 * Test that the Get endpoint
	 * "/campaign/{id}/survey-units/contact-outcomes[?date={date}]" return 404
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(52)
	void testGetContactOutcomeCountByCampaignNotExist()
			throws Exception {

		mockMvc.perform(get("/api/campaign/SIMPSONS2020X000000/survey-units/contact-outcomes")
				.with(authentication(LOCAL_USER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isNotFound());
	}

	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(54)
	void testPutCommentOnSu() throws Exception {
		String comment = asJsonString(new CommentDto(CommentType.MANAGEMENT, "Test of comment"));

		mockMvc.perform(put("/api/survey-unit/11/comment")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(comment))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/survey-unit/11")
				.with(authentication(INTERVIEWER))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.comments[0].type").value(equalTo(CommentType.MANAGEMENT.toString())),
						jsonPath("$.comments[0].value").value(equalTo("Test of comment")));

	}

	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/comment" return 404
	 * when id not exist
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(55)
	void testPutCommentSuNotExist() throws Exception {
		String comment = asJsonString(new CommentDto(CommentType.MANAGEMENT, "Test of comment"));

		mockMvc.perform(put("/api/survey-unit/11111111111/comment")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON)
				.content(comment))
				.andExpect(status().isNotFound());

	}

	/**
	 * Test that the Put endpoint
	 * "/survey-unit/{id}/viewed" return 200 and viewed attribut set to true
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(56)
	void testPutSuViewed() throws Exception {
		mockMvc.perform(put("/api/survey-unit/24/viewed")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

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
	@Order(57)
	void testPutSuViewedNotExist() throws Exception {
		mockMvc.perform(put("/api/survey-unit/11111111111/viewed")
				.with(authentication(LOCAL_USER))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	@Order(58)
	void testGetOrganizationUnits() throws Exception {
		mockMvc.perform(get("/api/organization-units")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$[?(@.id == 'OU-NORTH')].label").value("North region organizational unit"),
						jsonPath("$[?(@.id == 'OU-SOUTH')].label").value("South region organizational unit"),
						jsonPath("$[?(@.id == 'OU-NATIONAL')].label").value("National organizational unit"));

	}

	/**
	 * Test that the Post endpoint
	 * "/campaign" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(100)
	void testPostCampaignContext() throws Exception {
		CampaignContextDto campDto = new CampaignContextDto();
		campDto.setCampaign("campId");
		campDto.setCampaignLabel("An other campaign");
		campDto.setReferents(List.of(new ReferentDto("Bob", "Marley", "0123456789", "PRIMARY")));
		campDto.setIdentificationConfiguration(IdentificationConfiguration.IASCO);
		campDto.setContactOutcomeConfiguration(ContactOutcomeConfiguration.F2F);
		campDto.setContactAttemptConfiguration(ContactAttemptConfiguration.F2F);

		VisibilityContextDto visi1 = generateDumbVisibilityContextDto("OU-NORTH");
		VisibilityContextDto visi2 = generateDumbVisibilityContextDto("OU-SOUTH");

		campDto.setVisibilities(List.of(visi1, visi2));

		mockMvc.perform(post(Constants.API_CAMPAIGN)
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campDto)))
				.andExpect(status().isOk());

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
		assertEquals(false, campOpt.get().getCommunicationConfiguration());

	}

	/**
	 * Test that the Post endpoint
	 * "/campaign" returns 400
	 * when an attribute is missing
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(101)
	void testPostCampaignContextNoLabel() throws Exception {
		CampaignContextDto campDto = new CampaignContextDto();
		campDto.setCampaign("campId2");
		// Campaign label unset

		VisibilityContextDto visi1 = generateDumbVisibilityContextDto("OU-NORTH");
		VisibilityContextDto visi2 = generateDumbVisibilityContextDto("OU-SOUTH");

		campDto.setVisibilities(List.of(visi1, visi2));

		mockMvc.perform(post(Constants.API_CAMPAIGN)
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campDto)))
				.andExpect(status().isBadRequest());

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
	@Order(102)
	void testPostCampaignContextMissingOU() throws Exception {

		CampaignContextDto campDto = new CampaignContextDto();
		campDto.setCampaign("campId3");
		// Campaign label unset

		VisibilityContextDto visi1 = generateDumbVisibilityContextDto("OU-NORTH");
		VisibilityContextDto visi2 = generateDumbVisibilityContextDto("AN-OU-THAT-DOESNT-EXIST");
		campDto.setVisibilities(List.of(visi1, visi2));

		mockMvc.perform(post(Constants.API_CAMPAIGN)
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campDto)))
				.andExpect(status().isBadRequest());

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
	@Order(103)
	void testPostOrganizationUnitContext() throws Exception {
		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto("OU-NORTH2", "North region OU 2",
				OrganizationUnitType.LOCAL, Collections.emptyList());
		OrganizationUnitContextDto ou2 = new OrganizationUnitContextDto("OU-NATIONAL2", "National OU 2",
				OrganizationUnitType.LOCAL, Collections.emptyList());
		ou2.setOrganisationUnitRef(List.of("OU-NORTH2"));

		UserContextDto user1 = new UserContextDto("JBOULANGER1", "Jacques", "Boulanger", null, null);
		UserContextDto user2 = new UserContextDto("CBERLIN1", "Chloé", "Berlin", null, null);
		UserContextDto user3 = new UserContextDto("TFABRES1", "Thierry", "Fabres", null, null);
		UserContextDto user4 = new UserContextDto("FDUPONT1", "Fabrice", "Dupont", null, null);

		ou1.setUsers(List.of(user1, user2));
		ou2.setUsers(List.of(user3, user4));

		mockMvc.perform(post("/api/organization-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(ou1, ou2))))
				.andExpect(status().isOk());

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
	@Order(104)
	void testPostOrganizationUnitContextDuplicateUser()
			throws Exception {

		UserContextDto user1 = new UserContextDto("JBOULANGER2", "Jacques", "Boulanger", null, null);
		UserContextDto user2 = new UserContextDto("CBERLIN2", "Chloé", "Berlin", null, null);
		UserContextDto user3 = new UserContextDto("TFABRES2", "Thierry", "Fabres", null, null);

		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto("OU-NORTH3", "North region OU 3",
				OrganizationUnitType.LOCAL, List.of(user1, user2));
		OrganizationUnitContextDto ou2 = new OrganizationUnitContextDto("OU-NATIONAL3", "National OU 3",
				OrganizationUnitType.LOCAL, List.of(user2, user3));

		mockMvc.perform(post("/api/organization-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(ou1, ou2))))
				.andExpect(status().isBadRequest());

		// No OU should have been added
		Optional<OrganizationUnit> ou1Opt = organizationUnitRepository.findById("OU-NORTH3");
		Optional<OrganizationUnit> ou2Opt = organizationUnitRepository.findById("OU-NATIONAL3");
		assertTrue(ou1Opt.isEmpty());
		assertTrue(ou2Opt.isEmpty());

	}

	/**
	 * Test that the Post endpoint
	 * "/organization-unit/context" returns 400
	 * when a child organization unit does not exist
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(105)
	void testPostOrganizationUnitContextNoOU() throws Exception {
		UserContextDto user1 = new UserContextDto("JBOULANGER2", "Jacques", "Boulanger", null, null);
		UserContextDto user2 = new UserContextDto("CBERLIN2", "Chloé", "Berlin", null, null);
		UserContextDto user3 = new UserContextDto("TFABRES2", "Thierry", "Fabres", null, null);

		OrganizationUnitContextDto ou1 = new OrganizationUnitContextDto("OU-NORTH3", "North region OU 3",
				OrganizationUnitType.LOCAL, List.of(user1));
		ou1.setOrganisationUnitRef(List.of("AN-OU-THAT-DOESNT-EXIST"));

		OrganizationUnitContextDto ou2 = new OrganizationUnitContextDto("OU-NATIONAL3", "National OU 3",
				OrganizationUnitType.LOCAL, List.of(user2, user3));

		mockMvc.perform(post("/api/organization-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(ou1, ou2))))
				.andExpect(status().isBadRequest());

		// No OU should have been added
		Optional<OrganizationUnit> ou1Opt = organizationUnitRepository.findById("OU-NORTH3");
		Optional<OrganizationUnit> ou2Opt = organizationUnitRepository.findById("OU-NATIONAL3");
		assertTrue(ou1Opt.isEmpty());
		assertTrue(ou2Opt.isEmpty());

	}

	/**
	 * Test that the Post endpoint
	 * "api/interviewers" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(106)
	void testPostInterviewers() throws Exception {
		InterviewerContextDto interv1 = generateInterviewerAContextDto("INTERV1");
		InterviewerContextDto interv2 = generateInterviewerBContextDto("INTERV2");

		mockMvc.perform(post("/api/interviewers")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(interv1, interv2))))
				.andExpect(status().isOk());

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
	@Order(107)
	void testPostInterviewersMissingEmail() throws Exception {
		InterviewerContextDto interv1 = generateInterviewerAContextDto("INTERV4");
		InterviewerContextDto interv2 = generateInterviewerBContextDto("INTERV5");
		interv2.setEmail(null);

		mockMvc.perform(post("/api/interviewers")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(interv1, interv2))))
				.andExpect(status().isBadRequest());

		// Interviewers should not have been added
		Optional<Interviewer> interv1Opt = interviewerRepository.findById("INTERV4");
		Optional<Interviewer> interv2Opt = interviewerRepository.findById("INTERV5");
		assertTrue(interv1Opt.isEmpty());
		assertTrue(interv2Opt.isEmpty());
	}

	/**
	 * Test that the Post endpoint
	 * "api/interviewers" returns 400
	 * when an iterviewer id is present twice
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(108)
	void testPostInterviewersDuplicateId() throws Exception {
		InterviewerContextDto interv1 = generateInterviewerAContextDto("INTERV3");
		InterviewerContextDto interv2 = generateInterviewerBContextDto("INTERV3");

		mockMvc.perform(post("/api/interviewers")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(interv1, interv2))))
				.andExpect(status().isBadRequest());

		// Interviewers should not have been added
		Optional<Interviewer> interv1Opt = interviewerRepository.findById("INTERV3");
		assertTrue(interv1Opt.isEmpty());

	}

	/**
	 * Test that the Post endpoint
	 * "api/interviewers" returns 200
	 * and provide default title MISTER if no title provided
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(109)
	void testPostInterviewersWithNoTitle() throws Exception {
		InterviewerContextDto intervNoTitle = generateInterviewerAContextDto("INTERV_WITHOUT_TITLE");
		intervNoTitle.setTitle(null);

		mockMvc.perform(post("/api/interviewers")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(intervNoTitle))))
				.andExpect(status().isOk());

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
	@Order(111)
	void testPostSurveyUnits() throws Exception {
		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(generateSurveyUnit("8")))))
				.andExpect(status().isOk());

		assertTrue(surveyUnitRepository.findById("8").isPresent());
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
	@Order(112)
	void testPostSurveyUnitsDuplicateInDB() throws Exception {
		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(generateSurveyUnit("8")))))
				.andExpect(status().isBadRequest());
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
	@Order(113)
	void testPostSurveyUnitsDuplicateInBody() throws Exception {
		SurveyUnitContextDto su = generateSurveyUnit("9");

		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(su, su))))
				.andExpect(status().isBadRequest());
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
	@Order(114)
	void testPostSurveyUnitsOUNotExist() throws Exception {
		SurveyUnitContextDto su = generateSurveyUnit("9");
		// Use an unknown organization unit
		su.setOrganizationUnitId("OU-TEST");

		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(su))))
				.andExpect(status().isBadRequest());
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
	@Order(115)
	void testPostSurveyUnitsCampaignNotExist() throws Exception {
		SurveyUnitContextDto su = generateSurveyUnit("9");
		su.setCampaign("campaignTest");

		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(su))))
				.andExpect(status().isBadRequest());

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
	@Order(117)
	void testPostSurveyUnitsSUNotValid() throws Exception {
		SurveyUnitContextDto su = generateSurveyUnit("");

		// ID null
		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(su))))
				.andExpect(status().isBadRequest());

		// Campaign null
		su.setId("9");
		su.setCampaign("");
		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(su))))
				.andExpect(status().isBadRequest());

		// Persons Null
		su.setPersons(Collections.emptyList());
		su.setCampaign("SIMPSONS2020X00");
		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(su))))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test that the Post endpoint
	 * "/survey-units/interviewers" returns 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(118)
	void testPostAssignements() throws Exception {
		assertDoesNotThrow(() -> addUnattributedSU("101"));
		assertDoesNotThrow(() -> addUnattributedSU("102"));

		assertTrue(surveyUnitRepository.findById("101").isPresent());
		assertTrue(surveyUnitRepository.findById("102").isPresent());

		SurveyUnitInterviewerLinkDto assign1 = new SurveyUnitInterviewerLinkDto("101", "INTW4");
		SurveyUnitInterviewerLinkDto assign2 = new SurveyUnitInterviewerLinkDto("102", "INTW3");

		mockMvc.perform(post(Constants.API_SURVEYUNITS_INTERVIEWERS)
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(assign1, assign2))))
				.andDo(print())
				.andExpect(status().isOk());

		// Survey Units should have been attributed to interviewers
		Optional<SurveyUnit> su1 = surveyUnitRepository.findById("101");
		Optional<SurveyUnit> su2 = surveyUnitRepository.findById("102");

		assertTrue(su1.isPresent());
		assertEquals("INTW4", su1.get().getInterviewer().getId());

		assertTrue(su2.isPresent());
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
	@Order(119)
	void testPostAssignementsNoInterviewerDoesntExist()
			throws Exception {
		assertDoesNotThrow(() -> addUnattributedSU("103"));
		assertDoesNotThrow(() -> addUnattributedSU("104"));

		SurveyUnitInterviewerLinkDto assign1 = new SurveyUnitInterviewerLinkDto("103", "INTW4");
		SurveyUnitInterviewerLinkDto assign2 = new SurveyUnitInterviewerLinkDto("104", "INTWDOESNTEXIST");

		mockMvc.perform(post(Constants.API_SURVEYUNITS_INTERVIEWERS)
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(assign1, assign2))))
				.andDo(print())
				.andExpect(status().isBadRequest());

		// Survey Units should not have been attributed to interviewers
		Optional<SurveyUnit> su1 = surveyUnitRepository.findById("103");
		Optional<SurveyUnit> su2 = surveyUnitRepository.findById("104");

		assertTrue(su1.isPresent());
		assertNull(su1.get().getInterviewer());

		assertTrue(su2.isPresent());
		assertNull(su2.get().getInterviewer());

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
	@Order(120)
	void testPostUsersByOU() throws Exception {
		mockMvc.perform(post("/api/organization-unit/OU-NORTH/users")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(
						new UserContextDto("TEST", "test", "test", null, null),
						new UserContextDto("TEST2", "test2", "test2", null, null)))))
				.andExpect(status().isOk());

		// Ensure users have been added to the organization unit
		Optional<User> user = userRepository.findById("TEST");
		assertTrue(user.isPresent());
		assertEquals("OU-NORTH", user.get().getOrganizationUnit().getId());

		Optional<User> user2 = userRepository.findById("TEST2");
		assertTrue(user2.isPresent());
		assertEquals("OU-NORTH", user2.get().getOrganizationUnit().getId());
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
	@Order(121)
	void testPostUsersByOUThatDoesNotExist() throws Exception {
		mockMvc.perform(post("/api/organization-unit/OU-TEST/users")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(
						new UserContextDto("TEST", "test", "test", null, null),
						new UserContextDto("TEST2", "test2", "test2", null, null)))))
				.andExpect(status().isBadRequest());

		// Verify the user organization unit
		Optional<User> user = userRepository.findById("TEST");
		assertTrue(user.isPresent());
		assertEquals("OU-NORTH", user.get().getOrganizationUnit().getId());

		Optional<User> user2 = userRepository.findById("TEST2");
		assertTrue(user2.isPresent());
		assertEquals("OU-NORTH", user2.get().getOrganizationUnit().getId());
	}

	@Test
	@Order(200)
	void testDeleteSurveyUnit() throws Exception {
		mockMvc.perform(delete("/api/survey-unit/11")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		assertTrue(surveyUnitRepository.findById("11").isEmpty());
	}

	@Test
	@Order(201)
	void testDeleteSurveyUnitNotExist() throws Exception {
		mockMvc.perform(delete("/api/survey-unit/toto")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(202)
	void testDeleteCampaign() throws Exception {
		mockMvc.perform(delete("/api/campaign/XCLOSEDX00")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		assertTrue(campaignRepository.findById("XCLOSEDX00").isEmpty());

		mockMvc.perform(delete("/api/campaign/SIMPSONS2020X00")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());

		mockMvc.perform(delete("/api/campaign/SIMPSONS2020X00?force=false")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());

		mockMvc.perform(delete("/api/campaign/SIMPSONS2020X00?force=true")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		assertTrue(campaignRepository.findById("SIMPSONS2020X00").isEmpty());
	}

	@Test
	@Order(203)
	void testDeleteCampaignNotExist() throws Exception {
		mockMvc.perform(delete("/api/campaign/SIMPSONS2020XTT")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(204)
	void testDeleteUser() throws Exception {
		mockMvc.perform(delete("/api/user/JKL")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		assertTrue(userRepository.findById("JKL").isEmpty());
	}

	@Test
	@Order(205)
	void testDeleteUserNotExist() throws Exception {
		mockMvc.perform(delete("/api/user/USER")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(206)
	void testDeleteOrganizationUnit() throws Exception {
		// Delete all Survey Units before deleting Organization Unit
		surveyUnitRepository.findByOrganizationUnitIdIn(List.of("OU-NORTH"))
				.forEach(su -> surveyUnitRepository.delete(su));

		// Delete all Users before deleting Organization Unit
		userRepository.findAllByOrganizationUnitId("OU-NORTH")
				.forEach(u -> {
					messageService.deleteMessageByUserId(u.getId());
					preferenceService.setPreferences(Collections.emptyList(), u.getId());
					userService.delete(u.getId());
				});

		mockMvc.perform(delete("/api/organization-unit/OU-NORTH")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		assertTrue(organizationUnitRepository.findById("OU-NORTH").isEmpty());
	}

	@Test
	@Order(207)
	void testDeleteOrganizationUnitWithUsersOrSurveyUnits() throws Exception {
		mockMvc.perform(delete("/api/organization-unit/OU-SOUTH")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(208)
	void testDeleteOrganizationUnitNotExist() throws Exception {
		mockMvc.perform(delete("/api/organization-unit/TEST")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(209)
	void testCreateValidUser() throws Exception {
		mockMvc.perform(post("/api/user")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(generateValidUser())))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(210)
	void testCreateAreadyPresentUser() throws Exception {
		mockMvc.perform(post("/api/user")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(generateValidUser())))
				.andExpect(status().isConflict());
	}

	@Test
	@Order(211)
	void testCreateInvalidUser() throws Exception {
		// Null user object
		mockMvc.perform(post("/api/user")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(null)))
				.andDo(print())
				.andExpect(status().isBadRequest());

		// User with null first name
		UserDto user = generateValidUser();
		user.setFirstName(null);
		mockMvc.perform(post("/api/user")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(user)))
				.andExpect(status().isBadRequest());

		// User with null last name
		user = generateValidUser();
		user.setLastName(null);
		mockMvc.perform(post("/api/user")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(user)))
				.andExpect(status().isBadRequest());

		// User with null id
		user = generateValidUser();
		user.setId(null);
		mockMvc.perform(post("/api/user")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(user)))
				.andExpect(status().isBadRequest());

		// User with unknown organization unit
		user = generateValidUser();
		user.getOrganizationUnit().setId("WHERE_IS_CHARLIE");
		mockMvc.perform(post("/api/user")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(user)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(212)
	void testUpdateMissingUser() throws Exception {
		mockMvc.perform(put("/api/user/TEST")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(generateValidUser())))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(213)
	void testUpdateWrongUser() throws Exception {
		mockMvc.perform(put("/api/user/GHI")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(generateValidUser())))
				.andExpect(status().isConflict());
	}

	@Test
	@Order(214)
	void testUpdateUser() throws Exception {
		UserDto user = generateValidUser();
		user.setId("GHI");

		mockMvc.perform(put("/api/user/GHI")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(user)))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.id").value("GHI"),
						jsonPath("$.firstName").value("Bob"),
						jsonPath("$.lastName").value("Lennon"),
						jsonPath("$.organizationUnit.id").value("OU-SOUTH"),
						jsonPath("$.organizationUnit.label").value("South region organizational unit"));
	}

	@Test
	@Order(215)
	void testAssignUser() throws Exception {
		mockMvc.perform(put("/api/user/GHI/organization-unit/OU-SOUTH")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.id").value("GHI"),
						jsonPath("$.organizationUnit.id").value("OU-SOUTH"),
						jsonPath("$.organizationUnit.label").value("South region organizational unit"));
	}

	@Test
	@Order(216)
	void testAssignUserMissingUser() throws Exception {
		mockMvc.perform(put("/api/user/MISSING/organization-unit/OU-SOUTH")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(217)
	void testAssignUserMissingOu() throws Exception {
		mockMvc.perform(put("/api/user/GHI/organization-unit/MISSING")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(218)
	void testOngoing() throws Exception {
		mockMvc.perform(get("/campaigns/ZCLOSEDX00/ongoing")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.ongoing").value(false));

		mockMvc.perform(get("/campaigns/VQS2021X00/ongoing")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.ongoing").value(true));
	}

	@Test
	@Order(219)
	void testOngoingNotFound() throws Exception {
		mockMvc.perform(get("/campaigns/MISSING/ongoing")
				.with(authentication(ADMIN))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(220)
	void testPutCampaign() throws Exception {
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
		mockMvc.perform(put("/api/campaign/MISSING")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campaignContext)))
				.andExpect(status().isNotFound());

		// BAD REQUESTS
		campaignContext.setCampaignLabel(null);
		mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campaignContext)))
				.andExpect(status().isBadRequest());

		campaignContext.setCampaignLabel("  ");
		mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campaignContext)))
				.andExpect(status().isBadRequest());

		campaignContext.setCampaignLabel("");
		mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campaignContext)))
				.andExpect(status().isBadRequest());

		campaignContext.setCampaignLabel("Everyday life and health survey 2021");
		campaignContext.setVisibilities(List.of(emptyVcd));
		mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campaignContext)))
				.andExpect(status().isBadRequest());

		// NOT FOUND VISIBILITY
		campaignContext.setVisibilities(List.of(missingVcd));
		mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campaignContext)))
				.andExpect(status().isNotFound());

		// CONFLICT due to visibilities
		campaignContext.setVisibilities(List.of(invalidVcd));
		mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campaignContext)))
				.andExpect(status().isConflict());

		// 200
		campaignContext.setVisibilities(List.of(wvcd, svcd));
		campaignContext.setEmail("updated.email@test.com");
		campaignContext.setContactAttemptConfiguration(ContactAttemptConfiguration.TEL);
		campaignContext.setContactOutcomeConfiguration(ContactOutcomeConfiguration.TEL);
		campaignContext.setIdentificationConfiguration(IdentificationConfiguration.NOIDENT);

		mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(campaignContext)))
				.andExpect(status().isOk());

		assertEquals(wvcd.getEndDate(),
				visibilityRepository.findVisibilityByCampaignIdAndOuId("ZCLOSEDX00", "OU-WEST").get().getEndDate());
		CampaignDto updatedCampaign = campaignRepository.findDtoById("ZCLOSEDX00");
		assertEquals("updated.email@test.com", updatedCampaign.getEmail());
		assertEquals(ContactAttemptConfiguration.TEL, updatedCampaign.getContactAttemptConfiguration());
		assertEquals(ContactOutcomeConfiguration.TEL, updatedCampaign.getContactOutcomeConfiguration());
		assertEquals(IdentificationConfiguration.NOIDENT, updatedCampaign.getIdentificationConfiguration());
	}

	@Test
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
						.with(authentication(LOCAL_USER))
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

	private SurveyUnitContextDto generateSurveyUnit(String id) {
		SurveyUnitContextDto su = new SurveyUnitContextDto();
		su.setId(id);
		su.setCampaign("SIMPSONS2020X00");
		su.setOrganizationUnitId("OU-NORTH");
		su.setPriority(true);
		AddressDto addr = new AddressDto();
		addr.setL1("Test test");
		addr.setL2("1 rue test");
		addr.setL3("TEST");
		su.setAddress(addr);
		PersonDto p = new PersonDto();
		p.setFirstName("test");
		p.setLastName("test");
		p.setEmail("test@test.com");
		p.setFavoriteEmail(true);
		p.setBirthdate(1564656540L);
		p.setPrivileged(true);
		p.setTitle(Title.MISTER);
		p.setPhoneNumbers(List.of(new PhoneNumberDto(Source.FISCAL, true, "+33666666666")));
		List<PersonDto> lstPerson = List.of(p);
		su.setPersons(lstPerson);
		su.setSampleIdentifiers(new SampleIdentifiersDto(0, "0", 0, 0, 0, 0, 0, 0, 0, "0", "0"));
		return su;
	}

	private InterviewerContextDto generateInterviewerAContextDto(String id) {
		return new InterviewerContextDto(
				id,
				"Pierre",
				"Legrand",
				"pierre.legrand@insee.fr",
				"06 XX XX XX XX",
				Title.MISTER);
	}

	private InterviewerContextDto generateInterviewerBContextDto(String id) {
		return new InterviewerContextDto(
				id,
				"Clara",
				"Legouanec",
				"clara.legouanec@insee.fr",
				"06 XX XX XX XX",
				Title.MISS);
	}

	private VisibilityContextDto generateDumbVisibilityContextDto(String ouId) {
		VisibilityContextDto vcd = new VisibilityContextDto();
		vcd.setOrganizationalUnit(ouId);
		vcd.setCollectionStartDate(1111L);
		vcd.setCollectionEndDate(2222L);
		vcd.setIdentificationPhaseStartDate(3333L);
		vcd.setInterviewerStartDate(4444L);
		vcd.setManagementStartDate(5555L);
		vcd.setEndDate(6666L);
		return vcd;
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
		return new UserDto("XYZ", "Bob", "Lennon", ou, null);
	}

	private void addUnattributedSU(String suId) throws Exception {
		mockMvc.perform(post("/api/survey-units")
				.with(authentication(ADMIN))
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(List.of(generateSurveyUnit(suId)))));
	}

}
