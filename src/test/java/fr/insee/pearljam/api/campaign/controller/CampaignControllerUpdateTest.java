package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.api.campaign.dto.input.CommunicationInformationCampaignUpdateDto;
import fr.insee.pearljam.api.campaign.dto.input.VisibilityCampaignUpdateDto;
import fr.insee.pearljam.api.controller.CampaignController;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.campaign.controller.dummy.CampaignFakeService;
import fr.insee.pearljam.api.campaign.controller.dummy.ReferentFakeService;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.JsonTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignControllerUpdateTest {

    private CampaignFakeService campaignService;
    private final String updatePath = "/api/campaign/11";
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
    @DisplayName("Should update campaign")
    void testUpdateCampaign01() throws Exception {
        CampaignUpdateDto campaign = generateDefaultCampaign();

        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonTestHelper.toJson(campaign)))
                .andExpect(status().isOk());

        CampaignUpdateDto campaignUpdated = campaignService.getCampaignUpdated();
        assertThat(campaignUpdated).isEqualTo(campaign);
    }

    @Test
    @DisplayName("Should return bad request when invalid campaign label")
    void testUpdateCampaign02() throws Exception {
        CampaignUpdateDto campaign = generateCampaign("    ",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                List.of(),
                List.of(),
                List.of());
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonTestHelper.toJson(campaign)))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, updatePath, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));

        CampaignUpdateDto campaignUpdated = campaignService.getCampaignUpdated();

        assertThat(campaignUpdated).isNull();
    }

    @Test
    @DisplayName("Should return bad request when invalid OU in visibility")
    void testUpdateCampaign03() throws Exception {
        VisibilityCampaignUpdateDto visibility = generateVisibility("   ", 1721683250000L,
                1721683251000L, 1721683252000L,
                1721683253000L, 1721683254000L,
                1721683255000L);
        testUpdateExceptions(visibility, HttpStatus.BAD_REQUEST, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE);
    }

    @Test
    @DisplayName("Should return not found when campaign does not exist")
    void testUpdateCampaign04() throws Exception {
        campaignService.setShouldThrowCampaignNotFoundException(true);
        testUpdateExceptions(HttpStatus.NOT_FOUND, CampaignNotFoundException.MESSAGE);
    }

    @Test
    @DisplayName("Should return not found when visibility does not exist")
    void testUpdateCampaign05() throws Exception {
        campaignService.setShouldThrowVisibilityNotFoundException(true);
        testUpdateExceptions(HttpStatus.NOT_FOUND, VisibilityNotFoundException.MESSAGE);
    }

    @Test
    @DisplayName("Should return bad request when visibility has no date set")
    void testUpdateCampaign06() throws Exception {
        VisibilityCampaignUpdateDto visibility = generateVisibility("ou-id",
                null, null,
                null, null,
                null, null);
        testUpdateExceptions(visibility, HttpStatus.BAD_REQUEST, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE);
    }

    @Test
    @DisplayName("Should return conflict when invalidating update")
    void testUpdateCampaign07() throws Exception {
        campaignService.setShouldThrowVisibilityHasInvalidDatesException(true);
        testUpdateExceptions(HttpStatus.CONFLICT, VisibilityHasInvalidDatesException.MESSAGE);
    }

    private void testUpdateExceptions(HttpStatus httpStatus,
                                      String message) throws Exception {
        testUpdateExceptions(generateDefaultVisibility(), httpStatus, message);
    }

    private void testUpdateExceptions(VisibilityCampaignUpdateDto visibility, HttpStatus httpStatus, String message) throws Exception {
        CampaignUpdateDto campaign = generateCampaign("campaignLabel",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                List.of(visibility),
                List.of(),
                List.of());
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonTestHelper.toJson(campaign)))
                .andDo(print())
                .andExpect(MockMvcTestUtils.apiErrorMatches(httpStatus, updatePath, message));

        CampaignUpdateDto campaignUpdated = campaignService.getCampaignUpdated();

        assertThat(campaignUpdated).isNull();
    }

    private CampaignUpdateDto generateDefaultCampaign() {
        VisibilityCampaignUpdateDto firstVisibility = generateDefaultVisibility();
        VisibilityCampaignUpdateDto secondVisibility = generateVisibility("OU-SOUTH", 1721683260L,
                1721683261000L,
                1721683262000L,
                1721683263000L,
                1721683264000L,
                1721683265000L);

        CommunicationInformationCampaignUpdateDto firstCommunication = generateCommunicationInformation("OU-SOUTH",
                "mail",
                "tel");
        ReferentDto firstReferent = new ReferentDto("Bob", "Marley", "0123456789", "PRIMARY");
        ReferentDto secondReferent = new ReferentDto("Dupont", "Jean", "1234567890", "PRIMARY");
        return generateCampaign("An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                List.of(firstVisibility, secondVisibility),
                List.of(firstCommunication),
                List.of(firstReferent, secondReferent));
    }

    private CommunicationInformationCampaignUpdateDto generateCommunicationInformation(String ouId, String mail, String tel) {
        return new CommunicationInformationCampaignUpdateDto(ouId, mail, tel);
    }

    private CampaignUpdateDto generateCampaign(String campaignLabel,
                                               String email, IdentificationConfiguration identificationConfiguration,
                                               ContactOutcomeConfiguration contactOutcomeConfiguration,
                                               ContactAttemptConfiguration contactAttemptConfiguration,
                                               List<VisibilityCampaignUpdateDto> visibilities,
                                               List<CommunicationInformationCampaignUpdateDto> communicationInformations,
                                               List<ReferentDto> referents) {
        return new CampaignUpdateDto(
                campaignLabel,
                visibilities,
                communicationInformations,
                referents,
                email,
                identificationConfiguration,
                contactOutcomeConfiguration,
                contactAttemptConfiguration
        );

    }

    private VisibilityCampaignUpdateDto generateDefaultVisibility() {
        return generateVisibility("OU-NORTH", 1721683250000L, 1721683251000L,
                1721683252000L, 1721683253000L,
                1721683254000L, 1721683255000L);
    }

    private VisibilityCampaignUpdateDto generateVisibility(
            String organizationalUnit, Long managementDate,
            Long interviewerDate, Long identificationDate,
            Long collectionStartDate, Long collectionEndDate, Long endDate) {
        return new VisibilityCampaignUpdateDto(managementDate,
                interviewerDate, identificationDate, collectionStartDate, collectionEndDate, endDate, organizationalUnit);
    }
}
