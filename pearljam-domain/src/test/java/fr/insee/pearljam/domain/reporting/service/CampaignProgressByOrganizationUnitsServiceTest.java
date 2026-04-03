package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignProgressByOrganizationUnits;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.OrganizationUnitDailyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampaignProgressByOrganizationUnitsServiceTest {

    static final LocalDate DAY = LocalDate.of(2025, 1, 15);
    static final String USER_ID = "user-1";
    static final String CAMPAIGN_ID = "campaign-1";
    static final OrganizationUnitSummary OU = new OrganizationUnitSummary("ou-1", "Org Unit 1");

    UserService userService;
    CampaignDailyStatsRepositoryPort statsRepository;
    CampaignProgressByOrganizationUnitsService service;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        service = new CampaignProgressByOrganizationUnitsService(userService, statsRepository);

        when(userService.getUserOUsModel(USER_ID, false)).thenReturn(List.of(OU));
        when(statsRepository.getOrganizationUnitsStats(anyString(), anyList(), any())).thenReturn(List.of());
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.empty());
    }

    @Test
    void shouldReturnEmptyOrganizationUnits_whenNoOuStats() throws CampaignNotFoundException {
        CampaignProgressByOrganizationUnits result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.organizationUnits()).isEmpty();
        assertThat(result.campaign().progressRate()).isZero();
        assertThat(result.campaign().states().allocated()).isZero();
    }

    @Test
    void shouldMapOrganizationUnitLabelCorrectly() throws CampaignNotFoundException {
        OrganizationUnitDailyStats stats = ouStats("ou-1", "Org Unit 1");
        when(statsRepository.getOrganizationUnitsStats(anyString(), anyList(), any())).thenReturn(List.of(stats));

        CampaignProgressByOrganizationUnits result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.organizationUnits()).hasSize(1);
        assertThat(result.organizationUnits().getFirst().organizationUnitLabel()).isEqualTo("Org Unit 1");
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

        CampaignProgressByOrganizationUnits result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, DAY);

        CampaignProgressByOrganizationUnits.OrganizationUnit ou = result.organizationUnits().getFirst();
        // allocated = nns+anv+vin+vic+prc+aoc+aps+ins+wft+wfs+tbr+fin+clo = 5+2+3+4+6+7+8+9+6+11+20+40+12 = 133
        // progress = (tbr+fin+clo)/allocated*100 = (20+40+12)/133*100 ≈ 54.135%
        assertThat(ou.progressRate()).isCloseTo(54.135f, within(0.001f));
        assertThat(ou.states().allocated()).isEqualTo(133L);
        assertThat(ou.states().notStarted()).isEqualTo(4L);
        assertThat(ou.states().inProgress()).isEqualTo(30L);
        assertThat(ou.states().pendingTransmission()).isEqualTo(6L);
        assertThat(ou.states().toReview()).isEqualTo(20L);
        assertThat(ou.states().validated()).isEqualTo(52L);
        assertThat(ou.states().preparingContact()).isEqualTo(6L);
        assertThat(ou.states().withContact()).isEqualTo(7L);
        assertThat(ou.states().withAppointment()).isEqualTo(8L);
        assertThat(ou.communications().noticeLetter()).isEqualTo(15L);
        assertThat(ou.communications().reminderLetter()).isEqualTo(25L);
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

        CampaignProgressByOrganizationUnits result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.campaign().progressRate()).isCloseTo(50.0f, within(0.001f));
        assertThat(result.campaign().states().allocated()).isEqualTo(100L);
    }

    @Test
    void shouldUseEmptyCampaignStats_whenNoCampaignStatsExist() throws CampaignNotFoundException {
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.empty());

        CampaignProgressByOrganizationUnits result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.campaign().progressRate()).isZero();
        assertThat(result.campaign().states().allocated()).isZero();
    }

    @Test
    void shouldReturnMultipleOrganizationUnits() throws CampaignNotFoundException {
        OrganizationUnitDailyStats stats1 = ouStats("ou-1", "Org Unit 1");
        OrganizationUnitDailyStats stats2 = ouStats("ou-2", "Org Unit 2");
        when(statsRepository.getOrganizationUnitsStats(anyString(), anyList(), any()))
                .thenReturn(List.of(stats1, stats2));

        CampaignProgressByOrganizationUnits result = service.getProgressForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.organizationUnits()).hasSize(2);
        assertThat(result.organizationUnits())
                .extracting(CampaignProgressByOrganizationUnits.OrganizationUnit::organizationUnitLabel)
                .containsExactly("Org Unit 1", "Org Unit 2");
    }

    private OrganizationUnitDailyStats ouStats(String ouId, String ouLabel) {
        OrganizationUnitDailyStats stats = new OrganizationUnitDailyStats();
        stats.setOuId(ouId);
        stats.setOuLabel(ouLabel);
        return stats;
    }
}
