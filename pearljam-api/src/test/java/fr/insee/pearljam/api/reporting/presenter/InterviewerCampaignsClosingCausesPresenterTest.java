package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse.InterviewerCampaignSurveyUnits;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse.InterviewerCampaignsTotalSurveyUnit;
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
    void present_shouldReturnEmptyCampaignListAndZeroedTotals_whenStatsIsEmpty() {
        InterviewerCampaignsClosingCausesResponse result = presenter.present(List.of());

        assertThat(result.interviewerCampaignSurveyUnits()).isEmpty();

        InterviewerCampaignsTotalSurveyUnit total = result.interviewerCampaignsTotalSurveyUnit();
        assertThat(total.allocated()).isZero();
        assertThat(total.closingCauses().interviewerAbsence()).isZero();
        assertThat(total.closingCauses().notProcessedByInterviewer()).isZero();
        assertThat(total.closingCauses().exceptionalReason()).isZero();
        assertThat(total.closingCauses().rightOfWithdrawal()).isZero();
        assertThat(total.closingCauses().total()).isZero();
    }

    @Test
    void present_shouldMapStatsToResponse() {
        // Given
        InterviewerCampaignDailyStats stats = mockStats("CAMPAIGN-1", 10L, 1L, 2L, 3L, 4L, 10L);

        // When
        InterviewerCampaignsClosingCausesResponse result = presenter.present(List.of(stats));

        // Then — per-campaign entry
        assertThat(result.interviewerCampaignSurveyUnits()).hasSize(1);
        InterviewerCampaignSurveyUnits campaignEntry = result.interviewerCampaignSurveyUnits().getFirst();
        assertThat(campaignEntry.campaignLabel()).isEqualTo("CAMPAIGN-1");
        assertThat(campaignEntry.allocated()).isEqualTo(10L);

        InterviewerCampaignSurveyUnits.ClosingCauseResponse campaignClosing = campaignEntry.closingCauses();
        assertThat(campaignClosing.interviewerAbsence()).isEqualTo(1L);
        assertThat(campaignClosing.notProcessedByInterviewer()).isEqualTo(2L);
        assertThat(campaignClosing.exceptionalReason()).isEqualTo(3L);
        assertThat(campaignClosing.rightOfWithdrawal()).isEqualTo(4L);
        assertThat(campaignClosing.total()).isEqualTo(10L);

        // Then — totals (single campaign, same values)
        InterviewerCampaignsTotalSurveyUnit total = result.interviewerCampaignsTotalSurveyUnit();
        assertThat(total.allocated()).isEqualTo(10L);

        InterviewerCampaignsTotalSurveyUnit.ClosingCauseResponse totalClosing = total.closingCauses();
        assertThat(totalClosing.interviewerAbsence()).isEqualTo(1L);
        assertThat(totalClosing.notProcessedByInterviewer()).isEqualTo(2L);
        assertThat(totalClosing.exceptionalReason()).isEqualTo(3L);
        assertThat(totalClosing.rightOfWithdrawal()).isEqualTo(4L);
        assertThat(totalClosing.total()).isEqualTo(10L);
    }

    @Test
    void present_shouldMapMultipleStatsAndAggregateTotals() {
        // Given
        InterviewerCampaignDailyStats stats1 = mockStats("CAMPAIGN-1", 5L, 1L, 1L, 1L, 1L, 4L);
        InterviewerCampaignDailyStats stats2 = mockStats("CAMPAIGN-2", 20L, 5L, 6L, 7L, 0L, 18L);

        // When
        InterviewerCampaignsClosingCausesResponse result = presenter.present(List.of(stats1, stats2));

        // Then — per-campaign entries
        assertThat(result.interviewerCampaignSurveyUnits()).hasSize(2);
        assertThat(result.interviewerCampaignSurveyUnits().get(0).campaignLabel()).isEqualTo("CAMPAIGN-1");
        assertThat(result.interviewerCampaignSurveyUnits().get(1).campaignLabel()).isEqualTo("CAMPAIGN-2");
        assertThat(result.interviewerCampaignSurveyUnits().get(1).closingCauses().total()).isEqualTo(18L);

        // Then — aggregated totals across both campaigns
        InterviewerCampaignsTotalSurveyUnit total = result.interviewerCampaignsTotalSurveyUnit();
        assertThat(total.allocated()).isEqualTo(25L);

        InterviewerCampaignsTotalSurveyUnit.ClosingCauseResponse totalClosing = total.closingCauses();
        assertThat(totalClosing.interviewerAbsence()).isEqualTo(6L);
        assertThat(totalClosing.notProcessedByInterviewer()).isEqualTo(7L);
        assertThat(totalClosing.exceptionalReason()).isEqualTo(8L);
        assertThat(totalClosing.rightOfWithdrawal()).isEqualTo(1L);
        assertThat(totalClosing.total()).isEqualTo(22L);
    }
}