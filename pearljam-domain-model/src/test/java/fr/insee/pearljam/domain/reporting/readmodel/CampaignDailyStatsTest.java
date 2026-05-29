package fr.insee.pearljam.domain.reporting.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignDailyStatsTest {

    @Test
    @DisplayName("getAllocatedCount includes unaffectedCount in the sum")
    void allocatedCount_shouldIncludeUnaffected() {
        // Given
        CampaignDailyStats stats = populatedWithStateCountersSummingTo100();
        stats.setUnaffectedCount(25);

        // When
        long allocated = stats.getAllocatedCount();

        // Then
        assertThat(allocated).isEqualTo(125L);
    }

    @Test
    @DisplayName("getCollectionRate uses allocated including unaffected as denominator")
    void collectionRate_shouldUseAllocatedIncludingUnaffectedAsDenominator() {
        // Given
        CampaignDailyStats stats = populatedWithStateCountersSummingTo100();
        stats.setUnaffectedCount(25);
        stats.setInaContactOutcomeCount(30);

        // When
        float rate = stats.getCollectionRate();

        // Then
        assertThat(rate).isEqualTo(24f);
    }

    @Test
    @DisplayName("getWasteRate uses allocated including unaffected as denominator")
    void wasteRate_shouldUseAllocatedIncludingUnaffectedAsDenominator() {
        // Given
        CampaignDailyStats stats = populatedWithStateCountersSummingTo100();
        stats.setUnaffectedCount(25);
        stats.setRefContactOutcomeCount(5);
        stats.setImpContactOutcomeCount(3);
        stats.setNpiClosingCauseCount(2);

        // When
        float rate = stats.getWasteRate();

        // Then
        assertThat(rate).isEqualTo(8.0f);
    }

    @Test
    @DisplayName("getOutOfScopeRate uses allocated including unaffected as denominator")
    void outOfScopeRate_shouldUseAllocatedIncludingUnaffectedAsDenominator() {
        // Given
        CampaignDailyStats stats = populatedWithStateCountersSummingTo100();
        stats.setUnaffectedCount(25);
        stats.setUcdContactOutcomeCount(1);
        stats.setUtrContactOutcomeCount(1);
        stats.setAlaContactOutcomeCount(1);
        stats.setNuhContactOutcomeCount(1);
        stats.setDukContactOutcomeCount(1);
        stats.setNoaContactOutcomeCount(1);
        stats.setNpxClosingCauseCount(2);
        stats.setRowClosingCauseCount(2);

        // When
        float rate = stats.getOutOfScopeRate();

        // Then
        assertThat(rate).isEqualTo(8f);
    }

    @Test
    @DisplayName("getProgressStateRate uses allocated including unaffected as denominator")
    void progressStateRate_shouldUseAllocatedIncludingUnaffectedAsDenominator() {
        // Given
        CampaignDailyStats stats = populatedWithStateCountersSummingTo100();
        stats.setUnaffectedCount(25);

        // When
        float rate = stats.getProgressStateRate();

        // Then
        assertThat(rate).isEqualTo(32f);
    }

    @Test
    @DisplayName("getAllocatedCount equals unaffectedCount when no state counter is set")
    void allocatedCount_shouldEqualUnaffected_whenNoStateCounters() {
        // Given
        CampaignDailyStats stats = CampaignDailyStats.empty("c1");
        stats.setUnaffectedCount(50);

        // When
        long allocated = stats.getAllocatedCount();

        // Then
        assertThat(allocated).isEqualTo(50L);
    }

    @Test
    @DisplayName("All rates return 0 when allocated and unaffected are both 0")
    void allRates_shouldBeZero_whenAllocatedAndUnaffectedAreZero() {
        // Given
        CampaignDailyStats stats = CampaignDailyStats.empty("c1");
        stats.setInaContactOutcomeCount(5);
        stats.setRefContactOutcomeCount(5);

        // When / Then
        assertThat(stats.getAllocatedCount()).isZero();
        assertThat(stats.getCollectionRate()).isZero();
        assertThat(stats.getWasteRate()).isZero();
        assertThat(stats.getOutOfScopeRate()).isZero();
        assertThat(stats.getProgressStateRate()).isZero();
    }

    private CampaignDailyStats populatedWithStateCountersSummingTo100() {
        CampaignDailyStats stats = CampaignDailyStats.empty("c1", "Campaign 1");
        // 13 state counters → allocatedFromStateCounts() = 100
        stats.setNnsStateCount(5);
        stats.setAnvStateCount(5);
        stats.setVinStateCount(5);
        stats.setVicStateCount(5);
        stats.setPrcStateCount(5);
        stats.setAocStateCount(5);
        stats.setApsStateCount(5);
        stats.setInsStateCount(5);
        stats.setWftStateCount(10);
        stats.setWfsStateCount(10);
        stats.setTbrStateCount(10);
        stats.setFinStateCount(20);
        stats.setCloStateCount(10);
        return stats;
    }
}
