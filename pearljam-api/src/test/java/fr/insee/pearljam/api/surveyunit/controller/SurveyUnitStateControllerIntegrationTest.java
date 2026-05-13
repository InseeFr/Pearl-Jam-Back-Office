package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.request.SurveyUnitsNewStateRequest;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Transactional
@DisplayName("SurveyUnit State Controller Integration Tests")
class SurveyUnitStateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StateRepository stateRepository;

    @Test
    void shouldReturn204WhenAddingStateToSurveyUnit() throws Exception {
        // Given
        String surveyUnitId = "11";
        SurveyUnitsNewStateRequest request = new SurveyUnitsNewStateRequest();
        request.setSurveyUnitIds(List.of(surveyUnitId));
        request.setStateType(StateType.WFT);

        // When
        mockMvc.perform(post(Constants.API_SURVEYUNITS_ADD_STATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNoContent());

        // Then
        Optional<StateType> result = stateRepository.findStateBySurveyUnitId(surveyUnitId);
        assertThat(result).isPresent().contains(StateType.WFT);
    }

    @Test
    void shouldReturn404WhenSurveyUnitDoesNotExist() throws Exception {
        // Given
        SurveyUnitsNewStateRequest request = new SurveyUnitsNewStateRequest();
        request.setSurveyUnitIds(List.of("NONEXISTENT"));
        request.setStateType(StateType.ANV);

        // When & Then
        mockMvc.perform(post(Constants.API_SURVEYUNITS_ADD_STATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRollbackWhenBatchFails() throws Exception {
        // Given
        List<String> ids = List.of("11", "NONEXISTENT", "12");
        SurveyUnitsNewStateRequest request = new SurveyUnitsNewStateRequest();
        request.setSurveyUnitIds(ids);
        request.setStateType(StateType.ANV);

        // When
        mockMvc.perform(post(Constants.API_SURVEYUNITS_ADD_STATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNotFound());

        // Then
        Optional<StateType> result11 = stateRepository.findStateBySurveyUnitId("11");
        Optional<StateType> result12 = stateRepository.findStateBySurveyUnitId("12");

        assertThat(result11).isPresent();
        assertThat(result11.get()).isNotEqualTo(StateType.ANV);
        assertThat(result12).isPresent();
        assertThat(result12.get()).isNotEqualTo(StateType.ANV);
    }

    @Test
    void shouldReturnBadRequestWhenAddingUnexistingStateToSurveyUnit() throws Exception {
        // Given
        String surveyUnitId = "SABIANE50";
        SurveyUnitsNewStateRequest request = new SurveyUnitsNewStateRequest();
        request.setSurveyUnitIds(List.of(surveyUnitId));
        request.setStateType(StateType.NNS);

        // When & Then
        mockMvc.perform(post(Constants.API_SURVEYUNITS_ADD_STATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenRequestWhenAddingStateNotValidatingBusinessRule() throws Exception {
        // Given
        String surveyUnitId = "11";
        SurveyUnitsNewStateRequest request = new SurveyUnitsNewStateRequest();
        request.setSurveyUnitIds(List.of(surveyUnitId));
        request.setStateType(StateType.NNS);

        // When
        mockMvc.perform(post(Constants.API_SURVEYUNITS_ADD_STATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isForbidden());

        // Then
        Optional<StateType> result = stateRepository.findStateBySurveyUnitId(surveyUnitId);
        assertThat(result).isPresent().get().isNotEqualTo(StateType.NNS);
    }
}