package fr.insee.pearljam.api.reporting.scheduler;

import fr.insee.pearljam.infrastructure.persistence.reporting.batch.CampaignProgressBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;

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
                2,
                3
        );
    }

    @Test
    void shouldComputeTodayAndYesterdaySnapshot() {
        // When
        scheduler.computePeriodicSnapshot();

        // Then
        verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 10));
        verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 9));
        verifyNoMoreInteractions(campaignProgressBatch);
    }

    @Test
    void shouldComputeDailySnapshotForConfiguredNumberOfDays() {
        // When
        scheduler.computeHistoricalSnapshot();

        // Then
        verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 9));
        verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 8));
        verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 7));

        verifyNoMoreInteractions(campaignProgressBatch);
    }

    @Test
    void shouldComputeOnlyYesterdayWhenDailyCronDaysEqualsOne() {
        // Given
        scheduler = new CampaignProgressScheduler(
                clock,
                campaignProgressBatch,
                2,1
        );

        // When
        scheduler.computeHistoricalSnapshot();

        // Then
        verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 9));
        verifyNoMoreInteractions(campaignProgressBatch);
    }

    @Test
    void shouldCallBatchInChronologicalOrderForDailySnapshot() {
        // When
        scheduler.computeHistoricalSnapshot();

        // Then
        InOrder inOrder = inOrder(campaignProgressBatch);

        inOrder.verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 9));
        inOrder.verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 8));
        inOrder.verify(campaignProgressBatch).run(LocalDate.of(2025, Month.JUNE, 7));

        inOrder.verifyNoMoreInteractions();
    }
}

