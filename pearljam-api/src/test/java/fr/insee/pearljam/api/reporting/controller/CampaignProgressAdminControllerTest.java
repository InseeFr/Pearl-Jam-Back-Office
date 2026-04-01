package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.infrastructure.persistence.reporting.batch.CampaignProgressBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignProgressAdminControllerTest {

    private MockMvc mockMvc;
    private CampaignProgressBatch batch;
    private static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 15);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

    @BeforeEach
    void setup() {
        batch = mock(CampaignProgressBatch.class);
        CampaignProgressAdminController controller = new CampaignProgressAdminController(batch, FIXED_CLOCK);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturn204_whenDateIsInThePast() throws Exception {
        LocalDate pastDate = FIXED_TODAY.minusDays(1);

        mockMvc.perform(post("/api/admin/reporting/snapshots")
                        .param("date", pastDate.toString()))
                .andExpect(status().isNoContent());

        verify(batch).run(pastDate);
    }

    @Test
    void shouldReturn204_whenDateIsToday() throws Exception {
        mockMvc.perform(post("/api/admin/reporting/snapshots")
                        .param("date", FIXED_TODAY.toString()))
                .andExpect(status().isNoContent());

        verify(batch).run(FIXED_TODAY);
    }

    @Test
    void shouldReturn400_whenDateIsInTheFuture() throws Exception {
        LocalDate futureDate = FIXED_TODAY.plusDays(1);

        mockMvc.perform(post("/api/admin/reporting/snapshots")
                        .param("date", futureDate.toString()))
                .andExpect(status().isBadRequest());

        verify(batch, never()).run(any());
    }
}
