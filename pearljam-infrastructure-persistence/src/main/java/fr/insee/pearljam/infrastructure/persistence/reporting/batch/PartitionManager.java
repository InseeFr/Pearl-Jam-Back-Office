package fr.insee.pearljam.infrastructure.persistence.reporting.batch;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Transactional
public class PartitionManager {

    private final EntityManager em;

    public void ensureMonthlyPartitionExists(LocalDate day) {

        LocalDate start = day.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        String partitionName = buildPartitionName(start);

        createPartitionIfNotExists(partitionName, start, end);
        createIndexesIfNotExists(partitionName);
    }

    private String buildPartitionName(LocalDate start) {
        return "campaign_daily_stats_" +
                start.getYear() + "_" +
                String.format("%02d", start.getMonthValue());
    }

    private void createPartitionIfNotExists(String partitionName, LocalDate start, LocalDate end) {

        String sql = """
            CREATE TABLE IF NOT EXISTS %s
            PARTITION OF campaign_daily_stats
            FOR VALUES FROM (:start) TO (:end)
        """.formatted(partitionName);


        em.createNativeQuery(sql)
                .setParameter("start", start)
                .setParameter("end", end)
                .executeUpdate();
    }

    private void createIndexesIfNotExists(String partitionName) {

        String indexSql = """
            CREATE INDEX IF NOT EXISTS %s_idx_campaign_day_ou
            ON %s (campaign_id, day, organization_unit_id)
        """.formatted(partitionName, partitionName);

        em.createNativeQuery(indexSql).executeUpdate();
    }
}