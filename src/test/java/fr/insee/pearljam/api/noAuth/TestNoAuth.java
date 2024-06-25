package fr.insee.pearljam.api.noAuth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import com.jayway.jsonpath.JsonPath;
import fr.insee.pearljam.api.utils.ScriptConstants;
import org.json.JSONException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.ResultMatcher;

/* Test class for no Authentication */
@ActiveProfiles("noauth")
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
	void testGetCampaign() throws Exception {
		mockMvc.perform(get("/api/campaigns")
						.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$[0].id").value("SIMPSONS2020X00"),
						jsonPath("$[0].label").value("Survey on the Simpsons tv show 2020"),
						jsonPath("$[0].allocated").value(4),
						jsonPath("$[0].toAffect").value(0),
						jsonPath("$[0].toFollowUp").value(0),
						jsonPath("$[0].toReview").value(3),
						jsonPath("$[0].finalized").value(0),
						jsonPath("$[0].toProcessInterviewer").value(0),
						jsonPath("$[0].preference").value(true),
						expectValidManagementStartDate(),
						expectValidIdentificationPhaseStartDate(),
						expectValidInterviewerStartDate(),
						expectValidCollectionStartDate(),
						expectValidCollectionEndDate(),
						expectValidEndDate());
	}

	@Test
	@Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
	void testPutClosingCauseNoPreviousClosingCause() throws Exception {
		mockMvc.perform(put("/api/survey-unit/11/closing-cause/NPI")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
		assertEquals(ClosingCauseType.NPI, closingCauses.get(0).getType());
	}

	/**
	 * Test that the POST endpoint
	 * "api/message" return 200
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	@Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
	void testPostMessage() throws Exception {
		List<String> recipients = new ArrayList<String>();
		recipients.add("SIMPSONS2020X00");
		MessageDto message = new MessageDto("TEST", recipients);
		message.setSender("GUEST");
		mockMvc.perform(post("/api/message")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(message)))
				.andExpect(status().isOk());

		List<MessageDto> messages = messageRepository
				.findMessagesDtoByIds(messageRepository.getMessageIdsByInterviewer("INTW1"));
		assertEquals("TEST", messages.get(0).getText());
	}
}
