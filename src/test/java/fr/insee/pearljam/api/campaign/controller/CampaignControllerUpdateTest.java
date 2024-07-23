package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.api.campaign.dto.input.visibility.VisibilityCampaignUpdateDto;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.controller.CampaignController;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.service.dummy.CampaignFakeService;
import fr.insee.pearljam.api.service.dummy.ReferentFakeService;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import org.javatuples.Pair;
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
        Pair<String, VisibilityCampaignUpdateDto> firstVisibility = generateVisibility("OU-NORTH", 1721683250L,
                1721683251L,
                1721683252L,
                1721683253L,
                1721683254L,
                1721683255L);
        Pair<String, VisibilityCampaignUpdateDto> secondVisibility = generateVisibility("OU-SOUTH", 1721683260L,
                1721683261L,
                1721683262L,
                1721683263L,
                1721683264L,
                1721683265L);
        Pair<String, ReferentDto> firstReferent = generateReferent("Bob", "Marley", "0123456789", "PRIMARY");
        Pair<String, ReferentDto> secondReferent = generateReferent("Dupont", "Jean", "1234567890", "PRIMARY");
        Pair<String, CampaignUpdateDto> campaign = generateCampaign("An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(firstVisibility, secondVisibility),
                List.of(firstReferent, secondReferent));

        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(campaign.getValue0()))
                .andExpect(status().isOk());

        CampaignUpdateDto campaignUpdated = campaignService.getCampaignUpdated();
        assertThat(campaignUpdated).isEqualTo(campaign.getValue1());
    }

    @Test
    @DisplayName("Should return not found when campaign does not exists")
    void testUpdateCampaign02() throws Exception {
        campaignService.setShouldThrowCampaignNotFoundException(true);
        Pair<String, VisibilityCampaignUpdateDto> visibility = generateVisibility("OU-SOUTH", 1721683260L,
                1721683261L,
                1721683262L,
                1721683263L,
                1721683264L,
                1721683265L);
        Pair<String, CampaignUpdateDto> campaign = generateCampaign("An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(visibility),
                List.of());

        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(campaign.getValue0()))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, updatePath, CampaignNotFoundException.MESSAGE));
    }

    @Test
    @DisplayName("Should return not found when visibility does not exist")
    void testUpdateCampaign03() throws Exception {
        campaignService.setShouldThrowVisibilityNotFoundException(true);
        Pair<String, VisibilityCampaignUpdateDto> visibility = generateVisibility("OU-SOUTH", 1721683260L,
                1721683261L,
                1721683262L,
                1721683263L,
                1721683264L,
                1721683265L);
        Pair<String, CampaignUpdateDto> campaign = generateCampaign("An other campaign",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(visibility),
                List.of());

        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(campaign.getValue0()))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, updatePath, VisibilityNotFoundException.MESSAGE));
    }

    @Test
    @DisplayName("Should return bad request when invalid campaign input")
    void testUpdateCampaign04() throws Exception {
        Pair<String, CampaignUpdateDto> campaign = generateCampaign("    ",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(),
                List.of());
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(campaign.getValue0()))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, Constants.API_CAMPAIGN, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));

        CampaignUpdateDto campaignUpdated = campaignService.getCampaignUpdated();

        assertThat(campaignUpdated).isNull();
    }

    @Test
    @DisplayName("Should return bad request when invalid visibility")
    void testUpdateCampaign05() throws Exception {
        Pair<String, VisibilityCampaignUpdateDto> visibility = generateVisibility("   ", 1721683250L, 1721683251L, 1721683252L,
                1721683253L, 1721683254L, 1721683255L);
        Pair<String, CampaignUpdateDto> campaign = generateCampaign("campaignLabel",
                "test.test@sdf.com", IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true,
                List.of(visibility),
                List.of());
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(campaign.getValue0()))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, Constants.API_CAMPAIGN, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));

        CampaignUpdateDto campaignUpdated = campaignService.getCampaignUpdated();

        assertThat(campaignUpdated).isNull();
    }

    private Pair<String, CampaignUpdateDto> generateCampaign(String campaignLabel,
                                                             String email, IdentificationConfiguration identificationConfiguration,
                                                             ContactOutcomeConfiguration contactOutcomeConfiguration,
                                                             ContactAttemptConfiguration contactAttemptConfiguration,
                                                             Boolean communicationRequestConfiguration,
                                                             List<Pair<String, VisibilityCampaignUpdateDto>> visibilities,
                                                             List<Pair<String, ReferentDto>> referents) {
        StringBuilder visibilitiesJson = new StringBuilder("\"visibilities\": [");
        for(Pair<String, VisibilityCampaignUpdateDto> visibility : visibilities) {
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
                    "campaignLabel": "%s",
                    "email": "%s",
                    "identificationConfiguration": "%s",
                    "contactOutcomeConfiguration": "%s",
                    "contactAttemptConfiguration": "%s",
                    "communicationRequestConfiguration": %b
                }
                """;
        String campaignJson = String.format(campaignTemplate, campaignLabel, email, identificationConfiguration,
                contactOutcomeConfiguration, contactAttemptConfiguration, communicationRequestConfiguration);

        CampaignUpdateDto campaignUpdateDto = new CampaignUpdateDto(
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

        return Pair.with(campaignJson, campaignUpdateDto);
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
    private Pair<String, VisibilityCampaignUpdateDto> generateVisibility(String organizationalUnit,
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

        VisibilityCampaignUpdateDto VisibilityCampaignUpdateDto = new VisibilityCampaignUpdateDto(managementDate,
                interviewerDate, identificationDate, collectionStartDate, collectionEndDate, endDate, organizationalUnit);
        return Pair.with(visibilityJson, VisibilityCampaignUpdateDto);
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

    /*
    @Test
    @Order(220)
    void testPutCampaign() throws Exception {
        CampaignUpdateDto campaignUpdateDto = new CampaignUpdateDto(
                "Everyday life and health survey 2021",
                List.of(visi1, visi2),
                List.of(new ReferentDto("Bob", "Marley", "0123456789", "PRIMARY")),
                null,
                IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                null
        );

        VisibilityCampaignUpdateDto wvcd = generateVisibilityCampaignUpdateDto("OU-WEST", "ZCLOSEDX00", false);

        VisibilityCampaignUpdateDto svcd = generateVisibilityCampaignUpdateDto("OU-SOUTH", "ZCLOSEDX00", false);

        VisibilityCampaignUpdateDto emptyVcd = new VisibilityCampaignUpdateDto(null, null,
                null, null, null,
                null, "OU-WEST");

        VisibilityCampaignUpdateDto missingVcd = generateVisibilityCampaignUpdateDto("OU-SOUTH", "ZCLOSEDX00", false);
        missingVcd.setOrganizationalUnit("OU-TEAPOT");

        VisibilityCampaignUpdateDto invalidVcd = generateVisibilityCampaignUpdateDto("OU-WEST", "ZCLOSEDX00", true);

        // path variable campaignId not found in DB
        mockMvc.perform(put("/api/campaign/MISSING")
                        .with(authentication(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(campaignUpdateDto)))
                .andExpect(status().isNotFound());

        campaignUpdateDto.setCampaignLabel("Everyday life and health survey 2021");
        campaignUpdateDto.setVisibilities(List.of(emptyVcd));
        mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
                        .with(authentication(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(campaignUpdateDto)))
                .andExpect(status().isBadRequest());

        // NOT FOUND VISIBILITY
        campaignUpdateDto.setVisibilities(List.of(missingVcd));
        mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
                        .with(authentication(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(campaignUpdateDto)))
                .andExpect(status().isNotFound());

        // CONFLICT due to visibilities
        campaignUpdateDto.setVisibilities(List.of(invalidVcd));
        mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
                        .with(authentication(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(campaignUpdateDto)))
                .andExpect(status().isConflict());

        // 200
        campaignUpdateDto.setVisibilities(List.of(wvcd, svcd));
        campaignUpdateDto.setEmail("updated.email@test.com");
        campaignUpdateDto.setContactAttemptConfiguration(ContactAttemptConfiguration.TEL);
        campaignUpdateDto.setContactOutcomeConfiguration(ContactOutcomeConfiguration.TEL);
        campaignUpdateDto.setIdentificationConfiguration(IdentificationConfiguration.NOIDENT);

        mockMvc.perform(put("/api/campaign/ZCLOSEDX00")
                        .with(authentication(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(campaignUpdateDto)))
                .andExpect(status().isOk());

        assertEquals(wvcd.endDate(),
                visibilityRepository.findVisibilityByCampaignIdAndOuId("ZCLOSEDX00", "OU-WEST").get().getEndDate());
        CampaignDto updatedCampaign = campaignRepository.findDtoById("ZCLOSEDX00");
        assertEquals("updated.email@test.com", updatedCampaign.getEmail());
        assertEquals(ContactAttemptConfiguration.TEL, updatedCampaign.getContactAttemptConfiguration());
        assertEquals(ContactOutcomeConfiguration.TEL, updatedCampaign.getContactOutcomeConfiguration());
        assertEquals(IdentificationConfiguration.NOIDENT, updatedCampaign.getIdentificationConfiguration());
    }*/
}
