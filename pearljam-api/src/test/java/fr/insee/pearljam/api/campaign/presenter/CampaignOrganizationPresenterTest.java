package fr.insee.pearljam.api.campaign.presenter;

import fr.insee.pearljam.api.campaign.response.CampaignOrganizationResponse;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignOrganizationPresenterTest {

    private static final long NOW_MS = 1000000L;
    private static final long MGMT_START = NOW_MS - 4000L;
    private static final long COLL_START = NOW_MS - 2000L;
    private static final long COLL_END = NOW_MS + 2000L;
    private static final long END_DATE = NOW_MS + 4000L;
    private static final long INTERV_START = NOW_MS - 3000L;
    private static final long IDENT_START = NOW_MS - 3500L;

    private CampaignOrganizationPresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = new CampaignOrganizationPresenter();
    }

    @Test
    @DisplayName("should map campaign basic information")
    void shouldMapCampaignBasicInformation() {
        CampaignVisibility campaign = createCampaignVisibility();
        CampaignDailyStats stats = createCampaignStats();

        CampaignOrganizationResponse result = presenter.present(stats, campaign, List.of(), List.of(), 0, 0, NOW_MS);

        assertThat(result.campaignId()).isEqualTo("camp-1");
        assertThat(result.campaignLabel()).isEqualTo("Test Campaign");
        assertThat(result.identificationPhaseStartDate()).isEqualTo(IDENT_START);
        assertThat(result.collectionStartDate()).isEqualTo(COLL_START);
        assertThat(result.collectionEndDate()).isEqualTo(COLL_END);
        assertThat(result.endDate()).isEqualTo(END_DATE);
    }

    @Test
    @DisplayName("should calculate COLLECTION_IN_PROGRESS phase correctly")
    void shouldCalculateCollectionInProgressPhase() {
        CampaignVisibility campaign = createCampaignVisibility();
        CampaignDailyStats stats = createCampaignStats();

        CampaignOrganizationResponse result = presenter.present(stats, campaign, List.of(), List.of(), 0, 0, NOW_MS);

        assertThat(result.phase()).isEqualTo(CampaignPhase.COLLECTION_IN_PROGRESS);
    }

    @Test
    @DisplayName("should calculate INITIAL_ASSIGNMENT phase when before collection start")
    void shouldCalculateInitialAssignmentPhase() {
        CampaignVisibility campaign = new CampaignVisibility(
                "camp-1", "Test Campaign", "test@insee.fr",
                MGMT_START, INTERV_START, IDENT_START,
                NOW_MS + 1000L,  // collection not started yet
                COLL_END, END_DATE);
        CampaignDailyStats stats = createCampaignStats();

        CampaignOrganizationResponse result = presenter.present(stats, campaign, List.of(), List.of(), 0, 0, NOW_MS);

        assertThat(result.phase()).isEqualTo(CampaignPhase.INITIAL_ASSIGNMENT);
    }

    @Test
    @DisplayName("should calculate COLLECTION_COMPLETED phase when after collection end")
    void shouldCalculateCollectionCompletedPhase() {
        CampaignVisibility campaign = new CampaignVisibility(
                "camp-1", "Test Campaign", "test@insee.fr",
                MGMT_START, INTERV_START, IDENT_START,
                NOW_MS - 3000L,  // collection started
                NOW_MS - 1000L,  // collection ended
                END_DATE);
        CampaignDailyStats stats = createCampaignStats();

        CampaignOrganizationResponse result = presenter.present(stats, campaign, List.of(), List.of(), 0, 0, NOW_MS);

        assertThat(result.phase()).isEqualTo(CampaignPhase.COLLECTION_COMPLETED);
    }

    @Test
    @DisplayName("should map referents correctly")
    void shouldMapReferents() {
        CampaignVisibility campaign = createCampaignVisibility();
        CampaignDailyStats stats = createCampaignStats();
        List<Referent> referents = List.of(
                new Referent("John", "Doe", "0101010101", "PRIMARY"),
                new Referent("Jane", "Smith", "0202020202", "SECONDARY")
        );

        CampaignOrganizationResponse result = presenter.present(stats, campaign, referents, List.of(),0, 0, NOW_MS);

        assertThat(result.referents()).hasSize(2);
        assertThat(result.referents().getFirst().firstName()).isEqualTo("John");
        assertThat(result.referents().getFirst().lastName()).isEqualTo("Doe");
        assertThat(result.referents().getFirst().phoneNumber()).isEqualTo("0101010101");
        assertThat(result.referents().getFirst().role()).isEqualTo("PRIMARY");

        assertThat(result.referents().get(1).firstName()).isEqualTo("Jane");
        assertThat(result.referents().get(1).lastName()).isEqualTo("Smith");
        assertThat(result.referents().get(1).phoneNumber()).isEqualTo("0202020202");
        assertThat(result.referents().get(1).role()).isEqualTo("SECONDARY");
    }

    @Test
    @DisplayName("should map interviewers with concatenated name")
    void shouldMapInterviewersWithConcatenatedName() {
        CampaignVisibility campaign = createCampaignVisibility();
        CampaignDailyStats stats = createCampaignStats();
        List<InterviewerDailyStats> interviewers = List.of(
                createInterviewerStats("INT1", "Alice", "Anderson", 15L),
                createInterviewerStats("INT2", "Bob", "Brown", 10L)
        );

        CampaignOrganizationResponse result = presenter.present(stats, campaign, List.of(), interviewers, 0, 0, NOW_MS);

        assertThat(result.interviewers()).hasSize(2);
        assertThat(result.interviewers().getFirst().id()).isEqualTo("INT1");
        assertThat(result.interviewers().getFirst().label()).isEqualTo("Alice Anderson");
        assertThat(result.interviewers().getFirst().surveyUnits()).isEqualTo(15L);

        assertThat(result.interviewers().get(1).id()).isEqualTo("INT2");
        assertThat(result.interviewers().get(1).label()).isEqualTo("Bob Brown");
        assertThat(result.interviewers().get(1).surveyUnits()).isEqualTo(10L);
    }

    @Test
    @DisplayName("should map survey unit counts correctly")
    void shouldMapSurveyUnitCounts() {
        CampaignVisibility campaign = createCampaignVisibility();
        CampaignDailyStats stats = createCampaignStats();
        stats.setNnsStateCount(50L);
        stats.setAnvStateCount(30L);
        stats.setUnaffectedCount(20L);

        CampaignOrganizationResponse result = presenter.present(stats, campaign, List.of(), List.of(), 100L, 20L, NOW_MS);

        assertThat(result.surveyUnits().totalSite()).isEqualTo(100L);
        assertThat(result.surveyUnits().notAffected()).isEqualTo(20L);
    }

    @Test
    @DisplayName("should handle empty referents list")
    void shouldHandleEmptyReferents() {
        CampaignVisibility campaign = createCampaignVisibility();
        CampaignDailyStats stats = createCampaignStats();

        CampaignOrganizationResponse result = presenter.present(stats, campaign, List.of(), List.of(), 0, 0, NOW_MS);

        assertThat(result.referents()).isEmpty();
    }

    @Test
    @DisplayName("should handle empty interviewers list")
    void shouldHandleEmptyInterviewers() {
        CampaignVisibility campaign = createCampaignVisibility();
        CampaignDailyStats stats = createCampaignStats();

        CampaignOrganizationResponse result = presenter.present(stats, campaign, List.of(), List.of(), 0, 0, NOW_MS);

        assertThat(result.interviewers()).isEmpty();
    }

    @Test
    @DisplayName("should map complete response with all data")
    void shouldMapCompleteResponse() {
        CampaignVisibility campaign = createCampaignVisibility();
        CampaignDailyStats stats = createCampaignStats();
        stats.setNnsStateCount(50L);
        stats.setUnaffectedCount(10L);

        List<Referent> referents = List.of(new Referent("John", "Doe", "0101010101", "PRIMARY"));
        List<InterviewerDailyStats> interviewers = List.of(
                createInterviewerStats("INT1", "Alice", "Anderson", 15L)
        );

        CampaignOrganizationResponse result = presenter.present(stats, campaign, referents, interviewers, 65L, 10L, NOW_MS);

        assertThat(result).satisfies(response -> {
            assertThat(response.campaignId()).isEqualTo("camp-1");
            assertThat(response.campaignLabel()).isEqualTo("Test Campaign");
            assertThat(response.campaignEmail()).isEqualTo("test@insee.fr");
            assertThat(response.phase()).isEqualTo(CampaignPhase.COLLECTION_IN_PROGRESS);
            assertThat(response.referents()).hasSize(1);
            assertThat(response.interviewers()).hasSize(1);
            assertThat(response.surveyUnits().totalSite()).isEqualTo(65L);
            assertThat(response.surveyUnits().notAffected()).isEqualTo(10L);
        });
    }

    private CampaignVisibility createCampaignVisibility() {
        return new CampaignVisibility(
                "camp-1", "Test Campaign", "test@insee.fr",
                MGMT_START, INTERV_START, IDENT_START,
                COLL_START, COLL_END, END_DATE);
    }

    private CampaignDailyStats createCampaignStats() {
        CampaignDailyStats stats = new CampaignDailyStats();
        stats.setCampaignId("camp-1");
        stats.setCampaignLabel("Test Campaign");
        stats.setUnaffectedCount(5L);
        stats.setNnsStateCount(10L);
        stats.setAnvStateCount(5L);
        return stats;
    }

    private InterviewerDailyStats createInterviewerStats(String id, String firstName, String lastName, Long allocatedCount) {
        InterviewerDailyStats stats = new InterviewerDailyStats();
        stats.setInterviewerId(id);
        stats.setInterviewerFirstName(firstName);
        stats.setInterviewerLastName(lastName);
        stats.setNnsStateCount(allocatedCount);
        return stats;
    }
}