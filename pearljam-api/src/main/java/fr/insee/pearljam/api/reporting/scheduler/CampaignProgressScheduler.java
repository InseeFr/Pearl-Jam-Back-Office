package fr.insee.pearljam.api.reporting.scheduler;

import fr.insee.pearljam.infrastructure.persistence.reporting.batch.CampaignProgressBatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBooleanProperty(name = "feature.stats-scheduling.enabled")
public class CampaignProgressScheduler {

    private final Clock clock;
    private final CampaignProgressBatch campaignProgressBatch;
    @Value("${feature.stats-scheduling.periodic-cron-days}")
    private final int periodicCronDays;
    @Value("${feature.stats-scheduling.historical-cron-days}")
    private final int historicalCronDays;


    /**
     * Computes the historical snapshot for yesterday at 01:00 UTC.
     * Cron configurable via {@code application.scheduling.survey-unit-stats-cron}.
     */
    @Scheduled(cron = "${feature.stats-scheduling.periodic-cron-refresh:0 */30 * * * *}")
    public void computePeriodicSnapshot() {
        computeSnapshots(LocalDate.now(clock), periodicCronDays);
    }

    /**
     * Computes the historical snapshot for the last days defined in property feature.stats-scheduling.historical-cron-days.
     * Cron configurable via {@code application.scheduling.historical-cron}.
     */
    @Scheduled(cron = "${feature.stats-scheduling.historical-cron-refresh:0 0 1 * * *}")
    public void computeHistoricalSnapshot() {
        computeSnapshots(LocalDate.now(clock).minusDays(1), historicalCronDays);
    }

    private void computeSnapshots(LocalDate startDate, int numberOfDays) {
        for (int i = 0; i < numberOfDays; i++) {
            LocalDate date = startDate.minusDays(i);
            log.info("Scheduled snapshot computation for {}", date);
            campaignProgressBatch.run(date);
        }
    }
}