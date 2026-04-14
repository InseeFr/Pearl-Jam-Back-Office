package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
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

class CampaignReportingServiceTest {

    static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 15);
    static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    static final String USER_ID = "user-1";
    static final OrganizationUnitSummary OU = new OrganizationUnitSummary("ou-1", "Org Unit 1");

    CampaignRepository campaignRepository;
    CampaignDailyStatsRepositoryPort statsRepository;
    UserService userService;
    CampaignReportingService service;
    CampaignStatsPresenter<List<CampaignDailyStats>> passthroughPresenter;

    @BeforeEach
    void setup() {
        campaignRepository = mock(CampaignRepository.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        userService = mock(UserService.class);
        service = new CampaignReportingService(campaignRepository, statsRepository, userService, FIXED_CLOCK);
        passthroughPresenter = stats -> stats;

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of());
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of());
    }

    @Test
    void shouldDefaultToToday_whenDayIsNull() {
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));

        service.getCampaignsStats(USER_ID, null, passthroughPresenter);

        verify(statsRepository).getCampaignsStats(anyList(), anyList(), eq(FIXED_TODAY));
    }

    @Test
    void shouldThrow_whenDayIsInTheFuture() {
        LocalDate futureDay = FIXED_TODAY.plusDays(10);

        assertThatThrownBy(() -> service.getCampaignsStats(USER_ID, futureDay, passthroughPresenter))
                .isInstanceOf(FutureReportingDateException.class)
                .hasMessage("date must not be in the future");
    }

    @Test
    void shouldUseProvidedDay_whenDayIsInThePast() {
        LocalDate pastDate = FIXED_TODAY.minusDays(5);
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));

        service.getCampaignsStats(USER_ID, pastDate, passthroughPresenter);

        verify(statsRepository).getCampaignsStats(anyList(), anyList(), eq(pastDate));
    }

    @Test
    void shouldReturnEmptyList_whenUserHasNoOrganizationUnits() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of());

        List<CampaignDailyStats> result = service.getCampaignsStats(USER_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoCampaigns() {
        List<CampaignDailyStats> result = service.getCampaignsStats(USER_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoStatsForCampaign() {
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));

        List<CampaignDailyStats> result = service.getCampaignsStats(USER_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnCampaignDailyStats_whenStatsExist() {
        CampaignSummary campaign = new CampaignSummary("c1", "Campaign One");
        CampaignDailyStats stats = CampaignDailyStats.empty("c1", "Campaign One");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        List<CampaignDailyStats> result = service.getCampaignsStats(USER_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result).containsExactly(stats);
    }

    @Test
    void shouldReturnMultipleCampaigns() {
        CampaignSummary c1 = new CampaignSummary("c1", "Campaign One");
        CampaignSummary c2 = new CampaignSummary("c2", "Campaign Two");
        CampaignDailyStats stats1 = CampaignDailyStats.empty("c1", "Campaign One");
        CampaignDailyStats stats2 = CampaignDailyStats.empty("c2", "Campaign Two");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(c1, c2));
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats1, stats2));

        List<CampaignDailyStats> result = service.getCampaignsStats(USER_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result).containsExactly(stats1, stats2);
    }

    @Test
    void shouldPushOutputToPresenter() {
        CampaignSummary campaign = new CampaignSummary("c1", "Campaign One");
        CampaignDailyStats stats = CampaignDailyStats.empty("c1", "Campaign One");
        @SuppressWarnings("unchecked")
        CampaignStatsPresenter<String> presenter = mock(CampaignStatsPresenter.class);

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats));
        when(presenter.present(List.of(stats))).thenReturn("presented");

        String result = service.getCampaignsStats(USER_ID, FIXED_TODAY, presenter);

        verify(presenter).present(List.of(stats));
        assertThat(result).isEqualTo("presented");
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldDefaultToToday_whenDayIsNull() {
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of());

        service.getCampaignsStatsForInterviewer(USER_ID, null, "interviewer-1", passthroughPresenter);

        verify(statsRepository).getCampaignsStatsForInterviewer(eq("interviewer-1"), anyList(), anyList(), eq(FIXED_TODAY));
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldThrow_whenDayIsInTheFuture() {
        LocalDate futureDay = FIXED_TODAY.plusDays(10);

        assertThatThrownBy(() -> service.getCampaignsStatsForInterviewer(USER_ID, futureDay, "interviewer-1", passthroughPresenter))
                .isInstanceOf(FutureReportingDateException.class)
                .hasMessage("date must not be in the future");
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldUseProvidedDay_whenDayIsInThePast() {
        LocalDate pastDate = FIXED_TODAY.minusDays(5);
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of());

        service.getCampaignsStatsForInterviewer(USER_ID, pastDate, "interviewer-1", passthroughPresenter);

        verify(statsRepository).getCampaignsStatsForInterviewer(eq("interviewer-1"), anyList(), anyList(), eq(pastDate));
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldReturnEmptyList_whenUserHasNoOrganizationUnits() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of());

        List<CampaignDailyStats> result = service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", passthroughPresenter);

        assertThat(result).isEmpty();
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldReturnEmptyList_whenNoCampaigns() {
        List<CampaignDailyStats> result = service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", passthroughPresenter);

        assertThat(result).isEmpty();
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldReturnEmptyList_whenNoStatsForCampaign() {
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of());

        List<CampaignDailyStats> result = service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", passthroughPresenter);

        assertThat(result).isEmpty();
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldReturnCampaignDailyStats_whenStatsExist() {
        CampaignSummary campaign = new CampaignSummary("c1", "Campaign One");
        CampaignDailyStats stats = CampaignDailyStats.empty("c1", "Campaign One");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        List<CampaignDailyStats> result = service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", passthroughPresenter);

        assertThat(result).containsExactly(stats);
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldReturnMultipleCampaigns() {
        CampaignSummary c1 = new CampaignSummary("c1", "Campaign One");
        CampaignSummary c2 = new CampaignSummary("c2", "Campaign Two");
        CampaignDailyStats stats1 = CampaignDailyStats.empty("c1", "Campaign One");
        CampaignDailyStats stats2 = CampaignDailyStats.empty("c2", "Campaign Two");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(c1, c2));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of(stats1, stats2));

        List<CampaignDailyStats> result = service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", passthroughPresenter);

        assertThat(result).containsExactly(stats1, stats2);
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldPushOutputToPresenter() {
        CampaignSummary campaign = new CampaignSummary("c1", "Campaign One");
        CampaignDailyStats stats = CampaignDailyStats.empty("c1", "Campaign One");
        @SuppressWarnings("unchecked")
        CampaignStatsPresenter<String> presenter = mock(CampaignStatsPresenter.class);

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of(stats));
        when(presenter.present(List.of(stats))).thenReturn("presented");

        String result = service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", presenter);

        verify(presenter).present(List.of(stats));
        assertThat(result).isEqualTo("presented");
    }

    @Test
    void getCampaignsStatsForInterviewer_shouldCallRepositoryWithCorrectParameters() {
        CampaignSummary campaign = new CampaignSummary("c1", "Campaign One");
        CampaignDailyStats stats = CampaignDailyStats.empty("c1", "Campaign One");
        String interviewerId = "interviewer-123";

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(eq(interviewerId), anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, interviewerId, passthroughPresenter);

        verify(statsRepository).getCampaignsStatsForInterviewer(
                interviewerId,
                List.of("c1"),
                List.of("ou-1"),
                FIXED_TODAY
        );
    }
}
