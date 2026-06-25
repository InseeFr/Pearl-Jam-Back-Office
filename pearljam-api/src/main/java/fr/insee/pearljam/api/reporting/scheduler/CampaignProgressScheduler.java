package fr.insee.pearljam.api.reporting.scheduler;

import fr.insee.pearljam.infrastructure.persistence.reporting.batch.CampaignProgressBatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Computes the daily snapshot for yesterday at 01:00 UTC.
     * Cron configurable via {@code application.scheduling.survey-unit-stats-cron}.
     */
    @Scheduled(cron = "${feature.stats-scheduling.today-cron:0 */30 * * * *}")
    public void computeTodayAndYesterdaySnapshot() {
        LocalDate now = LocalDate.now(clock);
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);
        log.info("Scheduled snapshot computation for today {}", now);
        campaignProgressBatch.run(now);
        log.info("Scheduled snapshot computation for yesterday {}", yesterday);
        campaignProgressBatch.run(yesterday);
    }

    /**
     * Computes the daily snapshot for the last 30 days.
     * Cron configurable via {@code application.scheduling.daily-cron}.
     */
    @Scheduled(cron = "${feature.stats-scheduling.daily-cron:0 0 1 * * *}")
    public void computeDailySnapshot() {
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);

        for (int i = 0; i < 30; i++) {
            LocalDate date = yesterday.minusDays(i);
            log.info("Scheduled daily snapshot computation for {}", date);
            campaignProgressBatch.run(date);
        }
    }
}