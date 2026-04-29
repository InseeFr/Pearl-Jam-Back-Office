package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

class InterviewerCampaignsReportingServiceTest {

    static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 15);
    static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    static final String USER_ID = "user-1";
    static final OrganizationUnitSummary OU = new OrganizationUnitSummary("ou-1", "Org Unit 1");

    CampaignRepository campaignRepository;
    CampaignDailyStatsRepositoryPort statsRepository;
    UserService userService;
    InterviewerCampaignsReportingService service;
    InterviewerCampaignsStatsPresenter<List<InterviewerCampaignDailyStats>> interviewerPassthroughPresenter;

    @BeforeEach
    void setup() {
        campaignRepository = mock(CampaignRepository.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        userService = mock(UserService.class);
        service = new InterviewerCampaignsReportingService(campaignRepository, statsRepository, userService, FIXED_CLOCK);
        interviewerPassthroughPresenter = stats -> stats;

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of());
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of());
    }

    @Test
    @DisplayName("Defaults to today's date when day is null")
    void shouldDefaultToToday_whenDayIsNull() {
        // Given
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));

        // When
        service.getCampaignsStatsForInterviewer(USER_ID, null, "interviewer-1", interviewerPassthroughPresenter);

        // Then
        verify(statsRepository).getCampaignsStatsForInterviewer(eq("interviewer-1"), anyList(), anyList(), eq(FIXED_TODAY));
    }

    @Test
    @DisplayName("Throws FutureReportingDateException when day is in the future")
    void shouldThrow_whenDayIsInTheFuture() {
        // Given
        LocalDate futureDay = FIXED_TODAY.plusDays(10);

        // When / Then
        assertThatThrownBy(() -> service.getCampaignsStatsForInterviewer(USER_ID, futureDay, "interviewer-1", interviewerPassthroughPresenter))
                .isInstanceOf(FutureReportingDateException.class)
                .hasMessage("date must not be in the future");
    }

    @Test
    @DisplayName("Uses the provided day when it is in the past")
    void shouldUseProvidedDay_whenDayIsInThePast() {
        // Given
        LocalDate pastDate = FIXED_TODAY.minusDays(5);
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));

        // When
        service.getCampaignsStatsForInterviewer(USER_ID, pastDate, "interviewer-1", interviewerPassthroughPresenter);

        // Then
        verify(statsRepository).getCampaignsStatsForInterviewer(eq("interviewer-1"), anyList(), anyList(), eq(pastDate));
    }

    @Test
    @DisplayName("Returns an empty list when the user has no organization unit")
    void shouldReturnEmptyList_whenUserHasNoOrganizationUnits() {
        // Given
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of());

        // When
        List<InterviewerCampaignDailyStats> result =
                service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", interviewerPassthroughPresenter);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns an empty list when no opened campaign exists")
    void shouldReturnEmptyList_whenNoCampaigns() {
        // Given
        // (no campaigns returned, see @BeforeEach default)

        // When
        List<InterviewerCampaignDailyStats> result =
                service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", interviewerPassthroughPresenter);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns an empty list when no stats exist for the interviewer")
    void shouldReturnEmptyList_whenNoStatsForCampaign() {
        // Given
        CampaignSummary campaign = new CampaignSummary("c1", "C1");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));

        // When
        List<InterviewerCampaignDailyStats> result =
                service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", interviewerPassthroughPresenter);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns interviewer daily stats when stats exist")
    void shouldReturnCampaignDailyStats_whenStatsExist() {
        // Given
        CampaignSummary campaign = new CampaignSummary("c1", "Campaign One");
        InterviewerCampaignDailyStats stats = InterviewerCampaignDailyStats.empty("c1", "Campaign One");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        // When
        List<InterviewerCampaignDailyStats> result =
                service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", interviewerPassthroughPresenter);

        // Then
        assertThat(result).containsExactly(stats);
    }

    @Test
    @DisplayName("Returns stats for multiple campaigns of the interviewer")
    void shouldReturnMultipleCampaigns() {
        // Given
        CampaignSummary c1 = new CampaignSummary("c1", "Campaign One");
        CampaignSummary c2 = new CampaignSummary("c2", "Campaign Two");
        InterviewerCampaignDailyStats stats1 = InterviewerCampaignDailyStats.empty("c1", "Campaign One");
        InterviewerCampaignDailyStats stats2 = InterviewerCampaignDailyStats.empty("c2", "Campaign Two");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(c1, c2));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of(stats1, stats2));

        // When
        List<InterviewerCampaignDailyStats> result =
                service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", interviewerPassthroughPresenter);

        // Then
        assertThat(result).containsExactly(stats1, stats2);
    }

    @Test
    @DisplayName("Delegates output formatting to the presenter")
    void shouldPushOutputToPresenter() {
        // Given
        CampaignSummary campaign = new CampaignSummary("c1", "Campaign One");
        InterviewerCampaignDailyStats stats = InterviewerCampaignDailyStats.empty("c1", "Campaign One");
        @SuppressWarnings("unchecked")
        InterviewerCampaignsStatsPresenter<String> presenter = mock(InterviewerCampaignsStatsPresenter.class);
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(anyString(), anyList(), anyList(), any()))
                .thenReturn(List.of(stats));
        when(presenter.present(List.of(stats))).thenReturn("presented");

        // When
        String result = service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "interviewer-1", presenter);

        // Then
        verify(presenter).present(List.of(stats));
        assertThat(result).isEqualTo("presented");
    }

    @Test
    @DisplayName("Calls the repository with the expected interviewerId, campaignIds and OU ids")
    void shouldCallRepositoryWithCorrectParameters() {
        // Given
        CampaignSummary campaign = new CampaignSummary("c1", "Campaign One");
        InterviewerCampaignDailyStats stats = InterviewerCampaignDailyStats.empty("c1", "Campaign One");
        String interviewerId = "interviewer-123";
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStatsForInterviewer(eq(interviewerId), anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        // When
        service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, interviewerId, interviewerPassthroughPresenter);

        // Then
        verify(statsRepository).getCampaignsStatsForInterviewer(
                interviewerId,
                List.of("c1"),
                List.of("ou-1"),
                FIXED_TODAY
        );
    }
}
