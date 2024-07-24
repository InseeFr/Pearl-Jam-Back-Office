package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.controller.dummy.VisibilityFakeService;
import fr.insee.pearljam.api.campaign.dto.input.VisibilityUpdateDto;
import fr.insee.pearljam.api.campaign.dto.output.VisibilityCampaignDto;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.JsonTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VisibilityControllerTest {

    private MockMvc mockMvc;
    private VisibilityFakeService visibilityService;
    private final String campaignId = "campaign-id";
    private final String organizationalUnitId = "ou-id";
    private String updatePath;
    private String findPath;

    @BeforeEach
    void setup() {
        updatePath = String.format("/api/campaign/%s/organizational-unit/%s/visibility", campaignId, organizationalUnitId);
        findPath = String.format("/api/campaign/%s/visibilities", campaignId);

        visibilityService = new VisibilityFakeService();
        AuthenticationUserFakeService authenticatedUserService = new AuthenticationUserFakeService(AuthenticatedUserTestHelper.AUTH_ADMIN);
        VisibilityController visibilityController = new VisibilityController(visibilityService, authenticatedUserService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(visibilityController)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should return not found when campaign does not exist")
    void testGetVisibilities01() throws Exception {
        // Given
        List<Visibility> visibilities = List.of(
                new Visibility(campaignId, organizationalUnitId, 1721683250L, 1721683251L, 1721683252L,
                        1721683253L, 1721683254L, 1721683255L),
                new Visibility(campaignId, "ou-id2", 1721683250L, 1721683251L, 1721683252L,
                        1721683253L, 1721683254L, 1721683255L)
        );
        visibilityService.setVisibilitiesToFind(visibilities);

        // When & Then
        MvcResult result = mockMvc.perform(get(findPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<VisibilityCampaignDto> visibilitiesExpected = VisibilityCampaignDto.fromModel(visibilities);
        String resultContent = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(resultContent,
                JsonTestHelper.toJson(visibilitiesExpected),
                JSONCompareMode.STRICT);
    }

    @Test
    @DisplayName("Should return not found when campaign does not exist")
    void testGetVisibilities02() throws Exception {
        visibilityService.setShouldThrowCampaignNotFoundException(true);
        mockMvc.perform(get(findPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, findPath, CampaignNotFoundException.MESSAGE));
    }

    @Test
    @DisplayName("Should update visibility")
    void testUpdateVisibility01() throws Exception {
        VisibilityUpdateDto visibilityToUpdate = generateUpdateVisibility();

        mockMvc.perform(put(updatePath)
                        .content(JsonTestHelper.toJson(visibilityToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Visibility visibilityUpdated = visibilityService.getVisibilityUpdated();
        Visibility visibilityExpected = VisibilityUpdateDto.toModel(visibilityToUpdate, campaignId, organizationalUnitId);

        assertThat(visibilityUpdated).isEqualTo(visibilityExpected);
    }

    @Test
    @DisplayName("Should return not found when visibility does not exist")
    void testUpdateVisibility02() throws Exception {
        visibilityService.setShouldThrowVisibilityNotFoundException(true);
        VisibilityUpdateDto visibilityToUpdate = generateUpdateVisibility();
        mockMvc.perform(put(updatePath)
                        .content(JsonTestHelper.toJson(visibilityToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, updatePath, VisibilityNotFoundException.MESSAGE));
    }

    @Test
    @DisplayName("Should return conflict when problems with dates")
    void testUpdateVisibility03() throws Exception {
        visibilityService.setShouldThrowVisibilityMergingException(true);
        VisibilityUpdateDto visibilityToUpdate = generateUpdateVisibility();
        mockMvc.perform(put(updatePath)
                        .content(JsonTestHelper.toJson(visibilityToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.CONFLICT, updatePath, VisibilityHasInvalidDatesException.MESSAGE));
    }

    @Test
    @DisplayName("Should return bad request when visibility has no date set")
    void testUpdateVisibility04() throws Exception {
        VisibilityUpdateDto visibility = generateUpdateVisibility(
                null, null,
                null, null,
                null, null);
        mockMvc.perform(put(updatePath)
                        .content(JsonTestHelper.toJson(visibility))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, updatePath, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));
    }

    private VisibilityUpdateDto generateUpdateVisibility() {
        return generateUpdateVisibility(1721683250L, 1721683251L, 1721683252L,
                1721683253L, 1721683254L, 1721683255L);
    }

    private VisibilityUpdateDto generateUpdateVisibility(
            Long managementDate, Long interviewerDate,
            Long identificationDate, Long collectionStartDate,
            Long collectionEndDate, Long endDate) {
        return new VisibilityUpdateDto(managementDate,
                interviewerDate, identificationDate, collectionStartDate, collectionEndDate, endDate);
    }
}
