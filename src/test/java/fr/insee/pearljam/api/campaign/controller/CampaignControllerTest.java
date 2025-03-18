package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.controller.CampaignController;
import fr.insee.pearljam.api.campaign.controller.dummy.CampaignFakeService;
import fr.insee.pearljam.api.campaign.controller.dummy.ReferentFakeService;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.JsonTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.CampaignOnGoingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignControllerTest {

    private MockMvc mockMvc;
    private CampaignFakeService campaignService;
    private final String deletePath = "/api/campaign/campaign-id";
    private final String getPath = "/api/campaign/campaign-id";


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
    @DisplayName("Should retrieve campaign")
    void testGetCampaign01() throws Exception {
        CampaignResponseDto campaign = new CampaignResponseDto("campaign-id", "label", null, null, "email",
                IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.TEL, ContactAttemptConfiguration.F2F, false);
        campaignService.setCampaignToRetrieve(campaign);

        MvcResult mvcResult = mockMvc.perform(get(getPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentResult = mvcResult.getResponse().getContentAsString();
        String expectedResult = JsonTestHelper.toJson(campaign);
        JSONAssert.assertEquals(contentResult, expectedResult, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @DisplayName("Should return not found when campaign not found")
    void testGetCampaign02() throws Exception {
        campaignService.setShouldThrowCampaignNotFoundException(true);
        mockMvc.perform(get(getPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, getPath, CampaignNotFoundException.MESSAGE));
    }

    @Test
    @DisplayName("Should delete campaign")
    void testDeleteCampaign01() throws Exception {
        mockMvc.perform(delete(deletePath)
                        .param("force", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(campaignService.isDeleteForced()).isTrue();
        assertThat(campaignService.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should not force delete when force parameter not set")
    void testDeleteCampaign02() throws Exception {
        mockMvc.perform(delete(deletePath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertThat(campaignService.isDeleteForced()).isFalse();
        assertThat(campaignService.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should return not found when campaign does not exist")
    void testDeleteCampaign03() throws Exception {
        campaignService.setShouldThrowCampaignNotFoundException(true);
        mockMvc.perform(delete(deletePath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, deletePath, CampaignNotFoundException.MESSAGE));
    }

    @Test
    @DisplayName("Should return conflict when campaign is ongoing")
    void testDeleteCampaign04() throws Exception {
        campaignService.setShouldThrowCampaignOnGoingException(true);
        mockMvc.perform(delete(deletePath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.CONFLICT, deletePath, CampaignOnGoingException.MESSAGE));
    }
}
