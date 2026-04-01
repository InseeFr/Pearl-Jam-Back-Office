package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignProgressByInterviewers;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationsProgress;
import fr.insee.pearljam.domain.reporting.readmodel.StatesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;
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

class CampaignProgressByInterviewersServiceTest {

    static final LocalDate DAY = LocalDate.of(2025, 1, 15);
    static final String USER_ID = "user-1";
    static final String CAMPAIGN_ID = "campaign-1";
    static final OrganizationUnitSummary OU = new OrganizationUnitSummary("ou-1", "Org Unit 1");

    UserService userService;
    CampaignDailyStatsRepositoryPort statsRepository;
    CampaignProgressByInterviewersService service;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        service = new CampaignProgressByInterviewersService(userService, statsRepository);

        when(userService.getUserOUsModel(USER_ID, false)).thenReturn(List.of(OU));
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(List.of());
        when(statsRepository.findCampaignStatsForOrganizationUnits(anyString(), anyList(), any())).thenReturn(Optional.empty());
        when(statsRepository.findCampaignStats(anyString(), any())).thenReturn(Optional.empty());
    }

    @Test
    void shouldReturnEmptyInterviewers_whenNoInterviewerStats() throws CampaignNotFoundException {
        CampaignProgressByInterviewers result = service.getProgressionForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.interviewers()).isEmpty();
        assertThat(result.site().progressRate()).isZero();
        assertThat(result.site().states().allocated()).isZero();
        assertThat(result.campaign().progressRate()).isZero();
        assertThat(result.campaign().states().allocated()).isZero();
    }

    @Test
    void shouldMapInterviewerLabelCorrectly() throws CampaignNotFoundException {
        InterviewerDailyStats stats = new InterviewerDailyStats(
                "int-1", "Jean", "Dupont", "ou-1", "Org 1",
                CAMPAIGN_ID, "Campaign One",
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(List.of(stats));

        CampaignProgressByInterviewers result = service.getProgressionForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.interviewers()).hasSize(1);
        assertThat(result.interviewers().getFirst().interviewerLabel()).isEqualTo("Jean Dupont");
    }

    @Test
    void shouldMapInterviewerSurveyUnitsAndProgressRate() throws CampaignNotFoundException {
        // total=100, tbr=20, fin=40, clo=12 → progress = (20+40+12)/100*100 = 72%
        InterviewerDailyStats stats = new InterviewerDailyStats(
                "int-1", "Jean", "Dupont", "ou-1", "Org 1",
                CAMPAIGN_ID, "Campaign One",
                1L, 5L, 2L, 3L,
                4L,   // vic
                6L,   // prc
                7L,   // aoc
                8L,   // aps
                9L,   // ins
                6L,   // wft
                11L,  // wfs
                20L,  // tbr
                40L,  // fin
                12L,  // clo
                8L,   // nva
                9L,   // unaffected
                100L, // total
                15L,  // notice
                25L   // reminder
        );
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any())).thenReturn(List.of(stats));

        CampaignProgressByInterviewers result = service.getProgressionForDay(USER_ID, CAMPAIGN_ID, DAY);

        CampaignProgressByInterviewers.Interviewer interviewer = result.interviewers().getFirst();
        assertThat(interviewer.progressRate()).isCloseTo(72.0f, within(0.001f));

        StatesProgress states = interviewer.states();
        CommunicationsProgress communications = interviewer.communications();
        assertThat(states.allocated()).isEqualTo(100L);         // total
        assertThat(states.notStarted()).isEqualTo(4L);          // vic
        assertThat(states.inProgress()).isEqualTo(30L);         // prc+aoc+aps+ins = 6+7+8+9
        assertThat(states.pendingTransmission()).isEqualTo(6L); // wft
        assertThat(states.toReview()).isEqualTo(20L);           // tbr
        assertThat(states.validated()).isEqualTo(52L);          // fin+clo = 40+12
        assertThat(states.preparingContact()).isEqualTo(6L); // prc
        assertThat(states.withContact()).isEqualTo(7L);         // aoc
        assertThat(states.withAppointment()).isEqualTo(8L);     // aps
        assertThat(communications.noticeLetter()).isEqualTo(15L);
        assertThat(communications.reminderLetter()).isEqualTo(25L);
    }

    @Test
    void shouldMapSiteAndCampaignStats_whenStatsExist() throws CampaignNotFoundException {
        CampaignDailyStats campaignStat = new CampaignDailyStats(
                CAMPAIGN_ID, "Campaign One",
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                10L, 30L, 10L,  // tbr=10, fin=30, clo=10
                0, 0,
                100L, // total
                0, 0);

        when(statsRepository.findCampaignStatsForOrganizationUnits(anyString(), anyList(), any()))
                .thenReturn(Optional.of(campaignStat));
        when(statsRepository.findCampaignStats(anyString(), any()))
                .thenReturn(Optional.of(campaignStat));

        CampaignProgressByInterviewers result = service.getProgressionForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.site().progressRate()).isCloseTo(50.0f, within(0.001f));
        assertThat(result.site().states().allocated()).isEqualTo(100L);
        assertThat(result.campaign().progressRate()).isCloseTo(50.0f, within(0.001f));
        assertThat(result.campaign().states().allocated()).isEqualTo(100L);
    }

    @Test
    void shouldReturnMultipleInterviewers() throws CampaignNotFoundException {
        InterviewerDailyStats stats1 = new InterviewerDailyStats(
                "int-1", "Jean", "Dupont", "ou-1", "Org 1",
                CAMPAIGN_ID, "Campaign One",
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0);
        InterviewerDailyStats stats2 = new InterviewerDailyStats(
                "int-2", "Marie", "Martin", "ou-1", "Org 1",
                CAMPAIGN_ID, "Campaign One",
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 0);
        when(statsRepository.getInterviewerStats(anyString(), anyList(), any()))
                .thenReturn(List.of(stats1, stats2));

        CampaignProgressByInterviewers result = service.getProgressionForDay(USER_ID, CAMPAIGN_ID, DAY);

        assertThat(result.interviewers()).hasSize(2);
        assertThat(result.interviewers())
                .extracting(CampaignProgressByInterviewers.Interviewer::interviewerLabel)
                .containsExactly("Jean Dupont", "Marie Martin");
    }
}
