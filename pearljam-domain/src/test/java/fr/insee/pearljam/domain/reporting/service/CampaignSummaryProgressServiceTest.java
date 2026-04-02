package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignSummaryProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesSummaryProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampaignSummaryProgressServiceTest {

    static final Long FIXED_TIMESTAMP = 1_000_000L;
    static final LocalDate DAY = LocalDate.of(2025, 1, 15);
    static final String USER_ID = "user-1";
    static final OrganizationUnitSummary OU = new OrganizationUnitSummary("ou-1", "OrganizationUnit 1");

    CampaignRepository campaignRepository;
    CampaignDailyStatsRepositoryPort campaignDailyStatsRepositoryPort;
    UserService userService;
    DateService dateService;

    CampaignSummaryProgressService service;

    @BeforeEach
    void setup() {
        campaignRepository = mock(CampaignRepository.class);
        campaignDailyStatsRepositoryPort = mock(CampaignDailyStatsRepositoryPort.class);
        userService = mock(UserService.class);
        dateService = mock(DateService.class);

        service = new CampaignSummaryProgressService(
                campaignRepository,
                campaignDailyStatsRepositoryPort,
                userService,
                dateService
        );

        when(dateService.getCurrentTimestamp()).thenReturn(FIXED_TIMESTAMP);
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of());
    }

    @Test
    void shouldReturnEmptyList_whenUserHasNoOrgUnits() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of());

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoCampaignsForOrgUnits() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignRepository.findCampaignWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of());

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOneCampaign_withCorrectMappingForDatesAndPhase() {
        CampaignWithVisibility camp = new CampaignWithVisibility("CAMP1", "Campaign One", 900_000L,
                950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignRepository.findCampaignWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(CampaignDailyStats.empty("CAMP1", "Campaign One")));

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, DAY);

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
        CampaignWithVisibility camp = new CampaignWithVisibility("CAMP1", "Campaign One",
                900_000L, 950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignRepository.findCampaignWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnMultipleCampaigns() {
        CampaignWithVisibility c1 = new CampaignWithVisibility("CAMP1", "Campaign One",
                900_000L, 950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);
        CampaignWithVisibility c2 = new CampaignWithVisibility("CAMP2", "Campaign Two",
                900_000L, 950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignRepository.findCampaignWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(c1, c2));
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(CampaignDailyStats.empty("CAMP1", "Campaign One"),
                        CampaignDailyStats.empty("CAMP2", "Campaign Two")));

        List<CampaignSummaryProgress> result = service.getCampaignSummaryProgress(USER_ID, DAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CampaignSummaryProgress::campaignId)
                .containsExactlyInAnyOrder("CAMP1", "CAMP2");
    }

    @Test
    void shouldMapDailyStatsToSurveyUnitsCorrectly() {
        CampaignWithVisibility camp = new CampaignWithVisibility("CAMP1", "Campaign One",
                900_000L, 950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);
        CampaignDailyStats stats = new CampaignDailyStats(
                "CAMP1", "Campaign One",
                1L, 2L, 3L, 4L,
                5L,   // vic
                6L,   // prc
                7L,   // aoc
                8L,   // aps
                9L,   // ins
                10L,  // wft
                11L, 12L, 13L, 14L, 15L, 10L,
                130L, 1L, 2L
        );

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignRepository.findCampaignWithVisibilityByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));
        when(campaignDailyStatsRepositoryPort.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        CampaignSummaryProgress campaignSummaryProgress = service.getCampaignSummaryProgress(USER_ID, DAY).getFirst();
        StatesSummaryProgress states = campaignSummaryProgress.states();

        assertThat(states.allocated()).isEqualTo(130);
        assertThat(states.toProcessInterviewer()).isEqualTo(5 + 6 + 7 + 8 + 9 + 10); // VIC+PRC+AOC+APS+INS+WFT = 45
        assertThat(states.toReview()).isEqualTo(12);
        assertThat(states.completed()).isEqualTo(13 + 14); // fin + clo = 27
        assertThat(states.notAssigned()).isEqualTo(10);
    }
}
