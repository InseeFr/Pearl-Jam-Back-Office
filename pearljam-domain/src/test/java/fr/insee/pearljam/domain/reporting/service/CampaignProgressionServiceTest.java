package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.campaign.stub.CampaignDailyStatsRepositoryPortStub;
import fr.insee.pearljam.domain.campaign.stub.CampaignRepositoryStub;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignProgression;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.user.stub.UserServiceStub;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

class CampaignProgressionServiceTest {

    static final LocalDate DAY = LocalDate.of(2025, 1, 15);
    static final OrganizationUnitSummary ORG_UNIT = new OrganizationUnitSummary("ou-1", "Org Unit 1");

    static CampaignDailyStats dailyStats(String campaignId, String campaignLabel) {
        return new CampaignDailyStats(
                campaignId, campaignLabel,
                1L,   // nvm
                5L,   // nns
                2L,   // anv
                3L,   // vin
                4L,   // vic
                6L,   // prc
                7L,   // aoc
                8L,   // aps
                9L,   // ins
                6L,  // wft
                11L,  // wfs
                20L,  // tbr
                40L,  // fin
                12L,  // clo
                8L,  // nva
                9L,
                100L, // total
                15L,  // notice
                25L   // reminder
        );
    }

    private CampaignProgressionService buildService(
            List<OrganizationUnitSummary> orgUnits,
            List<CampaignSummary> campaigns,
            List<CampaignDailyStats> dailyStatsList) {
        return new CampaignProgressionService(
                new CampaignRepositoryStub(campaigns),
                new CampaignDailyStatsRepositoryPortStub(dailyStatsList),
                new UserServiceStub(orgUnits)
        );
    }

    @Test
    void shouldReturnEmptyList_whenUserHasNoOrganizationUnits() {
        CampaignProgressionService service = buildService(List.of(), List.of(), List.of());

        List<CampaignProgression> result = service.getCampaignsProgression("user-1", DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoCampaignsForOrgUnits() {
        CampaignProgressionService service = buildService(List.of(ORG_UNIT), List.of(), List.of());

        List<CampaignProgression> result = service.getCampaignsProgression("user-1", DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOneCampaignProjection_whenOneCampaignExists() {
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of(dailyStats("campaign-1", "Campaign One"))
        );

        List<CampaignProgression> result = service.getCampaignsProgression("user-1", DAY);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().campaignId()).isEqualTo("campaign-1");
        assertThat(result.getFirst().campaignLabel()).isEqualTo("Campaign One");
    }

    @Test
    void shouldComputeProgressRateCorrectly() {
        // total=100, fin=40, tbr=20, clo=12 → (40+20+12)/100*100 = 72%
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of(dailyStats("campaign-1", "Campaign One"))
        );

        List<CampaignProgression> result = service.getCampaignsProgression("user-1", DAY);

        assertThat(result.getFirst().progressRate()).isCloseTo(72.0f, within(0.001f));
    }

    @Test
    void shouldMapSurveyUnitCountsCorrectly() {
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of(dailyStats("campaign-1", "Campaign One"))
        );

        CampaignProgression.CampaignProgressionSurveyUnits su =
                service.getCampaignsProgression("user-1", DAY).getFirst().surveyUnits();

        assertThat(su.allocated()).isEqualTo(100L);        // total
        assertThat(su.notStarted()).isEqualTo(4L);         // vic
        assertThat(su.inProgress()).isEqualTo(30L);        // prc+aoc+aps+ins = 6+7+8+9
        assertThat(su.pendingTransmission()).isEqualTo(6L);// wft
        assertThat(su.toReview()).isEqualTo(20L);          // tbr
        assertThat(su.validated()).isEqualTo(52L);         // fin+clo = 40+12
        assertThat(su.preparingContact()).isEqualTo(6L);   // prc
        assertThat(su.atLeastOneContact()).isEqualTo(7L);  // aoc
        assertThat(su.appoLongmentScheduled()).isEqualTo(8L); // aps
        assertThat(su.noticeLetter()).isEqualTo(15L);      // notice
        assertThat(su.reminderLetter()).isEqualTo(25L);    // reminder
    }

    @Test
    void shouldUseZeroCounts_whenNoSnapshotForCampaign() {
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of()
        );

        List<CampaignProgression> result = service.getCampaignsProgression("user-1", DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnMultipleCampaigns_whenMultipleExist() {
        CampaignSummary c1 = new CampaignSummary("campaign-1", "Campaign One");
        CampaignSummary c2 = new CampaignSummary("campaign-2", "Campaign Two");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(c1, c2),
                List.of(dailyStats("campaign-1", "Campaign One"), dailyStats("campaign-2", "Campaign Two"))
        );

        List<CampaignProgression> result = service.getCampaignsProgression("user-1", DAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CampaignProgression::campaignId)
                .containsExactlyInAnyOrder("campaign-1", "campaign-2");
    }

    @Test
    void shouldHandleMultipleOrgUnits() {
        OrganizationUnitSummary ou2 = new OrganizationUnitSummary("ou-2", "Org 2");
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT, ou2),
                List.of(campaign),
                List.of(dailyStats("campaign-1", "Campaign One"))
        );

        List<CampaignProgression> result = service.getCampaignsProgression("user-1", DAY);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().campaignId()).isEqualTo("campaign-1");
    }
}
