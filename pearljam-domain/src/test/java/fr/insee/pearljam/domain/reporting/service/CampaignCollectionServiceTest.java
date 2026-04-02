package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.collect.CampaignCollection;
import fr.insee.pearljam.domain.reporting.readmodel.collect.ClosingCausesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.collect.CollectionRates;
import fr.insee.pearljam.domain.reporting.readmodel.collect.ContactOutcomesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampaignCollectionServiceTest {

    static final LocalDate DAY = LocalDate.of(2025, 1, 15);
    static final String USER_ID = "user-1";
    static final OrganizationUnitSummary ORG_UNIT = new OrganizationUnitSummary("ou-1", "Org Unit 1");

    CampaignRepository campaignRepository;
    CampaignDailyStatsRepositoryPort statsRepository;
    UserService userService;
    CampaignCollectionService service;

    @BeforeEach
    void setup() {
        campaignRepository = mock(CampaignRepository.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        userService = mock(UserService.class);
        service = new CampaignCollectionService(campaignRepository, statsRepository, userService);

        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(ORG_UNIT));
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of());
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of());
    }

    @Test
    void shouldReturnEmptyList_whenUserHasNoOrganizationUnits() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of());

        List<CampaignCollection> result = service.getCampaignsCollection(USER_ID, DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoCampaignsForOrgUnits() {
        List<CampaignCollection> result = service.getCampaignsCollection(USER_ID, DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoStatsForCampaign() {
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));

        List<CampaignCollection> result = service.getCampaignsCollection(USER_ID, DAY);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnCampaignWithCorrectIdAndLabel() {
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");
        CampaignDailyStats stats = dailyStats("campaign-1", "Campaign One");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        List<CampaignCollection> result = service.getCampaignsCollection(USER_ID, DAY);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().campaignId()).isEqualTo("campaign-1");
        assertThat(result.getFirst().campaignLabel()).isEqualTo("Campaign One");
    }

    @Test
    void shouldMapAllocatedAndCollectionRatesCorrectly() {
        // allocated = 100, ina=20 → collectionRate = 20%
        // ref=5, imp=3, npi=2 → wasteRate = (5+3+2)/100 = 10%
        // outOfScope contacts = ucd+utr+ala+duk+nuh+noa = 1+1+1+1+1+1 = 6
        // npx=3, row=1 → outOfScopeRate = (6+3+1)/100 = 10%
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");
        CampaignDailyStats stats = dailyStats("campaign-1", "Campaign One");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        CampaignCollection collection = service.getCampaignsCollection(USER_ID, DAY).getFirst();

        assertThat(collection.allocated()).isEqualTo(100L);

        CollectionRates rates = collection.rates();
        assertThat(rates.collection()).isCloseTo(20.0f, within(0.01f));
        assertThat(rates.waste()).isCloseTo(10.0f, within(0.01f));
        assertThat(rates.outOfScope()).isCloseTo(10.0f, within(0.01f));
    }

    @Test
    void shouldMapContactOutcomesCorrectly() {
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");
        CampaignDailyStats stats = dailyStats("campaign-1", "Campaign One");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        ContactOutcomesProgress outcomes = service.getCampaignsCollection(USER_ID, DAY)
                .getFirst().outcomes();

        assertThat(outcomes.accepted()).isEqualTo(20L);     // ina
        assertThat(outcomes.refused()).isEqualTo(5L);       // ref
        assertThat(outcomes.unreachable()).isEqualTo(3L);   // imp
        assertThat(outcomes.outOfScope()).isEqualTo(6L);    // ucd+utr+ala+duk+nuh+noa
        assertThat(outcomes.total()).isEqualTo(34L);        // ina+ref+imp+outOfScope
    }

    @Test
    void shouldMapClosingCausesCorrectly() {
        CampaignSummary campaign = new CampaignSummary("campaign-1", "Campaign One");
        CampaignDailyStats stats = dailyStats("campaign-1", "Campaign One");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(campaign));
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(stats));

        ClosingCausesProgress closingCauses = service.getCampaignsCollection(USER_ID, DAY)
                .getFirst().closingCauses();

        assertThat(closingCauses.absenceInterviewer()).isEqualTo(4L);  // npa
        assertThat(closingCauses.otherReasons()).isEqualTo(6L);        // npi+npx+row = 2+3+1
        assertThat(closingCauses.totalClosed()).isEqualTo(10L);        // npa + others
    }

    @Test
    void shouldReturnMultipleCampaigns() {
        CampaignSummary c1 = new CampaignSummary("campaign-1", "Campaign One");
        CampaignSummary c2 = new CampaignSummary("campaign-2", "Campaign Two");

        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
                .thenReturn(List.of(c1, c2));
        when(statsRepository.getCampaignsStats(anyList(), anyList(), any()))
                .thenReturn(List.of(
                        dailyStats("campaign-1", "Campaign One"),
                        dailyStats("campaign-2", "Campaign Two")));

        List<CampaignCollection> result = service.getCampaignsCollection(USER_ID, DAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CampaignCollection::campaignId)
                .containsExactlyInAnyOrder("campaign-1", "campaign-2");
    }

    /**
     * Builds a CampaignDailyStats with allocated=100 and meaningful
     * closing cause / contact outcome values for rate assertions.
     */
    static CampaignDailyStats dailyStats(String campaignId, String campaignLabel) {
        return new CampaignDailyStats(
                campaignId, campaignLabel,
                0L,   // nvm
                10L,  // nns
                0L,   // anv
                0L,   // vin
                5L,   // vic
                5L,   // prc
                5L,   // aoc
                5L,   // aps
                5L,   // ins
                5L,   // wft
                0L,   // wfs
                20L,  // tbr
                30L,  // fin
                10L,  // clo
                0L,   // nva
                0L,   // unaffected
                0L,   // notice
                0L,   // reminder
                4L,   // npa
                2L,   // npi
                3L,   // npx
                1L,   // row
                20L,  // ina
                5L,   // ref
                3L,   // imp
                1L,   // ucd
                1L,   // utr
                1L,   // ala
                1L,   // duk
                1L,   // nuh
                1L    // noa
        );
    }
}
