package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CampaignProgressByInterviewersServiceTest {

    static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 15);
    static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    static final String USER_ID = "user-1";
    static final String CAMPAIGN_ID = "campaign-1";
    static final OrganizationUnitSummary OU = new OrganizationUnitSummary("ou-1", "Org Unit 1");

    UserService userService;
    CampaignDailyStatsRepositoryPort statsRepository;
    CampaignReportingByInterviewersService service;
    CampaignStatsByInterviewersPresenter<InterviewerStatsResult> passthroughPresenter;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        service = new CampaignReportingByInterviewersService(userService, statsRepository, FIXED_CLOCK);
        passthroughPresenter = InterviewerStatsResult::new;

        when(userService.getUserOUsModel(USER_ID, false)).thenReturn(List.of(OU));
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(List.of());
        when(statsRepository.findCampaignStatsForOrganizationUnits(anyString(), anyList(), any())).thenReturn(Optional.empty());
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.empty());
    }

    @Test
    void shouldUseProvidedDay_whenDayIsInThePast() throws CampaignNotFoundException {
        LocalDate pastDate = FIXED_TODAY.minusDays(5);
        service.getProgressForDay(USER_ID, CAMPAIGN_ID, pastDate, passthroughPresenter);

        verify(statsRepository).getInterviewerStats(anyString(), anyList(), eq(pastDate));
    }

    @Test
    void shouldDefaultToToday_whenDayIsNull() throws CampaignNotFoundException {
        service.getProgressForDay(USER_ID, CAMPAIGN_ID, null, passthroughPresenter);

        verify(statsRepository).getInterviewerStats(anyString(), anyList(), eq(FIXED_TODAY));
    }

    @Test
    void shouldThrow_whenDayIsInTheFuture() {
        LocalDate futureDate = FIXED_TODAY.plusDays(10);
        assertThatThrownBy(() -> service.getProgressForDay(USER_ID, CAMPAIGN_ID, futureDate, passthroughPresenter))
                .isInstanceOf(FutureReportingDateException.class)
                .hasMessage("date must not be in the future");
    }

    @Test
    void shouldReturnEmptyInterviewers_whenNoInterviewerStats() throws CampaignNotFoundException {
        InterviewerStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.interviewerStats()).isEmpty();
        assertThat(result.siteStats().getProgressStateRate()).isZero();
        assertThat(result.siteStats().getAllocatedStateCount()).isZero();
        assertThat(result.campaignStats().getProgressStateRate()).isZero();
        assertThat(result.campaignStats().getAllocatedStateCount()).isZero();
    }

    @Test
    void shouldMapInterviewerLabelCorrectly() throws CampaignNotFoundException {
        InterviewerDailyStats stats = interviewerStats("int-1", "Jean", "Dupont");
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(List.of(stats));

        InterviewerStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.interviewerStats()).hasSize(1);
        assertThat(result.interviewerStats().getFirst().getInterviewerFirstName()).isEqualTo("Jean");
        assertThat(result.interviewerStats().getFirst().getInterviewerLastName()).isEqualTo("Dupont");
    }

    @Test
    void shouldMapInterviewerSurveyUnitsAndProgressRate() throws CampaignNotFoundException {
        InterviewerDailyStats stats = interviewerStats("int-1", "Jean", "Dupont");
        stats.setNvmStateCount(1L);
        stats.setNnsStateCount(5L);
        stats.setAnvStateCount(2L);
        stats.setVinStateCount(3L);
        stats.setVicStateCount(4L);
        stats.setPrcStateCount(6L);
        stats.setAocStateCount(7L);
        stats.setApsStateCount(8L);
        stats.setInsStateCount(9L);
        stats.setWftStateCount(6L);
        stats.setWfsStateCount(11L);
        stats.setTbrStateCount(20L);
        stats.setFinStateCount(40L);
        stats.setCloStateCount(12L);
        stats.setNvaStateCount(8L);
        stats.setNoticeCommunicationCount(15L);
        stats.setReminderCommunicationCount(25L);
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(List.of(stats));

        InterviewerStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        InterviewerDailyStats interviewer = result.interviewerStats().getFirst();
        assertThat(interviewer.getProgressStateRate()).isCloseTo(54.135f, within(0.001f));
        assertThat(interviewer.getAllocatedStateCount()).isEqualTo(133L);
        assertThat(interviewer.getVicStateCount()).isEqualTo(4L);
        assertThat(interviewer.getInProgressStateCount()).isEqualTo(30L);
        assertThat(interviewer.getWftStateCount()).isEqualTo(6L);
        assertThat(interviewer.getTbrStateCount()).isEqualTo(20L);
        assertThat(interviewer.getCompletedStateCount()).isEqualTo(52L);
        assertThat(interviewer.getPrcStateCount()).isEqualTo(6L);
        assertThat(interviewer.getAocStateCount()).isEqualTo(7L);
        assertThat(interviewer.getApsStateCount()).isEqualTo(8L);
        assertThat(interviewer.getNoticeCommunicationCount()).isEqualTo(15L);
        assertThat(interviewer.getReminderCommunicationCount()).isEqualTo(25L);
    }

    @Test
    void shouldMapSiteAndCampaignStats_whenStatsExist() throws CampaignNotFoundException {
        CampaignDailyStats campaignStat = new CampaignDailyStats();
        campaignStat.setCampaignId(CAMPAIGN_ID);
        campaignStat.setCampaignLabel("Campaign One");
        campaignStat.setNnsStateCount(50L);
        campaignStat.setTbrStateCount(10L);
        campaignStat.setFinStateCount(30L);
        campaignStat.setCloStateCount(10L);

        when(statsRepository.findCampaignStatsForOrganizationUnits(anyString(), anyList(), any()))
                .thenReturn(Optional.of(campaignStat));
        when(statsRepository.findCampaignStats(anyString(), any()))
                .thenReturn(Optional.of(campaignStat));

        InterviewerStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.siteStats().getProgressStateRate()).isCloseTo(50.0f, within(0.001f));
        assertThat(result.siteStats().getAllocatedStateCount()).isEqualTo(100L);
        assertThat(result.campaignStats().getProgressStateRate()).isCloseTo(50.0f, within(0.001f));
        assertThat(result.campaignStats().getAllocatedStateCount()).isEqualTo(100L);
    }

    @Test
    void shouldReturnMultipleInterviewers() throws CampaignNotFoundException {
        InterviewerDailyStats stats1 = interviewerStats("int-1", "Jean", "Dupont");
        InterviewerDailyStats stats2 = interviewerStats("int-2", "Marie", "Martin");
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any()))
                .thenReturn(List.of(stats1, stats2));

        InterviewerStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.interviewerStats()).hasSize(2);
        assertThat(result.interviewerStats())
                .extracting(InterviewerDailyStats::getInterviewerFirstName, InterviewerDailyStats::getInterviewerLastName)
                .containsExactly(tuple("Jean", "Dupont"), tuple("Marie", "Martin"));
    }

    @Test
    void shouldPushOutputToPresenter() throws CampaignNotFoundException {
        @SuppressWarnings("unchecked")
        CampaignStatsByInterviewersPresenter<String> presenter = mock(CampaignStatsByInterviewersPresenter.class);

        when(presenter.present(anyList(), any(), any())).thenReturn("presented");

        String result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, presenter);

        org.mockito.Mockito.verify(presenter).present(anyList(), any(CampaignDailyStats.class), any(CampaignDailyStats.class));
        assertThat(result).isEqualTo("presented");
    }

    record InterviewerStatsResult(
            List<InterviewerDailyStats> interviewerStats,
            CampaignDailyStats siteStats,
            CampaignDailyStats campaignStats
    ) {
    }

    private InterviewerDailyStats interviewerStats(String interviewerId, String firstName, String lastName) {
        InterviewerDailyStats stats = new InterviewerDailyStats();
        stats.setInterviewerId(interviewerId);
        stats.setInterviewerFirstName(firstName);
        stats.setInterviewerLastName(lastName);
        return stats;
    }
}
