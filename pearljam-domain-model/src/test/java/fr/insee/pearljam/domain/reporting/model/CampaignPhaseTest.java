package fr.insee.pearljam.domain.reporting.model;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CampaignPhaseTest {

    private static final long MANAGEMENT_START = 1_000L;
    private static final long COLLECTION_START = 2_000L;
    private static final long COLLECTION_END = 3_000L;
    private static final long END_DATE = 4_000L;

    @Test
    void shouldReturnInitialAssignment_betweenManagementAndCollectionStart() {
        long current = 1_500L; // between managementStartDate and collectionStartDate
        CampaignPhase phase = CampaignPhase.fromDates(
                current, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE
        );
        assertThat(phase).isEqualTo(CampaignPhase.INITIAL_ASSIGNMENT);
    }

    @Test
    void shouldReturnCollectionInProgress_betweenCollectionStartAndCollectionEnd() {
        long current = 2_500L; // between collectionStartDate and collectionEndDate
        CampaignPhase phase = CampaignPhase.fromDates(
                current, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE
        );
        assertThat(phase).isEqualTo(CampaignPhase.COLLECTION_IN_PROGRESS);
    }

    @Test
    void shouldReturnCollectionCompleted_betweenCollectionEndAndEndDate() {
        long current = 3_500L; // between collectionEndDate and endDate
        CampaignPhase phase = CampaignPhase.fromDates(
                current, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE
        );
        assertThat(phase).isEqualTo(CampaignPhase.COLLECTION_COMPLETED);
    }

    @Test
    void shouldThrowBeforeManagementStart() {
        long current = 500L; // before managementStartDate
        assertThatThrownBy(() ->
                CampaignPhase.fromDates(current, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not visible yet");
    }

    @Test
    void shouldThrowAfterEndDate() {
        long current = 5_000L; // après endDate
        assertThatThrownBy(() ->
                CampaignPhase.fromDates(current, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already terminated");
    }

    @Test
    void shouldHandleExactBoundaryValues() {
        // exactly managementStartDate → INITIAL_ASSIGNMENT
        assertThat(CampaignPhase.fromDates(MANAGEMENT_START, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE))
                .isEqualTo(CampaignPhase.INITIAL_ASSIGNMENT);

        // exactly collectionStartDate → COLLECTION_IN_PROGRESS
        assertThat(CampaignPhase.fromDates(COLLECTION_START, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE))
                .isEqualTo(CampaignPhase.COLLECTION_IN_PROGRESS);

        // exactly collectionEndDate → COLLECTION_COMPLETED
        assertThat(CampaignPhase.fromDates(COLLECTION_END, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE))
                .isEqualTo(CampaignPhase.COLLECTION_COMPLETED);

        // exactly endDate → COLLECTION_COMPLETED
        assertThat(CampaignPhase.fromDates(END_DATE, MANAGEMENT_START, COLLECTION_START, COLLECTION_END, END_DATE))
                .isEqualTo(CampaignPhase.COLLECTION_COMPLETED);
    }
}