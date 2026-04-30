package fr.insee.pearljam.api.reporting.export.campaignorganization;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignOrganizationCsvPresenterTest {

    private CampaignOrganizationCsvPresenter presenter;

    @BeforeEach
    void setup() {
        presenter = new CampaignOrganizationCsvPresenter();
    }

    @Test
    @DisplayName("Should present data with no interviewers")
    void shouldPresentDataWithNoInterviewers() {
        CampaignDailyStats stats = new CampaignDailyStats(10L, 5L);
        CampaignVisibility campaign = new CampaignVisibility(
                "camp-1", "Test Campaign", "email@test.com",
                1L, 1L, 1L, 1L, 1L
        );

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(), System.currentTimeMillis()
        );

        assertThat(csv.campaignLabel()).isEqualTo("Test Campaign");
        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values()).containsExactly(
                CampaignOrganizationCsv.NOT_AFFECTED, "", "5"
        );
        assertThat(csv.rows().get(1).values()).containsExactly(
                CampaignOrganizationCsv.TOTAL_SITE, "", "10"
        );
    }

    @Test
    @DisplayName("Should present data with one interviewer")
    void shouldPresentDataWithOneInterviewer() {
        CampaignDailyStats stats = new CampaignDailyStats(15L, 5L);
        CampaignVisibility campaign = new CampaignVisibility(
                "camp-1", "Test Campaign", "email@test.com",
                1L, 1L, 1L, 1L, 1L
        );
        InterviewerDailyStats interviewer = new InterviewerDailyStats(
                "ID001", "John", "Doe", 10L
        );

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(interviewer), System.currentTimeMillis()
        );

        assertThat(csv.campaignLabel()).isEqualTo("Test Campaign");
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
    @DisplayName("Should present data with multiple interviewers")
    void shouldPresentDataWithMultipleInterviewers() {
        CampaignDailyStats stats = new CampaignDailyStats(23L, 3L);
        CampaignVisibility campaign = new CampaignVisibility(
                "camp-1", "Test Campaign", "email@test.com",
                1L, 1L, 1L, 1L, 1L
        );
        InterviewerDailyStats interviewer1 = new InterviewerDailyStats(
                "ID001", "Alice", "Smith", 5L
        );
        InterviewerDailyStats interviewer2 = new InterviewerDailyStats(
                "ID002", "Bob", "Jones", 8L
        );
        InterviewerDailyStats interviewer3 = new InterviewerDailyStats(
                "ID003", "Charlie", "Brown", 10L
        );

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), 
                List.of(interviewer1, interviewer2, interviewer3), 
                System.currentTimeMillis()
        );

        assertThat(csv.campaignLabel()).isEqualTo("Test Campaign");
        assertThat(csv.rows()).hasSize(5);
        assertThat(csv.rows().get(0).values()).containsExactly("Alice Smith", "ID001", "5");
        assertThat(csv.rows().get(1).values()).containsExactly("Bob Jones", "ID002", "8");
        assertThat(csv.rows().get(2).values()).containsExactly("Charlie Brown", "ID003", "10");
        assertThat(csv.rows().get(3).values()).containsExactly(
                CampaignOrganizationCsv.NOT_AFFECTED, "", "3"
        );
        assertThat(csv.rows().get(4).values()).containsExactly(
                CampaignOrganizationCsv.TOTAL_SITE, "", "23"
        );
    }

    @Test
    @DisplayName("Should handle null counts in stats")
    void shouldHandleNullCounts() {
        CampaignDailyStats stats = new CampaignDailyStats(null, null);
        CampaignVisibility campaign = new CampaignVisibility(
                "camp-1", "Test Campaign", "email@test.com",
                1L, 1L, 1L, 1L, 1L
        );

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(), System.currentTimeMillis()
        );

        assertThat(csv.campaignLabel()).isEqualTo("Test Campaign");
        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values()).containsExactly(
                CampaignOrganizationCsv.NOT_AFFECTED, "", ""
        );
        assertThat(csv.rows().get(1).values()).containsExactly(
                CampaignOrganizationCsv.TOTAL_SITE, "", ""
        );
    }

    @Test
    @DisplayName("Should handle interviewer with null values")
    void shouldHandleInterviewerWithNullValues() {
        CampaignDailyStats stats = new CampaignDailyStats(10L, 5L);
        CampaignVisibility campaign = new CampaignVisibility(
                "camp-1", "Test Campaign", "email@test.com",
                1L, 1L, 1L, 1L, 1L
        );
        InterviewerDailyStats interviewer = new InterviewerDailyStats(
                null, null, null, null
        );

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(interviewer), System.currentTimeMillis()
        );

        assertThat(csv.campaignLabel()).isEqualTo("Test Campaign");
        assertThat(csv.rows()).hasSize(3);
        assertThat(csv.rows().get(0).values()).containsExactly(" ", "", "");
    }

    @Test
    @DisplayName("Should return correct headers")
    void shouldReturnCorrectHeaders() {
        CampaignDailyStats stats = new CampaignDailyStats(10L, 5L);
        CampaignVisibility campaign = new CampaignVisibility(
                "camp-1", "Test Campaign", "email@test.com",
                1L, 1L, 1L, 1L, 1L
        );

        CampaignOrganizationCsv csv = presenter.present(
                stats, campaign, List.of(), List.of(), System.currentTimeMillis()
        );

        CsvRow headers = csv.headers();
        assertThat(headers.values()).containsExactly(
                "Nom Prénom Enquêteur",
                "Idep Enquêteur",
                "Nombre d'UE"
        );
    }
}
