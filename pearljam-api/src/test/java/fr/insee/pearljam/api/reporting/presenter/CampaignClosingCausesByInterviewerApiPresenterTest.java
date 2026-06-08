package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.Interviewer;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.Interviewer.SurveyUnitsResponse;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.OrganizationUnitSite;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampaignClosingCausesByInterviewerApiPresenterTest {

    private CampaignClosingCausesByInterviewerPresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = new CampaignClosingCausesByInterviewerPresenter();
    }

    // --- Helpers ---

    private InterviewerDailyStats mockInterviewer(
            String id, String firstName, String lastName,
            long allocated,
            long npa, long npi, long npx, long row, long total
    ) {
        InterviewerDailyStats stats = mock(InterviewerDailyStats.class);
        when(stats.getInterviewerId()).thenReturn(id);
        when(stats.getInterviewerFirstName()).thenReturn(firstName);
        when(stats.getInterviewerLastName()).thenReturn(lastName);
        when(stats.getAllocatedCount()).thenReturn(allocated);
        when(stats.getNpaClosingCauseCount()).thenReturn(npa);
        when(stats.getNpiClosingCauseCount()).thenReturn(npi);
        when(stats.getNpxClosingCauseCount()).thenReturn(npx);
        when(stats.getRowClosingCauseCount()).thenReturn(row);
        when(stats.getTotalClosingCauses()).thenReturn(total);
        return stats;
    }

    private CampaignDailyStats mockCampaignStats(long allocated, long totalClosing) {
        CampaignDailyStats stats = mock(CampaignDailyStats.class);
        when(stats.getAllocatedCount()).thenReturn(allocated);
        when(stats.getTotalClosingCauses()).thenReturn(totalClosing);
        return stats;
    }

    // --- Tests ---

    @Test
    void present_shouldReturnSingleInterviewerWithCorrectFields() {
        InterviewerDailyStats interviewerStats = mockInterviewer(
                "int-01", "Alice", "Martin",
                100L,
                5L, 3L, 2L, 1L, 11L
        );
        CampaignDailyStats siteStats = mockCampaignStats(0L, 20L);
        CampaignDailyStats campaignStats = mockCampaignStats(200L, 40L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(interviewerStats), siteStats, campaignStats);

        assertThat(result.interviewers()).hasSize(1);

        Interviewer interviewer = result.interviewers().getFirst();
        assertThat(interviewer.interviewerId()).isEqualTo("int-01");
        assertThat(interviewer.interviewerLabel()).isEqualTo("Alice Martin");

        SurveyUnitsResponse su = interviewer.surveyUnits();
        assertThat(su.allocated()).isEqualTo(100L);

        SurveyUnitsResponse.ClosingCauseResponse cc = su.closingCauses();
        assertThat(cc.interviewerAbsence()).isEqualTo(5L);
        assertThat(cc.notProcessedByInterviewer()).isEqualTo(3L);
        assertThat(cc.exceptionalReason()).isEqualTo(2L);
        assertThat(cc.rightOfWithdrawal()).isEqualTo(1L);
        assertThat(cc.total()).isEqualTo(11L);
    }

    @Test
    void present_shouldReturnMultipleInterviewers() {
        InterviewerDailyStats int1 = mockInterviewer("int-01", "Alice", "Martin", 100L, 1L, 2L, 3L, 4L, 10L);
        InterviewerDailyStats int2 = mockInterviewer("int-02", "Bob", "Dupont", 50L, 0L, 1L, 0L, 2L, 3L);
        CampaignDailyStats siteStats = mockCampaignStats(0L, 13L);
        CampaignDailyStats campaignStats = mockCampaignStats(150L, 13L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(int1, int2), siteStats, campaignStats);

        assertThat(result.interviewers()).hasSize(2);
        assertThat(result.interviewers().get(0).interviewerId()).isEqualTo("int-01");
        assertThat(result.interviewers().get(1).interviewerId()).isEqualTo("int-02");
        assertThat(result.interviewers().get(1).interviewerLabel()).isEqualTo("Bob Dupont");
    }

    @Test
    void present_shouldReturnEmptyInterviewerListWhenNoneProvided() {
        CampaignDailyStats siteStats = mockCampaignStats(0L, 0L);
        CampaignDailyStats campaignStats = mockCampaignStats(0L, 0L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(), siteStats, campaignStats);

        assertThat(result.interviewers()).isEmpty();
    }

    @Test
    void present_shouldPopulateSiteWithCampaignAllocatedAndSiteClosingCauses() {
        CampaignDailyStats siteStats = mockCampaignStats(0L, 42L);
        CampaignDailyStats campaignStats = mockCampaignStats(300L, 0L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(), siteStats, campaignStats);

        OrganizationUnitSite site = result.site();
        assertThat(site).isNotNull();
        assertThat(site.surveyUnits().allocated()).isEqualTo(300L);
        assertThat(site.surveyUnits().closingCauses().total()).isEqualTo(42L);
    }

    @Test
    void present_shouldBuildInterviewerLabelFromFirstAndLastName() {
        InterviewerDailyStats stats = mockInterviewer("id-99", "Jean", "Dupuis", 10L, 0L, 0L, 0L, 0L, 0L);
        CampaignDailyStats siteStats = mockCampaignStats(0L, 0L);
        CampaignDailyStats campaignStats = mockCampaignStats(10L, 0L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(stats), siteStats, campaignStats);

        assertThat(result.interviewers().get(0).interviewerLabel()).isEqualTo("Jean Dupuis");
    }

    @Test
    void present_shouldHandleZeroCountsGracefully() {
        InterviewerDailyStats stats = mockInterviewer("int-zero", "Zero", "Values", 0L, 0L, 0L, 0L, 0L, 0L);
        CampaignDailyStats siteStats = mockCampaignStats(0L, 0L);
        CampaignDailyStats campaignStats = mockCampaignStats(0L, 0L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(stats), siteStats, campaignStats);

        Interviewer interviewer = result.interviewers().get(0);
        assertThat(interviewer.surveyUnits().allocated()).isZero();
        assertThat(interviewer.surveyUnits().closingCauses().total()).isZero();
        assertThat(result.site().surveyUnits().allocated()).isZero();
        assertThat(result.site().surveyUnits().closingCauses().total()).isZero();
    }

    @Test
    void present_shouldUseSiteStatsForSiteClosingCausesNotCampaignStats() {
        // siteStats.totalClosingCauses and campaignStats.totalClosingCauses differ
        // to confirm the correct source is used for each field
        CampaignDailyStats siteStats = mockCampaignStats(0L, 77L);
        CampaignDailyStats campaignStats = mockCampaignStats(500L, 99L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(), siteStats, campaignStats);

        // allocated comes from campaignStats
        assertThat(result.site().surveyUnits().allocated()).isEqualTo(500L);
        // closing cause total comes from siteStats
        assertThat(result.site().surveyUnits().closingCauses().total()).isEqualTo(77L);
    }
}