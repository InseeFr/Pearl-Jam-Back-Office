package fr.insee.pearljam.features;

import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.JsonTestHelper;
import fr.insee.pearljam.contracts.campaign.dto.ReferentDto;
import fr.insee.pearljam.contracts.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.contracts.campaign.dto.input.VisibilityCampaignCreateDto;
import fr.insee.pearljam.contracts.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.contracts.surveyunit.dto.identification.RawIdentificationDto;
import fr.insee.pearljam.contracts.surveyunit.dto.person.PersonDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.*;
import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.campaign.port.in.CampaignService;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.Title;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.OrganizationUnitJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.*;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.identification.IdentificationDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.InterviewerJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

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

	private final SurveyUnitJpaRepository surveyUnitRepository;
	private final OrganizationUnitJpaRepository organizationUnitRepository;
	private final InterviewerJpaRepository interviewerRepository;
	private final CampaignService campaignService;
	final JsonMapper jsonMapper = new JsonMapper();

	private Authentication securityRole;
	private IdentificationConfiguration identificationConfiguration;
	private MvcResult createdCampaign;
	private SurveyUnitDB surveyUnit;
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

		CampaignResponseDto campaignDto = jsonMapper.readValue(contentResult, CampaignResponseDto.class);

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

		AddressDB addressDB = new InseeAddressDB("l1", "l2", "l3", "l4", "l5", "l6", "l7", true,
				"building", "floor", "door", "staircase", true);
		CampaignDB campaignDB = campaignService.findById(campaignId).orElseThrow();
		System.out.println(
				interviewerRepository.findAll()
						.stream()
						.map(i -> i.getId())
						.toList()
		);
		InterviewerDB interviewerDB = interviewerRepository.findById("INTW1").orElseThrow();
		System.out.println(
				organizationUnitRepository.findAll()
						.stream()
						.map(o -> o.getId())
						.toList()
		);
		OrganizationUnitDB ouDB = organizationUnitRepository.findById("OU-NORTH").orElseThrow();
		Set<PersonDB> persons = Set.of(new PersonDB(null, Title.MISTER, "Bob", "Marley", "bob.marley@insee.fr", 537535032000L, true, surveyUnit, null, false,
				 null,null));
		Identification identificationDB = new Identification(null, IdentificationType.HOUSEF2F, null, null, null, null
				, null, null, null, null, null, null);
		surveyUnit = new SurveyUnitDB(surveyUnitId, false, false, addressDB, null, campaignDB, interviewerDB, ouDB, persons);

		surveyUnit.setIdentification(IdentificationDB.fromModel(surveyUnit, identificationDB, identificationConfiguration));
		surveyUnit.getStates().add(new StateDB(System.currentTimeMillis(), surveyUnit, StateType.VIN));
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

		JsonNode expectedJson = jsonMapper.readTree(expectedValue);
		JsonNode actualResponse = jsonMapper.readTree(content);
		JsonNode actualIdentification = actualResponse.get("identification");
		assertThat(actualIdentification).isEqualTo(expectedJson);
	}


	private SurveyUnitUpdateDto updateIdentification(RawIdentificationDto newIdentification) {
		AddressDto addressDto = null;
		if (surveyUnit.getAddress() instanceof InseeAddressDB addressDB) {
			addressDto = new AddressDto(addressDB.getL1(), addressDB.getL2(), addressDB.getL3(), addressDB.getL4(),
					addressDB.getL5(), addressDB.getL6(), addressDB.getL7(), addressDB.getElevator(),
					addressDB.getBuilding(), addressDB.getFloor(), addressDB.getDoor(), addressDB.getStaircase(),
					addressDB.getCityPriorityDistrict());
		}
		return new SurveyUnitUpdateDto(
				surveyUnit.getId(),
				surveyUnit.getPersons().stream().map(person -> PersonDB.toModel(person, null)).map(PersonDto::fromModel).toList(),
				addressDto,
				surveyUnit.getMove(),
				CommentDto.fromModel(surveyUnit.getComments().stream().map(CommentDB::toModel).collect(Collectors.toSet())),
				surveyUnit.getStates().stream().map(s -> new StateDto(s.getId(), s.getDate(), s.getType())).toList(),
				surveyUnit.getContactAttempts().stream()
						.map(ca -> new ContactAttemptDto(ca.getDate(), ca.getStatus(), ca.getMedium())).toList(),
				null,
				newIdentification, // New identification
				List.of(),
				null
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
				ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F, false, false);
		mockMvc.perform(post(Constants.API_CAMPAIGN).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(JsonTestHelper.toJson(inputCampaign)).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}
}
