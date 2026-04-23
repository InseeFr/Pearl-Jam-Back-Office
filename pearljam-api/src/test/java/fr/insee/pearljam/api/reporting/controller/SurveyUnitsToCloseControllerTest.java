package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToCloseStatsPresenter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitsToClosePort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitToClose;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyUnitsToCloseControllerTest {

    @Mock
    private SurveyUnitsToClosePort surveyUnitsToClosePort;

    private SurveyUnitToCloseStatsPresenter surveyUnitToCloseStatsPresenter;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SurveyUnitsToCloseController controller;

    @Test
    void testGetSurveyUnitsToClose() {
        // Given
        String userId = "test-user";

        // Create a mock result
        SurveyUnitToClose expectedDto = new SurveyUnitToClose();
        expectedDto.setCampaignLabel("Test Campaign");
        expectedDto.setSurveyUnitId("SU-123");
        expectedDto.setSsech(42);

        when(surveyUnitsToClosePort.getSurveyUnitsToClose(userId, surveyUnitToCloseStatsPresenter))
                .thenReturn(List.of(expectedDto));

        // When
        List<SurveyUnitToCloseResponse> result = controller.getSurveyUnitsToClose(request, userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

    }
}
