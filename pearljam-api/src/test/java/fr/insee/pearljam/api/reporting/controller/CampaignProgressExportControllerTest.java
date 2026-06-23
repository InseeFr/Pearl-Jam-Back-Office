package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.export.progress.CampaignProgressCsv;
import fr.insee.pearljam.api.reporting.export.progress.CampaignProgressCsvExporter;
import fr.insee.pearljam.api.reporting.export.progress.CampaignProgressCsvPresenter;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CampaignProgressExportControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingPort reportingPort;

    @BeforeEach
    void setup() {
        reportingPort = mock(CampaignReportingPort.class);
        when(reportingPort.getCampaignsStats(any(), any(), any())).thenReturn(new CampaignProgressCsv(List.of()));

        CampaignProgressCsvExporter exporter =
                new CampaignProgressCsvExporter(new CampaignProgressCsvPresenter(), reportingPort);
        CampaignProgressExportController controller = new CampaignProgressExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Returns 200 OK with CSV content type when date is provided")
    void shouldReturnOk_withCsvContentType() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    @DisplayName("Returns an attachment whose filename includes the date")
    void shouldReturnAttachmentWithFilename() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_enquetes")))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("10062025.csv")));
    }

    @Test
    @DisplayName("Returns a CSV starting with the BOM and the expected headers")
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        // Given
        // When
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // Then
        String csv = new String(content);
        assertThat(csv)
                .startsWith("\uFEFF")
                .contains("Enquête")
                .contains("Taux d'avancement");
    }

    @Test
    @DisplayName("Returns a CSV with one data row per campaign returned by the port")
    void shouldReturnCsvWithDataRows() throws Exception {
        // Given
        CampaignProgressCsv csv = new CampaignProgressCsv(List.of(
                CsvRow.from("Enquête Test", 75.5f, 10, 2, 3, 4, 5, 6, 7, 8, 9, 1, 11, 12)
        ));
        when(reportingPort.getCampaignsStats(any(), any(), any())).thenReturn(csv);

        // When
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // Then
        String csvContent = new String(content);
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête Test;75.5;");
    }

    @Test
    @DisplayName("Returns 400 Bad Request when date is missing")
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/progress/export"))
                .andExpect(status().isBadRequest());
    }
}
