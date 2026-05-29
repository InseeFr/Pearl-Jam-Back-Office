package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignSummaryProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesSummaryProgress;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CampaignSummaryProgressServiceTest {

    static final Long FIXED_TIMESTAMP = 1_000_000L;
    static final LocalDate FIXED_TODAY = LocalDate.of(2025, 1, 15);
    static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    static final String USER_ID = "user-1";
    static final OrganizationUnitSummary OU = new OrganizationUnitSummary("ou-1", "OrganizationUnit 1");

    CampaignVisibilityPort campaignVisibilityPort;
    CampaignDailyStatsRepositoryPort campaignDailyStatsRepositoryPort;
    UserService userService;
    DateService dateService;

    CampaignSummaryProgressService service;

    @BeforeEach
    void setup() {
        campaignVisibilityPort = mock(CampaignVisibilityPort.class);
        campaignDailyStatsRepositoryPort = mock(CampaignDailyStatsRepositoryPort.class);
        userService = mock(UserService.class);
        dateService = mock(DateService.class);

        service = new CampaignSummaryProgressService(
                campaignVisibilityPort,
                campaignDailyStatsRepositoryPort,
                userService,
                dateService,
                FIXED_CLOCK
        );

        when(dateService.getCurrentTimestamp()).thenReturn(FIXED_TIMESTAMP);
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of());
    }

    @Test
    void shouldDefaultToToday_whenDayIsNull() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        CampaignVisibility camp = new CampaignVisibility(
                "CAMP1", "Campaign One", "camp1@insee.fr",
                900_000L, 950_000L, 1_000_000L,
                1_050_000L, 1_100_000L, 1_150_000L);
        when(campaignVisibilityPort.findCampaignsWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(CampaignDailyStats.empty("CAMP1", "Campaign One")));

        service.getCampaignSummaryProgress(USER_ID, null);

        verify(campaignDailyStatsRepositoryPort).getCampaignsStats(anyList(), anyList(), eq(FIXED_TODAY));
    }

    @Test
    void shouldThrow_whenDayIsInTheFuture() {
        LocalDate futureDay = FIXED_TODAY.plusDays(10);

        assertThatThrownBy(() -> service.getCampaignSummaryProgress(USER_ID, futureDay))
                .isInstanceOf(FutureReportingDateException.class)
                .hasMessage("date must not be in the future");
    }

    @Test
    void shouldUseProvidedDay_whenDayIsInThePast() {
        LocalDate pastDate = FIXED_TODAY.minusDays(5);
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        CampaignVisibility camp = new CampaignVisibility(
                "CAMP1", "Campaign One", "camp1@insee.fr",
                900_000L, 950_000L, 1_000_000L,
                1_050_000L, 1_100_000L, 1_150_000L);
        when(campaignVisibilityPort.findCampaignsWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(CampaignDailyStats.empty("CAMP1", "Campaign One")));

        service.getCampaignSummaryProgress(USER_ID, pastDate);

        verify(campaignDailyStatsRepositoryPort).getCampaignsStats(anyList(), anyList(), eq(pastDate));
    }

    @Test
    void shouldReturnEmptyList_whenUserHasNoOrgUnits() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of());

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, FIXED_TODAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoCampaignsForOrgUnits() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignVisibilityPort.findCampaignsWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of());

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, FIXED_TODAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOneCampaign_withCorrectMappingForDatesAndPhase() {
        CampaignVisibility camp = new CampaignVisibility(
                "CAMP1", "Campaign One", "camp1@insee.fr",
                900_000L, 950_000L, 1_000_000L,
                1_050_000L, 1_100_000L, 1_150_000L);

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignVisibilityPort.findCampaignsWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(CampaignDailyStats.empty("CAMP1", "Campaign One")));

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, FIXED_TODAY);

        assertThat(result).hasSize(1);
        CampaignSummaryProgress cs = result.getFirst();
        assertThat(cs.campaignId()).isEqualTo("CAMP1");
        assertThat(cs.campaignLabel()).isEqualTo("Campaign One");
        assertThat(cs.collectionStartDate()).isEqualTo(1_050_000L);
        assertThat(cs.collectionEndDate()).isEqualTo(1_100_000L);
        assertThat(cs.endDate()).isEqualTo(1_150_000L);
        assertThat(cs.campaignPhase()).isEqualTo(CampaignPhase.INITIAL_ASSIGNMENT);
        assertThat(cs.states().allocated()).isZero();
    }

    @Test
    void shouldUseZeroCounts_whenNoSnapshotForCampaign() {
        CampaignVisibility camp = new CampaignVisibility(
                "CAMP1", "Campaign One", "camp1@insee.fr",
                900_000L, 950_000L, 1_000_000L,
                1_050_000L, 1_100_000L, 1_150_000L);

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignVisibilityPort.findCampaignsWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, FIXED_TODAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnMultipleCampaigns() {
        CampaignVisibility c1 = new CampaignVisibility(
                "CAMP1", "Campaign One", "camp1@insee.fr",
                900_000L, 950_000L, 1_000_000L,
                1_050_000L, 1_100_000L, 1_150_000L);
        CampaignVisibility c2 = new CampaignVisibility(
                "CAMP2", "Campaign Two", "camp2@insee.fr",
                900_000L, 950_000L, 1_000_000L,
                1_050_000L, 1_100_000L, 1_150_000L);

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignVisibilityPort.findCampaignsWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(c1, c2));
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(CampaignDailyStats.empty("CAMP1", "Campaign One"),
                        CampaignDailyStats.empty("CAMP2", "Campaign Two")));

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, FIXED_TODAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CampaignSummaryProgress::campaignId)
                .containsExactlyInAnyOrder("CAMP1", "CAMP2");
    }

    @Test
    void shouldMapDailyStatsToSurveyUnitsCorrectly() {
        CampaignVisibility camp = new CampaignVisibility(
                "CAMP1", "Campaign One", "camp1@insee.fr",
                900_000L, 950_000L, 1_000_000L,
                1_050_000L, 1_100_000L, 1_150_000L);
        CampaignDailyStats stats = new CampaignDailyStats();
        stats.setCampaignId("CAMP1");
        stats.setCampaignLabel("Campaign One");
        stats.setNvmStateCount(1L);
        stats.setNnsStateCount(2L);
        stats.setAnvStateCount(3L);
        stats.setVinStateCount(4L);
        stats.setVicStateCount(5L);
        stats.setPrcStateCount(6L);
        stats.setAocStateCount(7L);
        stats.setApsStateCount(8L);
        stats.setInsStateCount(9L);
        stats.setWftStateCount(10L);
        stats.setWfsStateCount(11L);
        stats.setTbrStateCount(12L);
        stats.setFinStateCount(13L);
        stats.setCloStateCount(14L);
        stats.setNvaStateCount(15L);
        stats.setUnaffectedCount(10L);
        stats.setNoticeCommunicationCount(1L);
        stats.setReminderCommunicationCount(2L);

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignVisibilityPort.findCampaignsWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        CampaignSummaryProgress campaignSummaryProgress = service.getCampaignSummaryProgress(USER_ID, FIXED_TODAY).getFirst();
        StatesSummaryProgress states = campaignSummaryProgress.states();

        assertThat(states.allocated()).isEqualTo(114);
        assertThat(states.toProcessInterviewer()).isEqualTo(5 + 6 + 7 + 8 + 9 + 10); // VIC+PRC+AOC+APS+INS+WFT = 45
        assertThat(states.toReview()).isEqualTo(12);
        assertThat(states.completed()).isEqualTo(13 + 14); // fin + clo = 27
        assertThat(states.notAssigned()).isEqualTo(10);
    }
}
