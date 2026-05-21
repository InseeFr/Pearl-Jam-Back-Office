package fr.insee.pearljam.api.reporting.export.campaignorganization;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignOrganizationCsvPresenterTest {

    private final CampaignOrganizationCsvPresenter presenter = new CampaignOrganizationCsvPresenter();

    @Test
    @DisplayName("Returns NOT_AFFECTED and TOTAL_SITE rows when no interviewers")
    void shouldReturnDefaultRows_whenNoInterviewers() {
        CampaignDailyStats stats = buildCampaignDailyStats(10L, 5L);
        CampaignVisibility campaign = buildCampaignVisibility();

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(), System.currentTimeMillis()
        );

        assertThat(csv.campaignId()).isEqualTo("camp-1");
        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values()).containsExactly(
                CampaignOrganizationCsv.NOT_AFFECTED, "", "5"
        );
        assertThat(csv.rows().get(1).values()).containsExactly(
                CampaignOrganizationCsv.TOTAL_SITE, "", "15"
        );
    }

    @Test
    @DisplayName("Maps interviewer to row with full name, id and allocated count")
    void shouldMapInterviewerToRow() {
        CampaignDailyStats stats = buildCampaignDailyStats(10L, 5L);
        CampaignVisibility campaign = buildCampaignVisibility();
        InterviewerDailyStats interviewer = buildInterviewerDailyStats("ID001", "John", "Doe", 10L);

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(interviewer), System.currentTimeMillis()
        );

        assertThat(csv.campaignId()).isEqualTo("camp-1");
        assertThat(csv.rows()).hasSize(3);
        assertThat(csv.rows().get(0).values()).containsExactly("John Doe", "ID001", "10");
        assertThat(csv.rows().get(1).values()).containsExactly(
                CampaignOrganizationCsv.NOT_AFFECTED, "", "5"
        );
        assertThat(csv.rows().get(2).values()).containsExactly(
                CampaignOrganizationCsv.TOTAL_SITE, "", "15"
        );
    }

    @Test
    @DisplayName("Maps multiple interviewers to rows")
    void shouldMapMultipleInterviewers() {
        CampaignDailyStats stats = buildCampaignDailyStats(20L, 3L);
        CampaignVisibility campaign = buildCampaignVisibility();
        InterviewerDailyStats interviewer1 = buildInterviewerDailyStats("ID001", "Alice", "Smith", 5L);
        InterviewerDailyStats interviewer2 = buildInterviewerDailyStats("ID002", "Bob", "Jones", 8L);

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(interviewer1, interviewer2), System.currentTimeMillis()
        );

        assertThat(csv.campaignId()).isEqualTo("camp-1");
        assertThat(csv.rows()).hasSize(4);
        assertThat(csv.rows().get(0).values()).containsExactly("Alice Smith", "ID001", "5");
        assertThat(csv.rows().get(1).values()).containsExactly("Bob Jones", "ID002", "8");
        assertThat(csv.rows().get(2).values()).containsExactly(
                CampaignOrganizationCsv.NOT_AFFECTED, "", "3"
        );
        assertThat(csv.rows().get(3).values()).containsExactly(
                CampaignOrganizationCsv.TOTAL_SITE, "", "23"
        );
    }

    @Test
    @DisplayName("Produces rows whose size matches the header size")
    void shouldHaveRowSizeMatchingHeaderSize() {
        CampaignDailyStats stats = buildCampaignDailyStats(10L, 5L);
        CampaignVisibility campaign = buildCampaignVisibility();
        InterviewerDailyStats interviewer = buildInterviewerDailyStats("ID001", "John", "Doe", 10L);

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(interviewer), System.currentTimeMillis()
        );

        assertThat(csv.headers().values()).hasSize(3);
        for (CsvRow row : csv.rows()) {
            assertThat(row.values()).hasSameSizeAs(csv.headers().values());
        }
    }

    private static CampaignDailyStats buildCampaignDailyStats(Long allocatedFromStates, Long unaffectedCount) {
        CampaignDailyStats stats = new CampaignDailyStats();
        if (allocatedFromStates != null && allocatedFromStates > 0) {
            stats.setNnsStateCount(allocatedFromStates);
        }
        if (unaffectedCount != null) {
            stats.setUnaffectedCount(unaffectedCount);
        }
        return stats;
    }

    private static CampaignVisibility buildCampaignVisibility() {
        return new CampaignVisibility("camp-1", "Test Campaign", "email@test.com", 1L, 1L, 1L, 1L, 1L, 1L);
    }

    private static InterviewerDailyStats buildInterviewerDailyStats(
            String interviewerId, String firstName, String lastName, Long allocatedCount) {
        InterviewerDailyStats interviewer = new InterviewerDailyStats();
        interviewer.setInterviewerId(interviewerId);
        interviewer.setInterviewerFirstName(firstName);
        interviewer.setInterviewerLastName(lastName);
        if (allocatedCount != null && allocatedCount > 0) {
            interviewer.setNnsStateCount(allocatedCount);
        }
        return interviewer;
    }
}
