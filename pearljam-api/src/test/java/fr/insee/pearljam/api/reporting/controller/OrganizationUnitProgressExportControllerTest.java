package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.export.progress.OrganizationUnitProgressCsv;
import fr.insee.pearljam.api.reporting.export.progress.OrganizationUnitProgressCsvExporter;
import fr.insee.pearljam.api.reporting.export.progress.OrganizationUnitProgressCsvPresenter;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
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

class OrganizationUnitProgressExportControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByOrganizationUnitsPort port;

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByOrganizationUnitsPort.class);
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenReturn(new OrganizationUnitProgressCsv(List.of()));

        OrganizationUnitProgressCsvExporter exporter =
                new OrganizationUnitProgressCsvExporter(new OrganizationUnitProgressCsvPresenter(), port);
        OrganizationUnitProgressExportController controller = new OrganizationUnitProgressExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Returns 200 OK with CSV content type when date is provided")
    void shouldReturnOk_withCsvContentType() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    @DisplayName("Returns an attachment whose filename includes the date")
    void shouldReturnAttachmentWithFilename() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_sites_10062025.csv")));
    }

    @Test
    @DisplayName("Returns a CSV starting with the BOM and the expected headers")
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        // When
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // Then
        String csv = new String(content);
        assertThat(csv).startsWith("\uFEFF")
                .contains("Site")
                .contains("Taux d'avancement");
    }

    @Test
    @DisplayName("Returns a CSV with one data row per organization unit returned by the port")
    void shouldReturnCsvWithDataRows() throws Exception {
        // Given
        OrganizationUnitProgressCsv csv = new OrganizationUnitProgressCsv(List.of(
                CsvRow.from("Site Paris", 75.5f, 10, 2, 3, 4, 5, 6, 7, 8, 9, 1, 11, 12)
        ));
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(csv);

        // When
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // Then
        String csvContent = new String(content);
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Site Paris;75.5;");
    }

    @Test
    @DisplayName("Returns 404 Not Found when the campaign does not exist")
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        // Given
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenThrow(new CampaignNotFoundException());

        // When / Then
        mockMvc.perform(get("/api/reporting/campaigns/unknown/organization-units/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns 400 Bad Request when date is missing")
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/progress/export"))
                .andExpect(status().isBadRequest());
    }
}
