package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.service.CampaignCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignCollectionControllerTest {

    private MockMvc mockMvc;
    private CampaignCollectionService collectionService;

    @BeforeEach
    void setup() {
        collectionService = mock(CampaignCollectionService.class);
        when(collectionService.getCampaignsCollection(anyString(), any())).thenReturn(List.of());

        CampaignCollectionController controller =
                new CampaignCollectionController(collectionService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_whenDayProvided() throws Exception {
        LocalDate day = LocalDate.of(2025, 6, 10);

        mockMvc.perform(get("/api/reporting/campaigns/collection")
                        .param("day", day.toString()))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(collectionService).getCampaignsCollection(any(), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(day);
    }

    @Test
    void shouldPassNullDay_whenDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/collection"))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(collectionService).getCampaignsCollection(any(), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isNull();
    }
}
