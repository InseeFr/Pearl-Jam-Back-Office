package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.progress.CampaignProgressCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignProgressPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CampaignProgressExportControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingPort reportingPort;

    @BeforeEach
    void setup() {
        reportingPort = mock(CampaignReportingPort.class);
        when(reportingPort.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        CampaignProgressCsvExporter exporter =
                new CampaignProgressCsvExporter(new CampaignProgressPresenter(), reportingPort);
        CampaignProgressExportController controller = new CampaignProgressExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_withCsvContentType() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    void shouldReturnAttachmentWithFilename() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_enquetes")))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("10062025.csv")));
    }

    @Test
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        assertThat(csv)
                .startsWith("\uFEFF")
                .contains("Enquête")
                .contains("Taux d'avancement");
    }

    @Test
    void shouldReturnCsvWithDataRows() throws Exception {
        CampaignProgressResponse response = new CampaignProgressResponse(
                "camp-1", "Enquête Test", 75.5f,
                new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1),
                new CommunicationsProgressResponse(11, 12)
        );
        when(reportingPort.getCampaignsStats(any(), any(), any())).thenReturn(List.of(response));

        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête Test;75.5;");
    }

    @Test
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/progress/export"))
                .andExpect(status().isBadRequest());
    }
}
