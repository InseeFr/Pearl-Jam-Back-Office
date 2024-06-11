package fr.insee.pearljam.api.noAuth;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import lombok.RequiredArgsConstructor;

/* Test class for no Authentication */
@ActiveProfiles("test")
@Disabled("Refactor : migrate to mockMvc before enabling again")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestNoAuth {

	private final MessageRepository messageRepository;
	private final ClosingCauseRepository closingCauseRepository;
	private final MockMvc mockMvc;

	@Before
	public void clearDataSet() throws Exception {
		mockMvc.perform(delete("/api/delete-dataset").accept(MediaType.APPLICATION_JSON)
				.with(authentication(AuthenticatedUserTestHelper.AUTH_MANAGER)));
	}

	/**
	 * This method set up the dataBase content
	 * 
	 * @throws Exception
	 * 
	 */
	@BeforeEach
	public void initDataSetIfNotPresent() throws Exception {
		mockMvc.perform(post("/api/create-dataset").accept(MediaType.APPLICATION_JSON)
				.with(authentication(AuthenticatedUserTestHelper.AUTH_MANAGER)));
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

	/* CampaignController */

	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws ParseException
	 */

	@Test
	@Order(1)
	void testGetCampaign() {

		given().when().get("api/campaigns").then().statusCode(200).and()
				.assertThat().body("id", hasItem("SIMPSONS2020X00")).and()
				.assertThat().body("label", hasItem("Survey on the Simpsons tv show 2020")).and()
				.assertThat().body("allocated", hasItem(4)).and()
				.assertThat().body("toAffect", hasItem(0)).and()
				.assertThat().body("toFollowUp", hasItem(0)).and()
				.assertThat().body("toReview", hasItem(0)).and()
				.assertThat().body("finalized", hasItem(0)).and()
				.assertThat().body("toProcessInterviewer", hasItem(0)).and()
				.assertThat().body("preference", hasItem(true));

		// Testing dates
		assertTrue(testingDates("managementStartDate", get("api/campaigns").path("managementStartDate[0]")));
		assertTrue(testingDates("interviewerStartDate", get("api/campaigns").path("interviewerStartDate[0]")));
		assertTrue(testingDates("identificationPhaseStartDate",
				get("api/campaigns").path("identificationPhaseStartDate[0]")));
		assertTrue(testingDates("collectionStartDate", get("api/campaigns").path("collectionStartDate[0]")));
		assertTrue(testingDates("collectionEndDate", get("api/campaigns").path("collectionEndDate[0]")));
		assertTrue(testingDates("endDate", get("api/campaigns").path("endDate[0]")));

	}

	@Test
	@Order(2)
	void testPutClosingCauseNoPreviousClosingCause() {
		given().when().put("api/survey-unit/11/closing-cause/NPI")
				.then().statusCode(200);

		List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
		Assert.assertEquals(ClosingCauseType.NPI, closingCauses.get(0).getType());

	}

	/**
	 * Test that the POST endpoint
	 * "api/message" return 200
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(3)
	void testPostMessage() throws JsonProcessingException {
		List<String> recipients = new ArrayList<String>();
		recipients.add("SIMPSONS2020X00");
		MessageDto message = new MessageDto("TEST", recipients);
		message.setSender("GUEST");
		given().contentType("application/json").body(new ObjectMapper().writeValueAsString(message)).when()
				.post("api/message").then().statusCode(200);
		List<MessageDto> messages = messageRepository
				.findMessagesDtoByIds(messageRepository.getMessageIdsByInterviewer("INTW1"));
		assertEquals("TEST", messages.get(0).getText());
	}

}
