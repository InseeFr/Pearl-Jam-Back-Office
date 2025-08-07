package fr.insee.pearljam.api.noAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.utils.ScriptConstants;
import fr.insee.pearljam.config.FixedDateServiceConfiguration;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Test class for no Authentication */
@ActiveProfiles(profiles = {"noauth", "test"})
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(FixedDateServiceConfiguration.class)
class TestNoAuth {

	private final MessageRepository messageRepository;
	private final ClosingCauseRepository closingCauseRepository;
	private final MockMvc mockMvc;

	@Test
	@Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
	void testPutClosingCauseNoPreviousClosingCause() throws Exception {
		mockMvc.perform(put("/api/survey-unit/11/closing-cause/NPI")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
		assertEquals(ClosingCauseType.NPI, closingCauses.getFirst().getType());
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
		List<String> recipients = new ArrayList<>();
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
		assertEquals("TEST", messages.getFirst().getText());
	}
}
