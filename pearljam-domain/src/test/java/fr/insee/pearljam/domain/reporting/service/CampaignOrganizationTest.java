package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationStatsPresenter;
import fr.insee.pearljam.domain.campaign.port.out.CampaignReferentRepository;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

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
    private static final LocalDate FIXED_TODAY = LocalDate.ofInstant(
            Instant.ofEpochMilli(NOW_MS), ZoneId.of("UTC"));

    private CampaignDailyStatsRepositoryPort statsRepository;
    private CampaignReferentRepository referentRepository;
    private CampaignVisibilityPort visibilityPort;
    private CampaignOrganizationService service;
    private CampaignOrganizationStatsPresenter<CampaignOrganizationResult> passthroughPresenter;

    @BeforeEach
    void setup() {
        UserService userService = mock(UserService.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        referentRepository = mock(CampaignReferentRepository.class);
        visibilityPort = mock(CampaignVisibilityPort.class);

        service = new CampaignOrganizationService(
                statsRepository,
                referentRepository,
                visibilityPort,
                userService,
                new FixedDateService(),
                fixedClock());

        passthroughPresenter = CampaignOrganizationResult::new;

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(defaultOUs());
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.empty());
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(List.of());
        when(referentRepository.getReferents(anyString())).thenReturn(List.of());
        when(visibilityPort.getCampaignVisibility(anyString(), anyList())).thenReturn(defaultCampaign());
    }

    private CampaignVisibility defaultCampaign() {
        return new CampaignVisibility(
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
        stats.setUnaffectedCount(3);
        stats.setAnvStateCount(5);
        stats.setNnsStateCount(5);
        return stats;
    }

    private List<InterviewerDailyStats> defaultInterviewerStats() {
        InterviewerDailyStats i1 = new InterviewerDailyStats();
        i1.setInterviewerId("INTERV1");
        i1.setInterviewerFirstName("Isabelle");
        i1.setInterviewerLastName("Interviewer 1");
        i1.setNnsStateCount(5);

        InterviewerDailyStats i2 = new InterviewerDailyStats();
        i2.setInterviewerId("INTERV2");
        i2.setInterviewerFirstName("Ingrid");
        i2.setInterviewerLastName("Interviewer 2");

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

    @Test
    @DisplayName("should use current date when fetching campaign stats")
    void shouldUseCurrentDate() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));

        service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(statsRepository).findCampaignStats(eq(CAMPAIGN_ID), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(FIXED_TODAY);
    }

    @Test
    @DisplayName("should throw CampaignNotFoundException when campaign stats not found")
    void shouldThrowWhenCampaignStatsNotFound() {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    @Test
    @DisplayName("should pass campaign stats to presenter")
    void shouldPassCampaignStatsToPresenter() throws CampaignNotFoundException {
        CampaignDailyStats stats = defaultCampaignStats();
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(stats));

        CampaignOrganizationResult result = service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        assertThat(result.campaignStats()).isEqualTo(stats);
        assertThat(result.campaignStats().getCampaignId()).isEqualTo(CAMPAIGN_ID);
        assertThat(result.campaignStats().getCampaignLabel()).isEqualTo("Survey to test");
    }

    @Test
    @DisplayName("should pass campaign visibility to presenter")
    void shouldPassCampaignVisibilityToPresenter() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));
        CampaignVisibility campaign = defaultCampaign();
        when(visibilityPort.getCampaignVisibility(anyString(), anyList())).thenReturn(campaign);

        CampaignOrganizationResult result = service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        assertThat(result.campaign()).isEqualTo(campaign);
        assertThat(result.campaign().id()).isEqualTo(CAMPAIGN_ID);
    }

    @Test
    @DisplayName("should pass referents to presenter")
    void shouldPassReferentsToPresenter() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));
        List<Referent> referents = defaultReferents();
        when(referentRepository.getReferents(CAMPAIGN_ID)).thenReturn(referents);

        CampaignOrganizationResult result = service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        assertThat(result.referents()).hasSize(1);
        assertThat(result.referents().getFirst().firstName()).isEqualTo("John");
        assertThat(result.referents().getFirst().lastName()).isEqualTo("Doe");
        assertThat(result.referents().getFirst().phoneNumber()).isEqualTo("0101010101");
        assertThat(result.referents().getFirst().role()).isEqualTo("PRIMARY");
    }

    @Test
    @DisplayName("should pass interviewer stats to presenter")
    void shouldPassInterviewerStatsToPresenter() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));
        List<InterviewerDailyStats> interviewerStats = defaultInterviewerStats();
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(interviewerStats);

        CampaignOrganizationResult result = service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        assertThat(result.interviewerStats()).hasSize(2);
        assertThat(result.interviewerStats().getFirst().getInterviewerId()).isEqualTo("INTERV1");
        assertThat(result.interviewerStats().getFirst().getInterviewerFirstName()).isEqualTo("Isabelle");
    }

    @Test
    @DisplayName("should pass current timestamp to presenter")
    void shouldPassCurrentTimestampToPresenter() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));

        CampaignOrganizationResult result = service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        assertThat(result.currentTimestamp()).isEqualTo(NOW_MS);
    }

    @Test
    @DisplayName("should handle empty referents")
    void shouldHandleEmptyReferents() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));
        when(referentRepository.getReferents(CAMPAIGN_ID)).thenReturn(List.of());

        CampaignOrganizationResult result = service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        assertThat(result.referents()).isEmpty();
    }

    @Test
    @DisplayName("should handle empty interviewer stats")
    void shouldHandleEmptyInterviewers() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(List.of());

        CampaignOrganizationResult result = service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        assertThat(result.interviewerStats()).isEmpty();
    }

    @Test
    @DisplayName("should fetch stats for correct campaign and OUs")
    void shouldFetchStatsForCorrectCampaignAndOUs() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));

        service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, passthroughPresenter);

        verify(statsRepository).findCampaignStats(eq(CAMPAIGN_ID), any());
        verify(visibilityPort).getCampaignVisibility(eq(CAMPAIGN_ID), eq(List.of("OU-NORTH")));
        verify(statsRepository).getInterviewerStats(eq(CAMPAIGN_ID), eq(List.of("OU-NORTH")), any());
    }

    @Test
    @DisplayName("should push output to custom presenter")
    void shouldPushOutputToPresenter() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(defaultCampaignStats()));

        @SuppressWarnings("unchecked")
        CampaignOrganizationStatsPresenter<String> presenter = mock(CampaignOrganizationStatsPresenter.class);
        when(presenter.present(any(), any(), anyList(), anyList(), anyLong())).thenReturn("presented");

        String result = service.getCampaignOrganization(USER_ID, CAMPAIGN_ID, presenter);

        verify(presenter).present(
                any(CampaignDailyStats.class),
                any(CampaignVisibility.class),
                anyList(),
                anyList(),
                eq(NOW_MS));
        assertThat(result).isEqualTo("presented");
    }

    record CampaignOrganizationResult(
            CampaignDailyStats campaignStats,
            CampaignVisibility campaign,
            List<Referent> referents,
            List<InterviewerDailyStats> interviewerStats,
            long currentTimestamp
    ) {
    }
}