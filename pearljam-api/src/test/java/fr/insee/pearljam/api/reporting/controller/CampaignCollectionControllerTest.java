package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionPresenter;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignCollectionControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        CampaignReportingPort reportingService = mock(CampaignReportingPort.class);
        when(reportingService.getCampaignsStats(anyString(), any(), any())).thenReturn(List.of());

        CampaignCollectionController controller =
                new CampaignCollectionController(reportingService, new CampaignCollectionPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_whenDayProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/collection")
                        .param("day", "2025-06-10"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOk_whenDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/collection"))
                .andExpect(status().isOk());
    }
}
