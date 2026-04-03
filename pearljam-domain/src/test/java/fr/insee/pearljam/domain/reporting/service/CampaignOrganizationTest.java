package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.model.CampaignOrganization;
import fr.insee.pearljam.domain.campaign.port.in.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.port.out.CampaignReferentRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.stub.CampaignDailyStatsRepositoryPortStub;
import fr.insee.pearljam.domain.campaign.stub.CampaignVibilityPortStub;
import fr.insee.pearljam.domain.campaign.stub.DateServiceStub;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.service.stub.CampaignReferentRepositoryStub;
import fr.insee.pearljam.domain.user.stub.UserServiceStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampaignOrganizationServiceTest {
    
    private static final long NOW_MS         = FixedDateService.FIXED_TIMESTAMP;
    private static final long MGMT_START     = NOW_MS - 4000L;
    private static final long COLL_START     = NOW_MS - 2000L;
    private static final long COLL_END       = NOW_MS + 2000L;
    private static final long END_DATE       = NOW_MS + 4000L;
    private static final long INTERV_START   = NOW_MS - 3000L;
    private static final long IDENT_START    = NOW_MS - 3500L;

    private static final String CAMPAIGN_ID = "CAMPAIGN_TEST_ID";
    private static final String USER_ID     = "USER_TEST_ID";

    private CampaignWithVisibility defaultCampaign() {
        return new CampaignWithVisibility(
                CAMPAIGN_ID,
                "Survey to test",
                MGMT_START,
                INTERV_START,
                IDENT_START,
                COLL_START,
                COLL_END,
                END_DATE);
    }

    private CampaignDailyStats defaultCampaignStats() {
        CampaignDailyStats stats = new CampaignDailyStats();
        stats.setCampaignId(CAMPAIGN_ID);
        stats.setCampaignLabel("Survey to test");
        stats.setTotal(10);
        stats.setUnaffected(3);
        return stats;
    }

    private List<InterviewerDailyStats> defaultInterviewerStats() {
        InterviewerDailyStats i1 = new InterviewerDailyStats();
        i1.setInterviewerId("INTERV1");
        i1.setInterviewerFirstName("Isabelle");
        i1.setInterviewerLastName("Interviewer 1");
        i1.setTotal(5);

        InterviewerDailyStats i2 = new InterviewerDailyStats();
        i2.setInterviewerId("INTERV2");
        i2.setInterviewerFirstName("Ingrid");
        i2.setInterviewerLastName("Interviewer 2");
        i2.setTotal(2);

        return List.of(i1, i2);
    }

    private List<Referent> defaultReferents() {
        return List.of(new Referent("John", "Doe", "0101010101", "PRIMARY"));
    }

    private List<OrganizationUnitSummary> defaultOUs() {
        return List.of(new OrganizationUnitSummary("OU-NORTH", "North"));
    }

    private Clock fixedClock() {
        return Clock.fixed(Instant.ofEpochMilli(NOW_MS), ZoneId.of("UTC"));
    }

    private CampaignOrganizationService buildService(
            CampaignReferentRepository campaignRepo,
            CampaignDailyStatsRepositoryPort statsRepo,
            CampaignVisibilityPort campaignVisibilityPort) {

        return new CampaignOrganizationService(
                statsRepo,
                campaignRepo,
                campaignVisibilityPort,
                new UserServiceStub(defaultOUs()),
                new DateServiceStub() {
                },
                fixedClock());
    }

    @Test
    @DisplayName("should return campaign organization with correct phase COLLECTION_IN_PROGRESS")
    void shouldReturnCampaignOrganizationWithCorrectPhase() throws CampaignNotFoundException {
        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(defaultReferents()),
                new CampaignDailyStatsRepositoryPortStub(List.of(defaultCampaignStats()), defaultInterviewerStats()),
                new CampaignVibilityPortStub(defaultCampaign()));

        CampaignOrganization result = service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID);

        assertThat(result.campaignId()).isEqualTo(CAMPAIGN_ID);
        assertThat(result.campaignLabel()).isEqualTo("Survey to test");
        assertThat(result.phase()).isEqualTo(CampaignPhase.COLLECTION_IN_PROGRESS);
    }

    @Test
    @DisplayName("should return INITIAL_ASSIGNMENT phase when before collection start")
    void shouldReturnInitialAssignmentPhase() throws CampaignNotFoundException {
        CampaignWithVisibility campaign = new CampaignWithVisibility(
                CAMPAIGN_ID, "Survey on the Simpsons",
                MGMT_START, INTERV_START, IDENT_START,
                NOW_MS + 1_000L,  // collection hasn't started yet
                COLL_END, END_DATE);

        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(defaultReferents()),
                new CampaignDailyStatsRepositoryPortStub(List.of(defaultCampaignStats()), defaultInterviewerStats()),
                new CampaignVibilityPortStub(campaign));

        CampaignOrganization result = service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID);

        assertThat(result.phase()).isEqualTo(CampaignPhase.INITIAL_ASSIGNMENT);
    }

    @Test
    @DisplayName("should return COLLECTION_COMPLETED phase when after collection end")
    void shouldReturnCollectionCompletedPhase() throws CampaignNotFoundException {
        CampaignWithVisibility campaign = new CampaignWithVisibility(
                CAMPAIGN_ID, "Survey to test",
                MGMT_START, INTERV_START, IDENT_START,
                NOW_MS - 3_000L,  // collection started
                NOW_MS - 1_000L,  // collection ended
                END_DATE);

        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(defaultReferents()),
                new CampaignDailyStatsRepositoryPortStub(List.of(defaultCampaignStats()), defaultInterviewerStats()),
                new CampaignVibilityPortStub(campaign));

        CampaignOrganization result = service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID);

        assertThat(result.phase()).isEqualTo(CampaignPhase.COLLECTION_COMPLETED);
    }

    @Test
    @DisplayName("should map referents correctly")
    void shouldMapReferents() throws CampaignNotFoundException {
        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(defaultReferents()),
                new CampaignDailyStatsRepositoryPortStub(List.of(defaultCampaignStats()), defaultInterviewerStats()),
                new CampaignVibilityPortStub(defaultCampaign()));

        CampaignOrganization result = service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID);

        assertThat(result.referents()).hasSize(1);
        assertThat(result.referents().getFirst().firstName()).isEqualTo("John");
        assertThat(result.referents().getFirst().lastName()).isEqualTo("Doe");
        assertThat(result.referents().getFirst().phoneNumber()).isEqualTo("0101010101");
        assertThat(result.referents().getFirst().role()).isEqualTo("PRIMARY");
    }

    @Test
    @DisplayName("should map interviewers with concatenated label")
    void shouldMapInterviewers() throws CampaignNotFoundException {
        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(defaultReferents()),
                new CampaignDailyStatsRepositoryPortStub(List.of(defaultCampaignStats()), defaultInterviewerStats()),
                new CampaignVibilityPortStub(defaultCampaign()));

        CampaignOrganization result = service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID);

        assertThat(result.interviewers()).hasSize(2);
        assertThat(result.interviewers().getFirst().id()).isEqualTo("INTERV1");
        assertThat(result.interviewers().getFirst().label()).isEqualTo("Isabelle Interviewer 1");
        assertThat(result.interviewers().getFirst().surveyUnits()).isEqualTo(5);
    }

    @Test
    @DisplayName("should map survey unit totals correctly")
    void shouldMapSurveyUnits() throws CampaignNotFoundException {
        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(defaultReferents()),
                new CampaignDailyStatsRepositoryPortStub(List.of(defaultCampaignStats()), defaultInterviewerStats()),
                new CampaignVibilityPortStub(defaultCampaign()));

        CampaignOrganization result = service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID);

        assertThat(result.surveyUnits().total()).isEqualTo(10);
        assertThat(result.surveyUnits().notAffected()).isEqualTo(3);
    }

    @Test
    @DisplayName("should throw CampaignNotFoundException when campaign stats not found")
    void shouldThrowWhenCampaignStatsNotFound() {
        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(defaultReferents()),
                new CampaignDailyStatsRepositoryPortStub(List.of(), List.of()),
                new CampaignVibilityPortStub(defaultCampaign()));


        assertThatThrownBy(() -> service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    @Test
    @DisplayName("should return empty referents list when no referents exist")
    void shouldHandleEmptyReferents() throws CampaignNotFoundException {
        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(List.of()),
                new CampaignDailyStatsRepositoryPortStub(List.of(defaultCampaignStats()), defaultInterviewerStats()),
                new CampaignVibilityPortStub(defaultCampaign()));

        CampaignOrganization result = service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID);

        assertThat(result.referents()).isEmpty();
    }

    @Test
    @DisplayName("should return empty interviewers list when no interviewers exist")
    void shouldHandleEmptyInterviewers() throws CampaignNotFoundException {
        CampaignOrganizationService service = buildService(
                new CampaignReferentRepositoryStub(defaultReferents()),
                new CampaignDailyStatsRepositoryPortStub(List.of(defaultCampaignStats()), List.of()),
                new CampaignVibilityPortStub(defaultCampaign()));


        CampaignOrganization result = service.getCampaignOrganizations(USER_ID, CAMPAIGN_ID);

        assertThat(result.interviewers()).isEmpty();
    }
}