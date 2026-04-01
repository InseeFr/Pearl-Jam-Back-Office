package fr.insee.pearljam.api.reporting.scheduler;

import fr.insee.pearljam.infrastructure.persistence.reporting.batch.CampaignProgressBatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBooleanProperty(name = "${feature.stats-scheduling.enabled}")
public class CampaignProgressScheduler {

    private final CampaignProgressBatch campaignProgressBatch;

    /**
     * Computes the daily snapshot for yesterday at 01:00 UTC.
     * Cron configurable via {@code application.scheduling.survey-unit-stats-cron}.
     */
    @Scheduled(cron = "${feature.stats-scheduling.survey-unit-stats-cron:0 0 1 * * *}")
    public void computeYesterdaySnapshot() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Scheduled snapshot computation for {}", yesterday);
        campaignProgressBatch.run(yesterday);
    }
}