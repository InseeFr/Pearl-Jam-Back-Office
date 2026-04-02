package fr.insee.pearljam.infrastructure.persistence.reporting.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
class PartitionManagerTest {

    @Autowired
    private PartitionManager partitionManager;

    @Autowired
    private JdbcClient jdbc;

    @Test
    @DisplayName("Should create monthly partition for given day")
    void shouldCreatePartition() {
        LocalDate day = LocalDate.of(2020, 3, 15);

        partitionManager.ensureMonthlyPartitionExists(day);

        boolean exists = partitionExists("campaign_daily_stats_2020_03");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should create partition covering first day of month")
    void shouldCreatePartitionForFirstDayOfMonth() {
        LocalDate day = LocalDate.of(2020, 1, 1);

        partitionManager.ensureMonthlyPartitionExists(day);

        assertThat(partitionExists("campaign_daily_stats_2020_01")).isTrue();
    }

    @Test
    @DisplayName("Should create partition covering last day of month")
    void shouldCreatePartitionForLastDayOfMonth() {
        LocalDate day = LocalDate.of(2020, 12, 31);

        partitionManager.ensureMonthlyPartitionExists(day);

        assertThat(partitionExists("campaign_daily_stats_2020_12")).isTrue();
    }

    @Test
    @DisplayName("Should be idempotent — calling twice does not fail")
    void shouldBeIdempotent() {
        LocalDate day = LocalDate.of(2020, 5, 10);

        partitionManager.ensureMonthlyPartitionExists(day);
        partitionManager.ensureMonthlyPartitionExists(day);

        assertThat(partitionExists("campaign_daily_stats_2020_05")).isTrue();
    }

    @Test
    @DisplayName("Should create index on the partition")
    void shouldCreateIndex() {
        LocalDate day = LocalDate.of(2020, 7, 20);

        partitionManager.ensureMonthlyPartitionExists(day);

        boolean indexExists = indexExists("campaign_daily_stats_2020_07_idx_campaign_day_ou");
        assertThat(indexExists).isTrue();
    }

    @Test
    @DisplayName("Should create separate partitions for different months")
    void shouldCreateSeparatePartitionsForDifferentMonths() {
        partitionManager.ensureMonthlyPartitionExists(LocalDate.of(2020, 8, 1));
        partitionManager.ensureMonthlyPartitionExists(LocalDate.of(2020, 9, 1));

        assertThat(partitionExists("campaign_daily_stats_2020_08")).isTrue();
        assertThat(partitionExists("campaign_daily_stats_2020_09")).isTrue();
    }

    private boolean partitionExists(String tableName) {
        return jdbc.sql("""
            SELECT COUNT(*) FROM information_schema.tables
            WHERE table_name = :tableName AND table_schema = 'public'
            """)
                .param("tableName", tableName)
                .query(Integer.class)
                .single() > 0;
    }

    private boolean indexExists(String indexName) {
        return jdbc.sql("""
            SELECT COUNT(*) FROM pg_indexes
            WHERE indexname = :indexName AND schemaname = 'public'
            """)
                .param("indexName", indexName)
                .query(Integer.class)
                .single() > 0;
    }
}
