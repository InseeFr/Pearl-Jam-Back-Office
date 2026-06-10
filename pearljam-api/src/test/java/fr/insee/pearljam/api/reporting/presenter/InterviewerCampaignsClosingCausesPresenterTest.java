package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterviewerCampaignsClosingCausesPresenterTest {

    private final InterviewerCampaignsClosingCausesPresenter presenter =
            new InterviewerCampaignsClosingCausesPresenter();

    private static InterviewerCampaignDailyStats mockStats(
            String campaignId,
            long allocatedCount,
            long npa,
            long npi,
            long npx,
            long row,
            long total) {
        InterviewerCampaignDailyStats stats = mock(InterviewerCampaignDailyStats.class);
        when(stats.getCampaignId()).thenReturn(campaignId);
        when(stats.getAllocatedCount()).thenReturn(allocatedCount);
        when(stats.getNpaClosingCauseCount()).thenReturn(npa);
        when(stats.getNpiClosingCauseCount()).thenReturn(npi);
        when(stats.getNpxClosingCauseCount()).thenReturn(npx);
        when(stats.getRowClosingCauseCount()).thenReturn(row);
        when(stats.getTotalClosingCauses()).thenReturn(total);
        return stats;
    }

    @Test
    void present_shouldReturnEmptyList_whenStatsIsEmpty() {
        assertThat(presenter.present(List.of())).isEmpty();
    }

    @Test
    void present_shouldMapStatsToResponse() {
        //Given
        InterviewerCampaignDailyStats stats = mockStats("CAMPAIGN-1", 10L, 1L, 2L, 3L, 4L, 10L);

        //When
        List<InterviewerCampaignsClosingCausesResponse> result = presenter.present(List.of(stats));

        //Then
        assertThat(result).hasSize(1);
        InterviewerCampaignsClosingCausesResponse response = result.getFirst();
        assertThat(response.campaignLabel()).isEqualTo("CAMPAIGN-1");
        assertThat(response.totalAllocated()).isEqualTo(10L);

        InterviewerCampaignsClosingCausesResponse.ClosingCauseResponse closing = response.closingCauses();
        assertThat(closing.interviewerAbsence()).isEqualTo(1L);
        assertThat(closing.notProcessedByInterviewer()).isEqualTo(2L);
        assertThat(closing.exceptionalReason()).isEqualTo(3L);
        assertThat(closing.rightOfWithdrawal()).isEqualTo(4L);
        assertThat(closing.total()).isEqualTo(10L);
    }

    @Test
    void present_shouldMapMultipleStats() {
        //Given
        InterviewerCampaignDailyStats stats1 = mockStats("CAMPAIGN-1", 5L, 1L, 1L, 1L, 1L, 4L);
        InterviewerCampaignDailyStats stats2 = mockStats("CAMPAIGN-2", 20L, 5L, 6L, 7L, 0L, 18L);

        //When
        List<InterviewerCampaignsClosingCausesResponse> result = presenter.present(List.of(stats1, stats2));

        //Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).campaignLabel()).isEqualTo("CAMPAIGN-1");
        assertThat(result.get(1).campaignLabel()).isEqualTo("CAMPAIGN-2");
        assertThat(result.get(1).closingCauses().total()).isEqualTo(18L);
    }
}