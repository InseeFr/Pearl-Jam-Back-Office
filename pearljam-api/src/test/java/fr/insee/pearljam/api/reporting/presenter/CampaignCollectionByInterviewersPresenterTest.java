package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignCollectionByInterviewersPresenterTest {

    private final CampaignCollectionByInterviewersPresenter presenter = new CampaignCollectionByInterviewersPresenter();

    @Test
    void shouldMapInterviewerAndCampaignStatsToCollectionResponse() {
        InterviewerDailyStats interviewerStats = ReportingPresenterTestData.interviewerStats("Jane", "Doe");
        CampaignDailyStats siteStats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 5L);
        CampaignDailyStats campaignStats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 42L);

        CampaignCollectionByInterviewersResponse result = presenter.present(List.of(interviewerStats), siteStats, campaignStats);

        assertThat(result.interviewers()).singleElement().satisfies(interviewer -> {
            assertThat(interviewer.interviewerLabel()).isEqualTo("Jane Doe");
            assertThat(interviewer.allocated()).isEqualTo(interviewerStats.getAllocatedCount());
            assertThat(interviewer.rates().waste()).isEqualTo(interviewerStats.getWasteRate());
            assertThat(interviewer.outcomes().outOfScope()).isEqualTo(interviewerStats.getOutOfScopeContactOutcomes());
            assertThat(interviewer.closingCauses().otherReasons()).isEqualTo(interviewerStats.getOtherReasonClosingCauses());
        });
        assertThat(result.site().allocated()).isEqualTo(siteStats.getAllocatedCount());
        assertThat(result.site().outcomes().total()).isEqualTo(siteStats.getTotalContactOutcomes());
        assertThat(result.campaign().allocated()).isEqualTo(campaignStats.getAllocatedCount());
        assertThat(result.campaign().unaffected()).isEqualTo(42L);
    }
}
