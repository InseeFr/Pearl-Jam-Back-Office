package fr.insee.pearljam.api.reporting.scheduler;

import fr.insee.pearljam.infrastructure.persistence.reporting.batch.CampaignProgressBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignProgressSchedulerTest {

    @Mock
    private CampaignProgressBatch campaignProgressBatch;

    private Clock clock;

    private CampaignProgressScheduler scheduler;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(
                Instant.parse("2025-06-10T12:00:00Z"),
                ZoneOffset.UTC
        );

        scheduler = new CampaignProgressScheduler(
                clock,
                campaignProgressBatch,
                3
        );
    }

    @Test
    void shouldComputeTodayAndYesterdaySnapshot() {
        // When
        scheduler.computeTodayAndYesterdaySnapshot();

        // Then
        verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 10));
        verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 9));
        verifyNoMoreInteractions(campaignProgressBatch);
    }

    @Test
    void shouldComputeDailySnapshotForConfiguredNumberOfDays() {
        // When
        scheduler.computeDailySnapshot();

        // Then
        verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 9));
        verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 8));
        verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 7));

        verifyNoMoreInteractions(campaignProgressBatch);
    }

    @Test
    void shouldComputeOnlyYesterdayWhenDailyCronDaysEqualsOne() {
        // Given
        scheduler = new CampaignProgressScheduler(
                clock,
                campaignProgressBatch,
                1
        );

        // When
        scheduler.computeDailySnapshot();

        // Then
        verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 9));
        verifyNoMoreInteractions(campaignProgressBatch);
    }

    @Test
    void shouldCallBatchInChronologicalOrderForDailySnapshot() {
        // When
        scheduler.computeDailySnapshot();

        // Then
        InOrder inOrder = inOrder(campaignProgressBatch);

        inOrder.verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 9));
        inOrder.verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 8));
        inOrder.verify(campaignProgressBatch).run(LocalDate.of(2025, 6, 7));

        inOrder.verifyNoMoreInteractions();
    }
}

