package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.campaign.dto.input.VisibilityCampaignCreateDto;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.controller.CampaignController;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.JsonTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.exception.CampaignAlreadyExistException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.insee.pearljam.api.campaign.controller.dummy.CampaignFakeService;
import fr.insee.pearljam.api.campaign.controller.dummy.ReferentFakeService;
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
        CampaignCreateDto campaign= generateDefaultCampaign();

        mockMvc.perform(post(Constants.API_CAMPAIGN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonTestHelper.toJson(campaign)))
                .andExpect(status().isOk());

        CampaignCreateDto campaignCreated = campaignService.getCampaignCreated();
        assertThat(campaignCreated).isEqualTo(campaign);
    }

    @Test
    @DisplayName("Should return bad request when campaign already exists")
    void testCreateCampaign02() throws Exception {
        campaignService.setShouldThrowCampaignAlreadyExistException(true);
        CampaignCreateDto campaign = generateDefaultCampaign();

        mockMvc.perform(post(Constants.API_CAMPAIGN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonTestHelper.toJson(campaign)))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, Constants.API_CAMPAIGN, CampaignAlreadyExistException.MESSAGE));
    }

    @Test
    @DisplayName("Should return bad request when organizationalUnit does not exist")
    void testCreateCampaign03() throws Exception {
        campaignService.setShouldThrowOrganizationalUnitNotFoundException(true);
        CampaignCreateDto campaign = generateDefaultCampaign();

        mockMvc.perform(post(Constants.API_CAMPAIGN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonTestHelper.toJson(campaign)))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, Constants.API_CAMPAIGN, OrganizationalUnitNotFoundException.MESSAGE));
    }

    @Test
    @DisplayName("Should return bad request when invalid campaign label")
    void testCreateCampaign04() throws Exception {

        VisibilityCampaignCreateDto visibility = generateVisibility("OU-NORTH", 1721683250000L, 1721683251000L, 1721683252000L,
                1721683253000L, 1721683254000L, 1721683255000L);
       CampaignCreateDto campaign1 = generateCampaign("   ", "An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(visibility),
                List.of());
       CampaignCreateDto campaign2 = generateCampaign("campId", "   ",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(visibility),
                List.of());
        List<CampaignCreateDto> campaigns = List.of(campaign1, campaign2);
        for(CampaignCreateDto campaign : campaigns) {
            mockMvc.perform(post(Constants.API_CAMPAIGN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonTestHelper.toJson(campaign)))
                    .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, Constants.API_CAMPAIGN, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));

            CampaignCreateDto campaignCreated = campaignService.getCampaignCreated();

            assertThat(campaignCreated).isNull();
        }
    }

    @Test
    @DisplayName("Should return bad request when invalid visibility")
    void testCreateCampaign05() throws Exception {
        List<VisibilityCampaignCreateDto> invalidVisibilities = new ArrayList<>(Arrays.asList(
                generateVisibility("   ", 1721683250000L, 1721683251000L, 1721683252000L,
                        1721683253000L, 1721683254000L, 1721683255000L),
                generateVisibility("OU-NORTH", null, 1721683251000L, 1721683252000L,
                        1721683253000L, 1721683254000L, 1721683255000L),
                generateVisibility("OU-NORTH", 1721683250000L, null, 1721683252000L,
                        1721683253000L, 1721683254000L, 1721683255000L),
                generateVisibility("OU-NORTH", 1721683250000L, 1721683251000L, null,
                        1721683253000L, 1721683254000L, 1721683255000L),
                generateVisibility("OU-NORTH", 1721683250000L, 1721683251000L, 1721683252000L,
                        null, 1721683254000L, 1721683255000L),
                generateVisibility("OU-NORTH", 1721683250000L, 1721683251000L, 1721683252000L,
                        1721683253000L, null, 1721683255000L),
                generateVisibility("OU-NORTH", 1721683250000L, 1721683251000L, 1721683252000L,
                        1721683253000L, 1721683254000L, null)
        ));
        invalidVisibilities.add(null);

        for(VisibilityCampaignCreateDto invalidVisibility : invalidVisibilities) {
            List<VisibilityCampaignCreateDto> invalidCampaignVisibilities = List.of();
            if(invalidVisibility != null) {
                invalidCampaignVisibilities = List.of(invalidVisibility);
            }
           CampaignCreateDto campaign = generateCampaign("campId", "campaignLabel",
                    "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                    ContactAttemptConfiguration.F2F,
                    true,
                    invalidCampaignVisibilities,
                    List.of());
            mockMvc.perform(post(Constants.API_CAMPAIGN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonTestHelper.toJson(campaign)))
                    .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, Constants.API_CAMPAIGN, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));

            CampaignCreateDto campaignCreated = campaignService.getCampaignCreated();

            assertThat(campaignCreated).isNull();
        }
    }

    @Test
    @DisplayName("Should return conflict when invalid visibility dates")
    void testCreateCampaign06() throws Exception {
        campaignService.setShouldThrowVisibilityHasInvalidDatesException(true);
        CampaignCreateDto campaign = generateDefaultCampaign();

        mockMvc.perform(post(Constants.API_CAMPAIGN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonTestHelper.toJson(campaign)))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.CONFLICT, Constants.API_CAMPAIGN, VisibilityHasInvalidDatesException.MESSAGE));
    }

    /**
     * Generate a default campaign
     * @return a pair of json string and dto object for the campaign
     */
    public CampaignCreateDto generateDefaultCampaign() {
        VisibilityCampaignCreateDto firstVisibility = generateVisibility("OU-NORTH", 1721683250000L,
                1721683251000L,
                1721683252000L,
                1721683253000L,
                1721683254000L,
                1721683255000L);
        VisibilityCampaignCreateDto secondVisibility = generateVisibility("OU-SOUTH", 1721683260L,
                1721683261000L,
                1721683262000L,
                1721683263000L,
                1721683264000L,
                1721683265000L);
        ReferentDto firstReferent = new ReferentDto("Bob", "Marley", "0123456789", "PRIMARY");
        ReferentDto secondReferent = new ReferentDto("Dupont", "Jean", "1234567890", "PRIMARY");
        return generateCampaign("campId", "An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.NOIDENT, ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                true,
                List.of(firstVisibility, secondVisibility),
                List.of(firstReferent, secondReferent));
    }

    private CampaignCreateDto generateCampaign(
            String campaignId, String campaignLabel,
            String email, IdentificationConfiguration identificationConfiguration,
            ContactOutcomeConfiguration contactOutcomeConfiguration,
            ContactAttemptConfiguration contactAttemptConfiguration,
            Boolean communicationRequestConfiguration,
            List<VisibilityCampaignCreateDto> visibilities,
            List<ReferentDto> referents) {

        return new CampaignCreateDto(campaignId,
                campaignLabel,
                visibilities,
                referents,
                email,
                identificationConfiguration,
                contactOutcomeConfiguration,
                contactAttemptConfiguration,
                communicationRequestConfiguration
        );
    }

    private VisibilityCampaignCreateDto generateVisibility(String organizationalUnit,
                                                           Long managementDate, Long interviewerDate,
                                                           Long identificationDate, Long collectionStartDate,
                                                           Long collectionEndDate, Long endDate) {
        return new VisibilityCampaignCreateDto(managementDate,
                interviewerDate, identificationDate, collectionStartDate, collectionEndDate, endDate, organizationalUnit);
    }
}
