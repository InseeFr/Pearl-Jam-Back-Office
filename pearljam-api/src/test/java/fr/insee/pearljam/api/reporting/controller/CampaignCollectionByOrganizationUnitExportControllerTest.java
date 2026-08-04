package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.collection.OrganizationUnitCollectionCsv;
import fr.insee.pearljam.api.reporting.export.collection.OrganizationUnitCollectionCsvExporter;
import fr.insee.pearljam.api.reporting.export.collection.OrganizationUnitCollectionCsvPresenter;
import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import org.junit.jupiter.api.BeforeEach;
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

class CampaignCollectionByOrganizationUnitExportControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByOrganizationUnitsPort port;

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByOrganizationUnitsPort.class);
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenReturn(new OrganizationUnitCollectionCsv(List.of()));

        OrganizationUnitCollectionCsvExporter exporter =
                new OrganizationUnitCollectionCsvExporter(new OrganizationUnitCollectionCsvPresenter(), port);
        CampaignCollectionByOrganizationUnitExportController controller =
                new CampaignCollectionByOrganizationUnitExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_withCsvContentType() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    void shouldReturnAttachmentWithFilename() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_collecte_sites_10062025.csv")));
    }

    @Test
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        assertThat(csv).startsWith("\uFEFF")
                .contains("Site")
                .contains("Taux de collecte")
                .contains("Confiées");
    }

    @Test
    void shouldReturnCsvWithDataRows() throws Exception {
        OrganizationUnitCollectionCsv csv = new OrganizationUnitCollectionCsv(List.of(
                CsvRow.from("Site Paris", 50f, 25f, 10f, 1L, 2L, 3L, 4L, 10L, 5L, 6L, 11L, 100L)
        ));
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(csv);

        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csvContent = new String(content);
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Site Paris;50.0;25.0;10.0;");
    }

    @Test
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenThrow(new CampaignNotFoundExceptionRuntime());

        mockMvc.perform(get("/api/reporting/campaigns/unknown/organization-units/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/collection/export"))
                .andExpect(status().isBadRequest());
    }
}
