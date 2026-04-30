package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.collection.CampaignCollectionCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignCollectionExportControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingPort reportingPort;

    @BeforeEach
    void setup() {
        reportingPort = mock(CampaignReportingPort.class);
        when(reportingPort.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        CampaignCollectionCsvExporter exporter =
                new CampaignCollectionCsvExporter(new CampaignCollectionPresenter(), reportingPort);
        CampaignCollectionExportController controller = new CampaignCollectionExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_withCsvContentType() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    void shouldReturnAttachmentWithFilenameWithoutPrefix() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_collecte_enquetes_10062025.csv")));
    }

    @Test
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        assertThat(csv).startsWith("﻿")
                .contains("Enquête")
                .contains("Taux de collecte")
                .contains("Confiées");
    }

    @Test
    void shouldReturnCsvWithDataRows() throws Exception {
        CampaignCollectionResponse response = new CampaignCollectionResponse(
                "camp-1", "Enquête Test", 100L,
                new CollectionRatesResponse(50f, 25f, 10f),
                new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L),
                new ClosingCausesProgressResponse(5L, 6L, 11L)
        );
        when(reportingPort.getCampaignsStats(any(), any(), any())).thenReturn(List.of(response));

        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête Test;50.0;25.0;10.0;");
    }

    @Test
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/collection/export"))
                .andExpect(status().isBadRequest());
    }
}
