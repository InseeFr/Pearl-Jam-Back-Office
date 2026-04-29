package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.request.CloseSurveyUnitsRequest;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    void shouldReturn204WhenClosingSurveyUnit() throws Exception {
        String surveyUnitId = "12";

        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(List.of(surveyUnitId));
        request.setClosingCauseType(ClosingCauseType.NPA);

        mockMvc.perform(post(Constants.API_SURVEYUNITS_CLOSE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNoContent());

        List<String> result =
                closingCauseRepository.findSurveyUnitIdsWithClosingCause(List.of(surveyUnitId));

        assertThat(result).contains(surveyUnitId);
    }

    @Test
    void shouldReturn404WhenSurveyUnitDoesNotExist() throws Exception {
        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(List.of("NONEXISTENT"));
        request.setClosingCauseType(ClosingCauseType.NPA);

        mockMvc.perform(post(Constants.API_SURVEYUNITS_CLOSE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRollbackWhenBatchFails() throws Exception {
        List<String> ids = List.of("22", "NONEXISTENT", "23");

        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(ids);
        request.setClosingCauseType(ClosingCauseType.NPA);

        mockMvc.perform(post(Constants.API_SURVEYUNITS_CLOSE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNotFound());

        List<String> result =
                closingCauseRepository.findSurveyUnitIdsWithClosingCause(List.of("22", "23"));

        assertThat(result).isEmpty(); // ✅ rollback verified
    }

    @Test
    void shouldReturn409WhenAlreadyClosed() throws Exception {
        String surveyUnitId = "11"; // already has closing cause in test data

        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(List.of(surveyUnitId));
        request.setClosingCauseType(ClosingCauseType.NPA);

        mockMvc.perform(post(Constants.API_SURVEYUNITS_CLOSE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isConflict());
    }
}