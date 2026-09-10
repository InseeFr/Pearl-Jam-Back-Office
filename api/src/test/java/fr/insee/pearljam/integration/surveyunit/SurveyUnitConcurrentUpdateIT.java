package fr.insee.pearljam.integration.surveyunit;

import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.ScriptConstants;
import fr.insee.pearljam.config.FixedDateServiceConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Reproduces E-001 : the interviewer device synchronizes every survey unit in parallel
 * ({@code Promise.all} in Pearl-Jam), and every update deletes then recreates the persons of the
 * survey unit. Two updates of the same survey unit therefore try to delete the same person row.
 */
@ActiveProfiles(profiles = {"auth", "test"})
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		// the test profile pins the pool to a single connection, which would serialize the requests
		properties = "spring.datasource.hikari.maximum-pool-size=5")
@Import(FixedDateServiceConfiguration.class)
class SurveyUnitConcurrentUpdateIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Disabled("documents an unfixed bug : concurrent updates of the same survey unit deadlock in database")
	@DisplayName("Should update survey unit when the same survey unit is synchronized twice at once")
	@Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
	void testConcurrentPutOnSameSurveyUnit() throws Exception {
		String updateJson = """
				{
				  "id": "11",
				  "persons": [
				    {
				      "id": 1,
				      "title": "MISTER",
				      "firstName": "Ted",
				      "lastName": "Farmer",
				      "email": "test@test.com",
				      "birthdate": 11111111,
				      "privileged": true,
				      "phoneNumbers": []
				    }
				  ]
				}
				""";

		Callable<Integer> update = () -> mockMvc.perform(put("/api/survey-unit/11")
						.with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER))
						.accept(MediaType.APPLICATION_JSON)
						.content(updateJson)
						.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getStatus();

		ExecutorService pool = Executors.newFixedThreadPool(2);
		try {
			for (int attempt = 0; attempt < 10; attempt++) {
				List<Future<Integer>> results = pool.invokeAll(List.of(update, update));
				for (Future<Integer> result : results) {
					assertThat(result.get()).isEqualTo(HttpStatus.OK.value());
				}
			}
		} finally {
			pool.shutdownNow();
		}
	}
}
