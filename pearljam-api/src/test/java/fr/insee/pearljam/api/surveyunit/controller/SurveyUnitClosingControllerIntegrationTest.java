package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.request.CloseSurveyUnitsRequest;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.insee.pearljam.contracts.constants.Constants.API_SURVEYUNIT_CLOSE_SURVEYUNITS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Transactional
@DisplayName("SurveyUnit Closing Controller Integration Tests")
class SurveyUnitClosingControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClosingCauseRepository closingCauseRepository;

    @Test
    @WithMockUser
    @DisplayName("Should successfully add closing cause to single survey unit")
    void shouldSuccessfullyAddClosingCauseToSingleSurveyUnit() throws Exception {
        // Given
        String surveyUnitId = "12";

        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(Collections.singletonList(surveyUnitId));
        request.setClosingCauseType(ClosingCauseType.NPA);

        // When/Then
        mockMvc.perform(post(API_SURVEYUNIT_CLOSE_SURVEYUNITS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNoContent());

        // Verify closing cause was created
        List<String> withClosingCause = closingCauseRepository
                .findSurveyUnitIdsWithClosingCause(Collections.singletonList(surveyUnitId));
        assert withClosingCause.contains(surveyUnitId);
    }

    @Test
    @WithMockUser
    @DisplayName("Should not add closing cause to single survey unit that already has one and return 409")
    void shouldNotAddClosingCauseToSingleSurveyUnit() throws Exception {
        // Given
        String surveyUnitId = "11";

        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(Collections.singletonList(surveyUnitId));
        request.setClosingCauseType(ClosingCauseType.NPA);

        // When/Then
        mockMvc.perform(post(API_SURVEYUNIT_CLOSE_SURVEYUNITS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("Should successfully add closing cause to multiple survey units")
    void shouldSuccessfullyAddClosingCauseToMultipleSurveyUnits() throws Exception {
        // Given
        List<String> surveyUnitIds = Arrays.asList("13", "14", "21");

        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(surveyUnitIds);
        request.setClosingCauseType(ClosingCauseType.NPI);

        // When/Then
        mockMvc.perform(post(API_SURVEYUNIT_CLOSE_SURVEYUNITS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsString(request)))
                .andExpect(status().isNoContent());

        // Verify all closing causes were created
        List<String> withClosingCause = closingCauseRepository
                .findSurveyUnitIdsWithClosingCause(surveyUnitIds);
        assert withClosingCause.size() == 3;
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when survey unit does not exist")
    void shouldReturn400WhenSurveyUnitDoesNotExist() throws Exception {
        // Given
        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(Collections.singletonList("NONEXISTENT"));
        request.setClosingCauseType(ClosingCauseType.NPX);

        // When/Then
        mockMvc.perform(post(API_SURVEYUNIT_CLOSE_SURVEYUNITS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle batch processing correctly - all or nothing")
    void shouldHandleBatchProcessingCorrectly() throws Exception {
        // Given - create only some of the survey units
        List<String> surveyUnitIds = Arrays.asList("22", "NONEXISTENT", "23");

        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(surveyUnitIds);
        request.setClosingCauseType(ClosingCauseType.NPA);

        // When/Then - should fail for all
        mockMvc.perform(post(API_SURVEYUNIT_CLOSE_SURVEYUNITS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // Verify NO closing causes were created (all-or-nothing)
        List<String> withClosingCause = closingCauseRepository
                .findSurveyUnitIdsWithClosingCause(Arrays.asList("22", "23"));
        assert withClosingCause.isEmpty();
    }
}