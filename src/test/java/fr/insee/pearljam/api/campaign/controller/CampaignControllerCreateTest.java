package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.campaign.dto.input.visibility.VisibilityCampaignCreateDto;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.controller.CampaignController;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.exception.CampaignAlreadyExistException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.insee.pearljam.api.service.dummy.CampaignFakeService;
import fr.insee.pearljam.api.service.dummy.ReferentFakeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignControllerCreateTest {
    private CampaignFakeService campaignService;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        campaignService = new CampaignFakeService();
        ReferentFakeService referentService = new ReferentFakeService();
        AuthenticationUserFakeService authenticatedUserService = new AuthenticationUserFakeService(AuthenticatedUserTestHelper.AUTH_ADMIN);
        CampaignController campaignController = new CampaignController(campaignService, referentService, authenticatedUserService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(campaignController)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should create campaign")
    void testCreateCampaign01() throws Exception {
        Pair<String, VisibilityCampaignCreateDto> firstVisibility = generateVisibility("OU-NORTH", 1721683250L,
                1721683251L,
                1721683252L,
                1721683253L,
                1721683254L,
                1721683255L);
        Pair<String, VisibilityCampaignCreateDto> secondVisibility = generateVisibility("OU-SOUTH", 1721683260L,
                1721683261L,
                1721683262L,
                1721683263L,
                1721683264L,
                1721683265L);
        Pair<String, ReferentDto> firstReferent = generateReferent("Bob", "Marley", "0123456789", "PRIMARY");
        Pair<String, ReferentDto> secondReferent = generateReferent("Dupont", "Jean", "1234567890", "PRIMARY");
        Pair<String, CampaignCreateDto> campaign = generateCampaign("campId", "An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(firstVisibility, secondVisibility),
                List.of(firstReferent, secondReferent));

        mockMvc.perform(post(Constants.API_CAMPAIGN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(campaign.getValue0()))
                .andExpect(status().isOk());

        CampaignCreateDto campaignCreated = campaignService.getCampaignCreated();
        assertThat(campaignCreated).isEqualTo(campaign.getValue1());
    }

    @Test
    @DisplayName("Should return bad request when campaign already exists")
    void testCreateCampaign02() throws Exception {
        campaignService.setShouldThrowCampaignAlreadyExistException(true);
        Pair<String, VisibilityCampaignCreateDto> visibility = generateVisibility("OU-SOUTH", 1721683260L,
                1721683261L,
                1721683262L,
                1721683263L,
                1721683264L,
                1721683265L);
        Pair<String, CampaignCreateDto> campaign = generateCampaign("campId", "An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(visibility),
                List.of());

        mockMvc.perform(post(Constants.API_CAMPAIGN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(campaign.getValue0()))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, Constants.API_CAMPAIGN, CampaignAlreadyExistException.MESSAGE));
    }

    @Test
    @DisplayName("Should return bad request when invalid campaign input")
    void testCreateCampaign03() throws Exception {

        Pair<String, VisibilityCampaignCreateDto> visibility = generateVisibility("OU-NORTH", 1721683250L, 1721683251L, 1721683252L,
                1721683253L, 1721683254L, 1721683255L);
        Pair<String, CampaignCreateDto> campaign1 = generateCampaign("   ", "An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(visibility),
                List.of());
        Pair<String, CampaignCreateDto> campaign2 = generateCampaign("campId", "   ",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(visibility),
                List.of());
        List<Pair<String, CampaignCreateDto>> campaigns = List.of(campaign1, campaign2);
        for(Pair<String, CampaignCreateDto> campaign : campaigns) {
            mockMvc.perform(post(Constants.API_CAMPAIGN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(campaign.getValue0()))
                    .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, Constants.API_CAMPAIGN, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));

            CampaignCreateDto campaignCreated = campaignService.getCampaignCreated();

            assertThat(campaignCreated).isNull();
        }
    }

    @Test
    @DisplayName("Should return bad request when invalid visibility")
    void testCreateCampaign04() throws Exception {
        List<Pair<String, VisibilityCampaignCreateDto>> invalidVisibilities = new ArrayList<>(Arrays.asList(
                generateVisibility("   ", 1721683250L, 1721683251L, 1721683252L,
                        1721683253L, 1721683254L, 1721683255L),
                generateVisibility("OU-NORTH", null, 1721683251L, 1721683252L,
                        1721683253L, 1721683254L, 1721683255L),
                generateVisibility("OU-NORTH", 1721683250L, null, 1721683252L,
                        1721683253L, 1721683254L, 1721683255L),
                generateVisibility("OU-NORTH", 1721683250L, 1721683251L, null,
                        1721683253L, 1721683254L, 1721683255L),
                generateVisibility("OU-NORTH", 1721683250L, 1721683251L, 1721683252L,
                        null, 1721683254L, 1721683255L),
                generateVisibility("OU-NORTH", 1721683250L, 1721683251L, 1721683252L,
                        1721683253L, null, 1721683255L),
                generateVisibility("OU-NORTH", 1721683250L, 1721683251L, 1721683252L,
                        1721683253L, 1721683254L, null)
        ));
        invalidVisibilities.add(null);

        for(Pair<String, VisibilityCampaignCreateDto> invalidVisibility : invalidVisibilities) {
            List<Pair<String, VisibilityCampaignCreateDto>> invalidCampaignVisibilities = List.of();
            if(invalidVisibility != null) {
                invalidCampaignVisibilities = List.of(invalidVisibility);
            }
            Pair<String, CampaignCreateDto> campaign = generateCampaign("campId", "campaignLabel",
                    "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                    ContactAttemptConfiguration.F2F,
                    true,
                    invalidCampaignVisibilities,
                    List.of());
            mockMvc.perform(post(Constants.API_CAMPAIGN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(campaign.getValue0()))
                    .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, Constants.API_CAMPAIGN, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));

            CampaignCreateDto campaignCreated = campaignService.getCampaignCreated();

            assertThat(campaignCreated).isNull();
        }

    }

    private Pair<String, CampaignCreateDto> generateCampaign(String campaignId, String campaignLabel,
                                                             String email, IdentificationConfiguration identificationConfiguration,
                                                             ContactOutcomeConfiguration contactOutcomeConfiguration,
                                                             ContactAttemptConfiguration contactAttemptConfiguration,
                                                             Boolean communicationRequestConfiguration,
                                                             List<Pair<String, VisibilityCampaignCreateDto>> visibilities,
                                                             List<Pair<String, ReferentDto>> referents) {
        StringBuilder visibilitiesJson = new StringBuilder("\"visibilities\": [");
        for(Pair<String, VisibilityCampaignCreateDto> visibility : visibilities) {
            visibilitiesJson.append(visibility.getValue0());
            if(!visibility.getValue0().equals(visibilities.getLast().getValue0())) {
                visibilitiesJson.append(", ");
            }
        }
        visibilitiesJson.append("], ");

        StringBuilder referentsJson = new StringBuilder("\"referents\": [");
        for(Pair<String, ReferentDto> referent : referents) {
            referentsJson.append(referent.getValue0());
            if(!referent.getValue0().equals(referents.getLast().getValue0())) {
                referentsJson.append(", ");
            }
        }
        referentsJson.append("], ");

        String campaignTemplate = """
                {""" + visibilitiesJson + referentsJson + """
                    "campaign": "%s",
                    "campaignLabel": "%s",
                    "email": "%s",
                    "identificationConfiguration": "%s",
                    "contactOutcomeConfiguration": "%s",
                    "contactAttemptConfiguration": "%s",
                    "communicationRequestConfiguration": %b
                }
                """;
        String campaignJson = String.format(campaignTemplate, campaignId, campaignLabel, email, identificationConfiguration,
                contactOutcomeConfiguration, contactAttemptConfiguration, communicationRequestConfiguration);

        CampaignCreateDto campaignCreateDto = new CampaignCreateDto(campaignId,
                campaignLabel,
                visibilities.stream()
                        .map(Pair::getValue1)
                        .toList(),
                referents.stream()
                        .map(Pair::getValue1)
                        .toList(),
                email,
                IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                communicationRequestConfiguration
        );

        return Pair.with(campaignJson, campaignCreateDto);
    }

    /**
     * Generate a pair of visibility json string and visbility create dto
     * @param organizationalUnit
     * @param managementDate
     * @param interviewerDate
     * @param identificationDate
     * @param collectionStartDate
     * @param collectionEndDate
     * @param endDate
     * @return
     */
    private Pair<String, VisibilityCampaignCreateDto> generateVisibility(String organizationalUnit,
                                                                         Long managementDate, Long interviewerDate,
                                                                         Long identificationDate, Long collectionStartDate,
                                                                         Long collectionEndDate, Long endDate) {
        String visibilityPlaceHolder = """
        {
            "organizationalUnit": "%s",
            "managementStartDate": %d,
            "interviewerStartDate": %d,
            "identificationPhaseStartDate": %d,
            "collectionStartDate": %d,
            "collectionEndDate": %d,
            "endDate": %d
        }""";
        String visibilityJson = String.format(String.format(visibilityPlaceHolder, organizationalUnit,
                managementDate, interviewerDate, identificationDate, collectionStartDate, collectionEndDate, endDate));

        VisibilityCampaignCreateDto visibilityCampaignCreateDto = new VisibilityCampaignCreateDto(managementDate,
                interviewerDate, identificationDate, collectionStartDate, collectionEndDate, endDate, organizationalUnit, null);
        return Pair.with(visibilityJson, visibilityCampaignCreateDto);
    }

    private Pair<String, ReferentDto> generateReferent(String firstName, String lastName, String phoneNumber, String role) {
        String referentPlaceHolder = """
        {
            "firstName": "%s",
            "lastName": "%s",
            "phoneNumber": "%s",
            "role": "%s"
        }""";
        String referentJson = String.format(referentPlaceHolder, firstName, lastName, phoneNumber, role);

        ReferentDto referentDto = new ReferentDto(firstName, lastName, phoneNumber, role);
        return Pair.with(referentJson, referentDto);
    }
}
