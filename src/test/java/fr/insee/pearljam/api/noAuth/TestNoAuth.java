package fr.insee.pearljam.api.noAuth;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
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

import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.dto.message.MessageDto;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import io.restassured.RestAssured;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

/* Test class for no Authentication */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({ "test" })
@ContextConfiguration(initializers = { TestNoAuth.Initializer.class })
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties= {"fr.insee.pearljam.application.mode = noauth"})
class TestNoAuth {

	@Autowired
	MessageRepository messageRepository;

	@Autowired
	ClosingCauseRepository closingCauseRepository;

	@LocalServerPort
	int port;

	public static ClientAndServer clientAndServer;
	public static MockServerClient mockServerClient;

	public Liquibase liquibase;

	/**
	 * This method set up the port of the PostgreSqlContainer
	 * @throws SQLException
	 * @throws LiquibaseException
	 */
	@BeforeEach
	public void setUp() throws SQLException, LiquibaseException {
		RestAssured.port = port;
		post("api/create-dataset");
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

	/*CampaignController*/
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 404
	 * @throws InterruptedException
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	
	
	@Test
	@Order(1)
	void testGetCampaign() throws InterruptedException, JSONException, ParseException {

		given().when().get("api/campaigns").then().statusCode(200).and()
		.assertThat().body("id", hasItem("SIMPSONS2020X00")).and()
		.assertThat().body("label", hasItem("Survey on the Simpsons tv show 2020")).and()
		.assertThat().body("allocated",hasItem(4)).and()
		.assertThat().body("toAffect",hasItem(0)).and()
		.assertThat().body("toFollowUp",hasItem(0)).and()
		.assertThat().body("toReview",hasItem(0)).and()
		.assertThat().body("finalized",hasItem(0)).and()
		.assertThat().body("toProcessInterviewer",hasItem(0)).and()
		.assertThat().body("preference",hasItem(true));
				
		//Testing dates
		assertTrue(testingDates("managementStartDate", get("api/campaigns").path("managementStartDate[0]")));
		assertTrue(testingDates("interviewerStartDate", get("api/campaigns").path("interviewerStartDate[0]")));
		assertTrue(testingDates("identificationPhaseStartDate", get("api/campaigns").path("identificationPhaseStartDate[0]")));
		assertTrue(testingDates("collectionStartDate", get("api/campaigns").path("collectionStartDate[0]")));
		assertTrue(testingDates("collectionEndDate", get("api/campaigns").path("collectionEndDate[0]")));
		assertTrue(testingDates("endDate", get("api/campaigns").path("endDate[0]")));

	}

	@Test
	@Order(2)
	void testPutClosingCauseNoPreviousClosingCause()
			throws InterruptedException, JsonProcessingException, JSONException {
		given().when().put("api/survey-unit/11/closing-cause/NPI")
				.then().statusCode(200);

		List<ClosingCause> closingCauses = closingCauseRepository.findBySurveyUnitId("11");
		Assert.assertEquals(ClosingCauseType.NPI, closingCauses.get(0).getType());

	}
	
	/**
	 * Test that the POST endpoint
	 * "api/message" return 200
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Order(3)
	void testPostMessage() throws InterruptedException, JsonProcessingException, JSONException {
		List<String> recipients = new ArrayList<String>();
		recipients.add("SIMPSONS2020X00");
		MessageDto message = new MessageDto("TEST", recipients);
		message.setSender("GUEST");
		given().contentType("application/json").body(new ObjectMapper().writeValueAsString(message)).when()
				.post("api/message").then().statusCode(200);
		List<MessageDto> messages = messageRepository.findMessagesDtoByIds(messageRepository.getMessageIdsByInterviewer("INTW1"));
		assertEquals("TEST", messages.get(0).getText());
	}
	
}
