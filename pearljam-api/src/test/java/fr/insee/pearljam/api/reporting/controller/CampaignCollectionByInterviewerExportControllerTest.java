package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.collect.InterviewerCollectCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
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

class CampaignCollectionByInterviewerExportControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByInterviewersPort port;

    private static final CollectionRatesResponse RATES = new CollectionRatesResponse(0f, 0f, 0f);
    private static final ContactOutcomesProgressResponse OUTCOMES =
            new ContactOutcomesProgressResponse(0L, 0L, 0L, 0L, 0L);
    private static final ClosingCausesProgressResponse CLOSING_CAUSES =
            new ClosingCausesProgressResponse(0L, 0L, 0L);

    private static final CampaignCollectionByInterviewersResponse EMPTY_RESULT =
            new CampaignCollectionByInterviewersResponse(
                    List.of(),
                    new CampaignCollectionByInterviewersResponse.OrganizationUnit(0L, RATES, OUTCOMES, CLOSING_CAUSES),
                    new CampaignCollectionByInterviewersResponse.Campaign(0L, 0L, RATES, OUTCOMES, CLOSING_CAUSES)
            );

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByInterviewersPort.class);
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(EMPTY_RESULT);

        InterviewerCollectCsvExporter exporter =
                new InterviewerCollectCsvExporter(new CampaignCollectionByInterviewersPresenter(), port);
        CampaignCollectionByInterviewerExportController controller =
                new CampaignCollectionByInterviewerExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_withCsvContentType() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    void shouldReturnAttachmentWithFilename() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_collecte_enqueteurs_10062025.csv")));
    }

    @Test
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        assertThat(csv).startsWith("﻿")
                .contains("Nom prénom")
                .contains("Idep")
                .contains("Taux de collecte")
                .contains("Confiées");
    }

    @Test
    void shouldReturnCsvWithDataRows() throws Exception {
        CampaignCollectionByInterviewersResponse response = new CampaignCollectionByInterviewersResponse(
                List.of(new CampaignCollectionByInterviewersResponse.Interviewer(
                        "INT1", "Jane Doe", 100L,
                        new CollectionRatesResponse(50f, 25f, 10f),
                        new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L),
                        new ClosingCausesProgressResponse(5L, 6L, 11L))),
                new CampaignCollectionByInterviewersResponse.OrganizationUnit(0L, RATES, OUTCOMES, CLOSING_CAUSES),
                new CampaignCollectionByInterviewersResponse.Campaign(0L, 0L, RATES, OUTCOMES, CLOSING_CAUSES)
        );
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(response);

        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Jane Doe;INT1;50.0;25.0;10.0;");
    }

    @Test
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenThrow(new CampaignNotFoundException());

        mockMvc.perform(get("/api/reporting/campaigns/unknown/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export"))
                .andExpect(status().isBadRequest());
    }
}
