package fr.insee.pearljam.domain.reporting;

import fr.insee.pearljam.domain.campaign.stub.CampaignProgressionRepositoryStub;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnit;
import fr.insee.pearljam.domain.reporting.projection.*;
import fr.insee.pearljam.domain.reporting.service.CampaignProgressionService;
import fr.insee.pearljam.domain.user.stub.UserServiceStub;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

class CampaignProgressionServiceTest {

    static final Instant FIXED_DATE = Instant.ofEpochMilli(1_000_000L);
    static final OrganizationUnit ORG_UNIT = new OrganizationUnit("ou-1", "Org Unit 1");

    static StateCountProjection stateCount(String campaignId) {
        return new StateCountProjection(
                campaignId,
                1L,
                5L,
                2L,
                3L,
                4L,
                6L,
                7L,
                8L,
                9L,
                10L,
                11L,
                20L,
                40L,
                12L,
                13L,
                100L
        );
    }

    static CommunicationRequestCountProjection commCount(String campaignId) {
        return new CommunicationRequestCountProjection(campaignId, 15L, 25L);
    }

    private CampaignProgressionService buildService(
            List<OrganizationUnit> orgUnits,
            List<CampaignProjection> campaigns,
            List<StateCountProjection> stateCounts,
            List<CommunicationRequestCountProjection> commCounts) {
        return new CampaignProgressionService(
                new CampaignProgressionRepositoryStub(campaigns, stateCounts, commCounts),
                new UserServiceStub(orgUnits)
        );
    }

    // --- Tests ---

    @Test
    void shouldReturnEmptyList_whenUserHasNoOrganizationUnits() {
        CampaignProgressionService service = buildService(List.of(), List.of(), List.of(), List.of());

        List<CampaignProgressionProjection> result = service.getCampaignsProgression("user-1", FIXED_DATE);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoCampaignsForOrgUnits() {
        CampaignProgressionService service = buildService(List.of(ORG_UNIT), List.of(), List.of(), List.of());

        List<CampaignProgressionProjection> result = service.getCampaignsProgression("user-1", FIXED_DATE);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOneCampaignProjection_whenOneCampaignExists() {
        CampaignProjection campaign = new CampaignProjection("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of(stateCount("campaign-1")),
                List.of(commCount("campaign-1"))
        );

        List<CampaignProgressionProjection> result = service.getCampaignsProgression("user-1", FIXED_DATE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().campaignId()).isEqualTo("campaign-1");
        assertThat(result.getFirst().campaignLabel()).isEqualTo("Campaign One");
    }

    @Test
    void shouldComputeProgressRateCorrectly() {
        // total=100, fin=40, tbr=20 → (40+20)/100*100 = 60%
        CampaignProjection campaign = new CampaignProjection("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of(stateCount("campaign-1")),
                List.of(commCount("campaign-1"))
        );

        List<CampaignProgressionProjection> result = service.getCampaignsProgression("user-1", FIXED_DATE);

        assertThat(result.getFirst().progressRate()).isCloseTo(60.0f, within(0.001f));
    }

    @Test
    void shouldMapSurveyUnitCountsCorrectly() {
        CampaignProjection campaign = new CampaignProjection("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of(stateCount("campaign-1")),
                List.of(commCount("campaign-1"))
        );

        CampaignProgressionProjection.SurveyUnits su =
                service.getCampaignsProgression("user-1", FIXED_DATE).getFirst().surveyUnits();

        assertThat(su.allocated()).isEqualTo(100L);
        assertThat(su.notStarted()).isEqualTo(5L);
        assertThat(su.inProgress()).isEqualTo(9L);
        assertThat(su.pendingTransmission()).isEqualTo(10L);
        assertThat(su.toReview()).isEqualTo(20L);
        assertThat(su.validated()).isEqualTo(40L);
        assertThat(su.preparingContact()).isEqualTo(6L);
        assertThat(su.atLeastOneContact()).isEqualTo(7L);
        assertThat(su.appoLongmentScheduled()).isEqualTo(8L);
        assertThat(su.started()).isEqualTo(40L);
        assertThat(su.noticeLetter()).isEqualTo(15L);
        assertThat(su.reminderLetter()).isEqualTo(25L);
    }

    @Test
    void shouldUseEmptyStateCounts_whenNoStateCountForCampaign() {
        // No stateCount for campaign-1 → StateCountProjection.empty() used → all zeros
        CampaignProjection campaign = new CampaignProjection("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of(), // no state counts
                List.of(commCount("campaign-1"))
        );

        List<CampaignProgressionProjection> result = service.getCampaignsProgression("user-1", FIXED_DATE);

        // progressRate = (0 + 0) / 0 * 100 = NaN (division by zero)
        assertThat(result.getFirst().surveyUnits().allocated()).isZero();
        assertThat(result.getFirst().surveyUnits().noticeLetter()).isEqualTo(15L);
    }

    @Test
    void shouldUseEmptyCommunicationCounts_whenNoCommCountForCampaign() {
        CampaignProjection campaign = new CampaignProjection("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(campaign),
                List.of(stateCount("campaign-1")),
                List.of() // no comm counts
        );

        CampaignProgressionProjection.SurveyUnits su =
                service.getCampaignsProgression("user-1", FIXED_DATE).getFirst().surveyUnits();

        assertThat(su.noticeLetter()).isZero();
        assertThat(su.reminderLetter()).isZero();
    }

    @Test
    void shouldReturnMultipleCampaigns_whenMultipleExist() {
        CampaignProjection c1 = new CampaignProjection("campaign-1", "Campaign One");
        CampaignProjection c2 = new CampaignProjection("campaign-2", "Campaign Two");

        CampaignProgressionService service = buildService(
                List.of(ORG_UNIT),
                List.of(c1, c2),
                List.of(stateCount("campaign-1"), stateCount("campaign-2")),
                List.of(commCount("campaign-1"), commCount("campaign-2"))
        );

        List<CampaignProgressionProjection> result = service.getCampaignsProgression("user-1", FIXED_DATE);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CampaignProgressionProjection::campaignId)
                .containsExactlyInAnyOrder("campaign-1", "campaign-2");
    }

    @Test
    void shouldHandleMultipleOrgUnits() {
        OrganizationUnit ou1 = new OrganizationUnit("ou-1", "Org 1");
        OrganizationUnit ou2 = new OrganizationUnit("ou-2", "Org 2");
        CampaignProjection campaign = new CampaignProjection("campaign-1", "Campaign One");

        CampaignProgressionService service = buildService(
                List.of(ou1, ou2),
                List.of(campaign),
                List.of(stateCount("campaign-1")),
                List.of(commCount("campaign-1"))
        );

        List<CampaignProgressionProjection> result = service.getCampaignsProgression("user-1", FIXED_DATE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().campaignId()).isEqualTo("campaign-1");
    }
}