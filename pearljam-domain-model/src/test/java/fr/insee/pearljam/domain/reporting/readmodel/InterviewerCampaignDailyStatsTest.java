package fr.insee.pearljam.domain.reporting.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerCampaignDailyStatsTest {

    @Test
    @DisplayName("getAllocatedCount sums the 13 state counters and excludes any unaffected count")
    void allocatedCount_shouldSumStateCountersWithoutUnaffected() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        long allocated = stats.getAllocatedCount();

        // Then
        assertThat(allocated).isEqualTo(100L);
    }

    @Test
    @DisplayName("getCollectionRate equals ina * 100 / allocated")
    void collectionRate_shouldDivideInaByAllocated() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        float rate = stats.getCollectionRate();

        // Then
        assertThat(rate).isEqualTo(30f);
    }

    @Test
    @DisplayName("getWasteRate equals (ref + imp + npi) * 100 / allocated")
    void wasteRate_shouldDivideRefImpNpiByAllocated() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        float rate = stats.getWasteRate();

        // Then
        assertThat(rate).isEqualTo(10f);
    }

    @Test
    @DisplayName("getOutOfScopeRate equals (outOfScopeOutcomes + npx + row) * 100 / allocated")
    void outOfScopeRate_shouldDivideOutOfScopeOutcomesAndNpxRowByAllocated() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        float rate = stats.getOutOfScopeRate();

        // Then
        assertThat(rate).isEqualTo(10f);
    }

    @Test
    @DisplayName("getProgressStateRate equals (tbr + fin + clo) * 100 / allocated")
    void progressStateRate_shouldDivideTbrFinCloByAllocated() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        float rate = stats.getProgressStateRate();

        // Then
        assertThat(rate).isEqualTo(40f);
    }

    @Test
    @DisplayName("getToProcessInterviewerStateCount sums vic + prc + aoc + aps + ins + wft")
    void toProcessInterviewerStateCount_shouldSumVicPrcAocApsInsWft() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        long count = stats.getToProcessInterviewerStateCount();

        // Then
        assertThat(count).isEqualTo(35L);
    }

    @Test
    @DisplayName("getCompletedStateCount sums fin + clo")
    void completedStateCount_shouldSumFinClo() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        long count = stats.getCompletedStateCount();

        // Then
        assertThat(count).isEqualTo(30L);
    }

    @Test
    @DisplayName("getInProgressStateCount sums prc + aoc + aps + ins")
    void inProgressStateCount_shouldSumPrcAocApsIns() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        long count = stats.getInProgressStateCount();

        // Then
        assertThat(count).isEqualTo(20L);
    }

    @Test
    @DisplayName("getOutOfScopeContactOutcomes sums ucd + utr + ala + nuh + duk + noa")
    void outOfScopeContactOutcomes_shouldSumUcdUtrAlaNuhDukNoa() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        long count = stats.getOutOfScopeContactOutcomes();

        // Then
        assertThat(count).isEqualTo(6L);
    }

    @Test
    @DisplayName("getTotalContactOutcomes sums ina + imp + ref + outOfScope contacts")
    void totalContactOutcomes_shouldSumInaImpRefAndOutOfScope() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        long count = stats.getTotalContactOutcomes();

        // Then
        assertThat(count).isEqualTo(44L);
    }

    @Test
    @DisplayName("getOtherReasonClosingCauses sums npi + npx + row")
    void otherReasonClosingCauses_shouldSumNpiNpxRow() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        long count = stats.getOtherReasonClosingCauses();

        // Then
        assertThat(count).isEqualTo(6L);
    }

    @Test
    @DisplayName("getTotalClosingCauses sums npa and other closing causes")
    void totalClosingCauses_shouldSumNpaAndOtherReasons() {
        // Given
        InterviewerCampaignDailyStats stats = populated();

        // When
        long count = stats.getTotalClosingCauses();

        // Then
        assertThat(count).isEqualTo(10L);
    }

    @Test
    @DisplayName("All rates return 0 when allocated is 0")
    void allRates_shouldBeZero_whenAllocatedIsZero() {
        // Given
        InterviewerCampaignDailyStats stats = new InterviewerCampaignDailyStats();
        stats.setInaContactOutcomeCount(5);
        stats.setRefContactOutcomeCount(5);
        stats.setNpiClosingCauseCount(5);

        // When / Then
        assertThat(stats.getAllocatedCount()).isZero();
        assertThat(stats.getCollectionRate()).isZero();
        assertThat(stats.getWasteRate()).isZero();
        assertThat(stats.getOutOfScopeRate()).isZero();
        assertThat(stats.getProgressStateRate()).isZero();
    }

    private InterviewerCampaignDailyStats populated() {
        InterviewerCampaignDailyStats stats = InterviewerCampaignDailyStats.empty("c1", "Campaign 1");
        // 13 state counters → allocated = 100
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
        stats.setNvmStateCount(99);  // not allocated
        stats.setNvaStateCount(99);  // not allocated

        stats.setInaContactOutcomeCount(30);
        stats.setRefContactOutcomeCount(5);
        stats.setImpContactOutcomeCount(3);
        stats.setUcdContactOutcomeCount(1);
        stats.setUtrContactOutcomeCount(1);
        stats.setAlaContactOutcomeCount(1);
        stats.setNuhContactOutcomeCount(1);
        stats.setDukContactOutcomeCount(1);
        stats.setNoaContactOutcomeCount(1);

        stats.setNpaClosingCauseCount(4);
        stats.setNpiClosingCauseCount(2);
        stats.setNpxClosingCauseCount(2);
        stats.setRowClosingCauseCount(2);
        return stats;
    }
}
