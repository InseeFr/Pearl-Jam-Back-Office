package fr.insee.pearljam.api.reporting.scheduler;

import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressSnapshotServicePort;
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
    private CampaignProgressSnapshotServicePort snapshotService;

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
                snapshotService,
                2,
                3
        );
    }

    @Test
    void shouldComputeTodayAndYesterdaySnapshot() {
        // When
        scheduler.computePeriodicSnapshot();

        // Then
        verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 10));
        verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 9));
        verifyNoMoreInteractions(snapshotService);
    }

    @Test
    void shouldComputeDailySnapshotForConfiguredNumberOfDays() {
        // When
        scheduler.computeHistoricalSnapshot();

        // Then
        verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 9));
        verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 8));
        verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 7));

        verifyNoMoreInteractions(snapshotService);
    }

    @Test
    void shouldComputeOnlyYesterdayWhenDailyCronDaysEqualsOne() {
        // Given
        scheduler = new CampaignProgressScheduler(
                clock,
                snapshotService,
                2,1
        );

        // When
        scheduler.computeHistoricalSnapshot();

        // Then
        verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 9));
        verifyNoMoreInteractions(snapshotService);
    }

    @Test
    void shouldCallBatchInChronologicalOrderForDailySnapshot() {
        // When
        scheduler.computeHistoricalSnapshot();

        // Then
        InOrder inOrder = inOrder(snapshotService);

        inOrder.verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 9));
        inOrder.verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 8));
        inOrder.verify(snapshotService).computeSnapshot(LocalDate.of(2025, Month.JUNE, 7));

        inOrder.verifyNoMoreInteractions();
    }
}

