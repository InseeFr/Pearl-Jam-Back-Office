package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.surveyunit.controller.SurveyUnitClosingExportController;
import fr.insee.pearljam.api.surveyunit.controller.export.closing.SurveyUnitClosingApiCsvPresenter;
import fr.insee.pearljam.api.surveyunit.controller.export.closing.SurveyUnitClosingCsv;
import fr.insee.pearljam.api.surveyunit.controller.export.closing.SurveyUnitClosingCsvExporter;
import fr.insee.pearljam.api.surveyunit.controller.export.closing.SurveyUnitClosingCsvHeaders;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitClosingViewModelMapper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SurveyUnitClosingExportControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        SurveyUnitClosingPort port = mock(SurveyUnitClosingPort.class);
        when(port.getSurveyUnitsToClose(any(), any(), any())).thenReturn(new SurveyUnitClosingCsv(List.of()));

        SurveyUnitClosingCsvExporter exporter =
                new SurveyUnitClosingCsvExporter(new SurveyUnitClosingApiCsvPresenter(new SurveyUnitClosingViewModelMapper()), port);
        SurveyUnitClosingExportController controller = new SurveyUnitClosingExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Returns 200 OK with CSV content type")
    void shouldReturnOkWithCsvContentType() throws Exception {
        mockMvc.perform(get(Constants.API_SURVEYUNITS_TO_CLOSE_EXPORT))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    @DisplayName("Returns an attachment whose filename contains 'UE_à_clore'")
    void shouldReturnAttachmentWithFilename() throws Exception {
        mockMvc.perform(get(Constants.API_SURVEYUNITS_TO_CLOSE_EXPORT))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("UE_à_clore")));
    }

    @Test
    @DisplayName("Returns a CSV starting with the BOM and the expected headers")
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        byte[] content = mockMvc.perform(get(Constants.API_SURVEYUNITS_TO_CLOSE_EXPORT))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        assertThat(csv).startsWith("\uFEFF")
                .contains(SurveyUnitClosingCsvHeaders.values()[0].getHeaderName());
    }

    @Test
    @DisplayName("Returns a CSV with headers only when no survey units are returned by the port")
    void shouldReturnCsvWithHeadersOnly_whenNoSurveyUnits() throws Exception {
        byte[] content = mockMvc.perform(get(Constants.API_SURVEYUNITS_TO_CLOSE_EXPORT))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
    }
}