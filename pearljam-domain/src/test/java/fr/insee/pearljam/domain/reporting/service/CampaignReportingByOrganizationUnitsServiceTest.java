package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CampaignReportingByOrganizationUnitsServiceTest {

    static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 15);
    static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    static final String USER_ID = "user-1";
    static final String CAMPAIGN_ID = "campaign-1";
    static final OrganizationUnitSummary OU = new OrganizationUnitSummary("ou-1", "Org Unit 1");

    UserService userService;
    CampaignDailyStatsRepositoryPort statsRepository;
    CampaignReportingByOrganizationUnitsService service;
    CampaignStatsByOrganizationUnitsPresenter<OrganizationUnitStatsResult> passthroughPresenter;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        service = new CampaignReportingByOrganizationUnitsService(userService, statsRepository, FIXED_CLOCK);
        passthroughPresenter = OrganizationUnitStatsResult::new;

        when(userService.getUserOUsModel(USER_ID, false)).thenReturn(List.of(OU));
        when(statsRepository.getOrganizationUnitsStats(anyString(), anyList(), any())).thenReturn(List.of());
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.empty());
    }

    @Test
    void shouldUseProvidedDay_whenDayIsInThePast() throws CampaignNotFoundException {
        LocalDate pastDate = FIXED_TODAY.minusDays(5);
        service.getProgressForDay(USER_ID, CAMPAIGN_ID, pastDate, passthroughPresenter);

        verify(statsRepository).getOrganizationUnitsStats(anyString(), anyList(), eq(pastDate));
    }

    @Test
    void shouldDefaultToToday_whenDayIsNull() throws CampaignNotFoundException {
        service.getProgressForDay(USER_ID, CAMPAIGN_ID, null, passthroughPresenter);

        verify(statsRepository).getOrganizationUnitsStats(anyString(), anyList(), eq(FIXED_TODAY));
    }

    @Test
    void shouldThrow_whenDayIsInTheFuture() {
        LocalDate futureDate = FIXED_TODAY.plusDays(10);
        assertThatThrownBy(() -> service.getProgressForDay(USER_ID, CAMPAIGN_ID, futureDate, passthroughPresenter))
                .isInstanceOf(FutureReportingDateException.class)
                .hasMessage("date must not be in the future");
    }

    @Test
    void shouldReturnEmptyOrganizationUnits_whenNoOuStats() throws CampaignNotFoundException {
        OrganizationUnitStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.organizationUnitStats()).isEmpty();
        assertThat(result.campaignStats().getProgressStateRate()).isZero();
        assertThat(result.campaignStats().getAllocatedStateCount()).isZero();
    }

    @Test
    void shouldMapOrganizationUnitLabelCorrectly() throws CampaignNotFoundException {
        OrganizationUnitDailyStats stats = ouStats("ou-1", "Org Unit 1");
        when(statsRepository.getOrganizationUnitsStats(anyString(), anyList(), any())).thenReturn(List.of(stats));

        OrganizationUnitStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.organizationUnitStats()).hasSize(1);
        assertThat(result.organizationUnitStats().getFirst().getOuLabel()).isEqualTo("Org Unit 1");
    }

    @Test
    void shouldMapOrganizationUnitStatesAndProgressRate() throws CampaignNotFoundException {
        OrganizationUnitDailyStats stats = ouStats("ou-1", "Org Unit 1");
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
        when(statsRepository.getOrganizationUnitsStats(anyString(), anyList(), any())).thenReturn(List.of(stats));

        OrganizationUnitStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        OrganizationUnitDailyStats ou = result.organizationUnitStats().getFirst();
        assertThat(ou.getProgressStateRate()).isCloseTo(54.135f, within(0.001f));
        assertThat(ou.getAllocatedStateCount()).isEqualTo(133L);
        assertThat(ou.getVicStateCount()).isEqualTo(4L);
        assertThat(ou.getInProgressStateCount()).isEqualTo(30L);
        assertThat(ou.getWftStateCount()).isEqualTo(6L);
        assertThat(ou.getTbrStateCount()).isEqualTo(20L);
        assertThat(ou.getCompletedStateCount()).isEqualTo(52L);
        assertThat(ou.getPrcStateCount()).isEqualTo(6L);
        assertThat(ou.getAocStateCount()).isEqualTo(7L);
        assertThat(ou.getApsStateCount()).isEqualTo(8L);
        assertThat(ou.getNoticeCommunicationCount()).isEqualTo(15L);
        assertThat(ou.getReminderCommunicationCount()).isEqualTo(25L);
    }

    @Test
    void shouldMapCampaignStats_whenStatsExist() throws CampaignNotFoundException {
        CampaignDailyStats campaignStat = new CampaignDailyStats();
        campaignStat.setCampaignId(CAMPAIGN_ID);
        campaignStat.setNnsStateCount(50L);
        campaignStat.setTbrStateCount(10L);
        campaignStat.setFinStateCount(30L);
        campaignStat.setCloStateCount(10L);

        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.of(campaignStat));

        OrganizationUnitStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.campaignStats().getProgressStateRate()).isCloseTo(50.0f, within(0.001f));
        assertThat(result.campaignStats().getAllocatedStateCount()).isEqualTo(100L);
    }

    @Test
    void shouldUseEmptyCampaignStats_whenNoCampaignStatsExist() throws CampaignNotFoundException {
        OrganizationUnitStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.campaignStats().getProgressStateRate()).isZero();
        assertThat(result.campaignStats().getAllocatedStateCount()).isZero();
    }

    @Test
    void shouldReturnMultipleOrganizationUnits() throws CampaignNotFoundException {
        OrganizationUnitDailyStats stats1 = ouStats("ou-1", "Org Unit 1");
        OrganizationUnitDailyStats stats2 = ouStats("ou-2", "Org Unit 2");
        when(statsRepository.getOrganizationUnitsStats(anyString(), anyList(), any()))
                .thenReturn(List.of(stats1, stats2));

        OrganizationUnitStatsResult result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, passthroughPresenter);

        assertThat(result.organizationUnitStats()).hasSize(2);
        assertThat(result.organizationUnitStats())
                .extracting(OrganizationUnitDailyStats::getOuLabel)
                .containsExactly("Org Unit 1", "Org Unit 2");
    }

    @Test
    void shouldPushOutputToPresenter() throws CampaignNotFoundException {
        @SuppressWarnings("unchecked")
        CampaignStatsByOrganizationUnitsPresenter<String> presenter = mock(CampaignStatsByOrganizationUnitsPresenter.class);

        when(presenter.present(anyList(), any())).thenReturn("presented");

        String result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, FIXED_TODAY, presenter);

        org.mockito.Mockito.verify(presenter).present(anyList(), any(CampaignDailyStats.class));
        assertThat(result).isEqualTo("presented");
    }

    record OrganizationUnitStatsResult(
            List<OrganizationUnitDailyStats> organizationUnitStats,
            CampaignDailyStats campaignStats
    ) {
    }

    private OrganizationUnitDailyStats ouStats(String ouId, String ouLabel) {
        OrganizationUnitDailyStats stats = new OrganizationUnitDailyStats();
        stats.setOuId(ouId);
        stats.setOuLabel(ouLabel);
        return stats;
    }
}
