package fr.insee.pearljam.features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.campaign.dto.input.VisibilityCampaignCreateDto;
import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitInterviewerLinkDto;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.service.CampaignService;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
import fr.insee.pearljam.api.surveyunit.dto.identification.RawIdentificationDto;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.JsonTestHelper;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommentDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.identification.IdentificationDB;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class IdentificationSteps {

	@Autowired
	MockMvc mockMvc;

	private final SurveyUnitRepository surveyUnitRepository;
	private final OrganizationUnitRepository organizationUnitRepository;
	private final InterviewerRepository interviewerRepository;
	private final CampaignService campaignService;
	ObjectMapper objectMapper = new ObjectMapper();

	private Authentication securityRole;
	private IdentificationConfiguration identificationConfiguration;
	private MvcResult createdCampaign;
	private SurveyUnit surveyUnit;
	private String surveyUnitId;
	private String campaignId;
	private ResultActions result;

	@Given("a user with {string} role")
	public void a_user_with_following_role(String role) {
		securityRole = switch (role.toLowerCase()) {
			case "admin" -> AuthenticatedUserTestHelper.AUTH_ADMIN;
			case "interviewer" -> AuthenticatedUserTestHelper.AUTH_INTERVIEWER;
			default -> throw new IllegalArgumentException("Unknown role: " + role);
		};
	}

	@When("the user create a campaign with identificationConfiguration equals to {string}")
	public void the_user_create_a_campaign_with_identification_configuration_to(String inputIdentificationConfiguration) throws Exception {
		createACampaignWithAuthenticationAndIdentificationConfiguration(securityRole,
				inputIdentificationConfiguration);

		createdCampaign =
				mockMvc.perform(get(String.join("/", Constants.API_CAMPAIGN, campaignId)).with(authentication(securityRole)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@Then("the created campaign should have the identification configuration {string}")
	public void the_created_campaign_should_have_the_identification_configuration(String expectedIdentificationType) throws IOException {
		String contentResult = createdCampaign.getResponse().getContentAsString();

		CampaignResponseDto campaignDto = objectMapper.readValue(contentResult, CampaignResponseDto.class);

		assertThat(campaignDto.identificationConfiguration()).isEqualTo(IdentificationConfiguration.fromName(expectedIdentificationType));
	}


	@Given("a survey-unit is in a campaign with identification configuration equals to {string}")
	public void a_survey_unit_is_in_a_campaign_with_identification_configuration_equals_to(String inputIdentification) throws Exception {
		createACampaignWithAuthenticationAndIdentificationConfiguration(AuthenticatedUserTestHelper.AUTH_ADMIN,
				inputIdentification);
	}

	@And("this survey-unit is affected to this interviewer")
	public void this_survey_unit_is_affected_to_this_interviewer() throws Exception {
		surveyUnitId = "SURVEYUNIT_" + System.currentTimeMillis();

		Address addressDB = new InseeAddress("l1", "l2", "l3", "l4", "l5", "l6", "l7", true,
				"building", "floor", "door", "staircase", true);
		Campaign campaignDB = campaignService.findById(campaignId).orElseThrow();
		Interviewer interviewerDB = interviewerRepository.findById("INTW1").orElseThrow();
		OrganizationUnit ouDB = organizationUnitRepository.findById("OU-NORTH").orElseThrow();
		Set<Person> personsDB = Set.of(new Person(Title.MISTER, "Bob", "Marley", "bob.marley@insee.fr", true, true,
				537535032000L, surveyUnit));
		Identification identificationDB = new Identification(null, IdentificationType.HOUSEF2F, null, null, null, null
				, null, null, null, null, null, null);
		surveyUnit = new SurveyUnit(surveyUnitId, false, false, addressDB, null, campaignDB, interviewerDB, ouDB, personsDB);

		surveyUnit.setIdentification(IdentificationDB.fromModel(surveyUnit, identificationDB, identificationConfiguration));
		surveyUnit.getStates().add(new State(System.currentTimeMillis(), surveyUnit, StateType.VIN));
		surveyUnit = surveyUnitRepository.save(surveyUnit);

		List<SurveyUnitInterviewerLinkDto> link = List.of(new SurveyUnitInterviewerLinkDto(surveyUnitId, "INTW1"));

		mockMvc.perform(post(Constants.API_SURVEYUNITS_INTERVIEWERS).with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN)).content(JsonTestHelper.toJson(link)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());


	}


	@When("the interviewer update the survey-unit with identification value")
	public void the_interviewer_update_the_survey_unit_with_identification_value() throws Exception {

		SurveyUnitUpdateDto editedSurveyUnit = updateIdentification(
				new RawIdentificationDto(IdentificationQuestionValue.UNIDENTIFIED, AccessQuestionValue.ACC,
						SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY,
						OccupantQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.SAME_ADDRESS,
						InterviewerCanProcessQuestionValue.YES,
						NumberOfRespondentsQuestionValue.ONE, PresentInPreviousHomeQuestionValue.AT_LEAST_ONE,
						HouseholdCompositionQuestionValue.SAME_COMPO));

		result =
				mockMvc.perform(put(String.join("/", "/api/survey-unit", surveyUnitId)).with(authentication(securityRole)).contentType(MediaType.APPLICATION_JSON).content(JsonTestHelper.toJson(editedSurveyUnit)).accept(MediaType.APPLICATION_JSON));

	}

	@Then("the survey-unit is updated and its identification equals:")
	public void the_survey_unit_is_updated_and_its_identification_equals(String expectedValue) throws Exception {
		result.andExpect(status().isOk());
		String content = result.andReturn().getResponse().getContentAsString();

		JsonNode expectedJson = objectMapper.readTree(expectedValue);
		JsonNode actualResponse = objectMapper.readTree(content);
		JsonNode actualIdentification = actualResponse.get("identification");
		assertThat(actualIdentification).isEqualTo(expectedJson);
	}


	private SurveyUnitUpdateDto updateIdentification(RawIdentificationDto newIdentification) {
		return new SurveyUnitUpdateDto(
				surveyUnit.getId(),
				surveyUnit.getPersons().stream().map(PersonDto::new).toList(),
				new AddressDto(surveyUnit.getAddress()),
				surveyUnit.getMove(),
				CommentDto.fromModel(surveyUnit.getComments().stream().map(CommentDB::toModel).collect(Collectors.toSet())),
				surveyUnit.getStates().stream().map(StateDto::new).toList(),
				surveyUnit.getContactAttempts().stream().map(ContactAttemptDto::new).toList(),
				null,
				newIdentification, // New identification
				List.of()
		);
	}

	private void createACampaignWithAuthenticationAndIdentificationConfiguration(Authentication authentication,
																				 String inputIdentificationConfiguration) throws Exception {
		identificationConfiguration =
				IdentificationConfiguration.fromName(inputIdentificationConfiguration);
		campaignId = "CAMPAIGN_" + System.currentTimeMillis();
		CampaignCreateDto inputCampaign = new CampaignCreateDto(campaignId, "campaign_label",
				List.of(new VisibilityCampaignCreateDto(1L
						, 2L, 3L, 4L, 5L, 6L, "OU-NORTH", false, "mail", "tel")), List.of(), List.of(new ReferentDto(
				"Bob",
				"Marley"
				, "0123456789", "PRIMARY")), "campaign@e.mail", identificationConfiguration,
				ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F, false);
		mockMvc.perform(post(Constants.API_CAMPAIGN).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(JsonTestHelper.toJson(inputCampaign)).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}
}
