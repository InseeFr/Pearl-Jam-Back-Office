package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.reporting.model.CampaignPhase;
import fr.insee.pearljam.domain.reporting.model.CampaignSummaryWithStateCount;
import fr.insee.pearljam.domain.reporting.port.out.CampaignStateCountRepositoryPort;
import fr.insee.pearljam.domain.reporting.port.out.CampaignSummaryWithStateCountRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.StateCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampaignSummaryWithStateCountServiceTest {

    static final Long FIXED_TIMESTAMP = 1_000_000L;
    static final String USER_ID = "user-1";

    CampaignSummaryWithStateCountRepositoryPort campaignSummaryWithStateCountRepositoryPort;
    CampaignStateCountRepositoryPort campaignStateCountRepositoryPort;
    UserService userService;
    DateService dateService;

    CampaignSummaryWithStateCountService service;

    @BeforeEach
    void setup() {
        campaignSummaryWithStateCountRepositoryPort = mock(CampaignSummaryWithStateCountRepositoryPort.class);
        campaignStateCountRepositoryPort = mock(CampaignStateCountRepositoryPort.class);
        userService = mock(UserService.class);
        dateService = mock(DateService.class);

        service = new CampaignSummaryWithStateCountService(
                campaignSummaryWithStateCountRepositoryPort,
                campaignStateCountRepositoryPort,
                userService,
                dateService
        );

        when(dateService.getCurrentTimestamp()).thenReturn(FIXED_TIMESTAMP);
    }


    @Test
    void shouldReturnEmptyList_whenUserHasNoOrgUnits() {
        when(userService.getUserOUs(USER_ID, true)).thenReturn(List.of());

        List<CampaignSummaryWithStateCount> result = service.getCampaignSummaryWithStateCount(USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenNoCampaignsForOrgUnits() {
        when(userService.getUserOUs(USER_ID, true)).thenReturn(List.of(new OrganizationUnitDto("ou-1", "Org 1")));
        when(campaignSummaryWithStateCountRepositoryPort.findByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of());

        List<CampaignSummaryWithStateCount> result = service.getCampaignSummaryWithStateCount(USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOneCampaign_withCorrectMappingForDatesAndPhase() {
        OrganizationUnitDto ou = new OrganizationUnitDto("ou-1", "OrganizationUnit 1");
        CampaignWithVisibility camp = new CampaignWithVisibility("CAMP1", "Campaign One", 900_000L,
                950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);
        StateCount stateCount = StateCount.empty("CAMP1");

        when(userService.getUserOUs(USER_ID, true)).thenReturn(List.of(ou));
        when(campaignSummaryWithStateCountRepositoryPort.findByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));
        when(campaignStateCountRepositoryPort.getStateCountByCampaignsAndOrganisationUnits(anyList(), anyList(), any()))
                .thenReturn(List.of(stateCount));

        List<CampaignSummaryWithStateCount> result = service.getCampaignSummaryWithStateCount(USER_ID);

        assertThat(result).hasSize(1);

        CampaignSummaryWithStateCount cs = result.getFirst();
        assertThat(cs.campaignId()).isEqualTo("CAMP1");
        assertThat(cs.campaignLabel()).isEqualTo("Campaign One");
        assertThat(cs.collectionStartDate()).isEqualTo(1_050_000L);
        assertThat(cs.collectionEndDate()).isEqualTo(1_100_000L);
        assertThat(cs.endDate()).isEqualTo(1_150_000L);
        assertThat(cs.campaignPhase()).isEqualTo(CampaignPhase.INITIAL_ASSIGNMENT);
        assertThat(cs.surveyUnits().notAssigned()).isZero();
        assertThat(cs.surveyUnits().completed()).isZero();
        assertThat(cs.surveyUnits().toReview()).isZero();
        assertThat(cs.surveyUnits().allocated()).isZero();
        assertThat(cs.surveyUnits().toProcessInterviewer()).isZero();


    }

    @Test
    void shouldUseEmptyStateCount_whenNoStateCountReturned() {
        OrganizationUnitDto ou = new OrganizationUnitDto("ou-1", "OrganizationUnit 1");
        CampaignWithVisibility camp = new CampaignWithVisibility("CAMP1", "Campaign One", 900_000L, 950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);

        when(userService.getUserOUs(USER_ID, true)).thenReturn(List.of(ou));
        when(campaignSummaryWithStateCountRepositoryPort.findByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(camp));
        when(campaignStateCountRepositoryPort.getStateCountByCampaignsAndOrganisationUnits(anyList(), anyList(), any()))
                .thenReturn(List.of());

        List<CampaignSummaryWithStateCount> result = service.getCampaignSummaryWithStateCount(USER_ID);

        CampaignSummaryWithStateCount cs = result.getFirst();

        assertThat(cs.surveyUnits().allocated()).isZero();
        assertThat(cs.surveyUnits().toProcessInterviewer()).isZero();
        assertThat(cs.surveyUnits().toReview()).isZero();
        assertThat(cs.surveyUnits().completed()).isZero();
        assertThat(cs.surveyUnits().notAssigned()).isZero();

    }

    @Test
    void shouldReturnMultipleCampaigns() {
        OrganizationUnitDto ou = new OrganizationUnitDto("ou-1", "OrganizationUnit 1");
        CampaignWithVisibility c1 = new CampaignWithVisibility("CAMP1", "Campaign One", 900_000L, 950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);
        CampaignWithVisibility c2 = new CampaignWithVisibility("CAMP2", "Campaign Two", 900_000L, 950_000L, 1_000_000L, 1_050_000L, 1_100_000L, 1_150_000L);


        when(userService.getUserOUs(USER_ID, true)).thenReturn(List.of(ou));
        when(campaignSummaryWithStateCountRepositoryPort.findByUserAndManagementVisibility(anyList(), anyString(), anyLong()))
                .thenReturn(List.of(c1, c2));
        when(campaignStateCountRepositoryPort.getStateCountByCampaignsAndOrganisationUnits(anyList(), anyList(), any()))
                .thenReturn(List.of(StateCount.empty("CAMP1"), StateCount.empty("CAMP2")));

        List<CampaignSummaryWithStateCount> result = service.getCampaignSummaryWithStateCount(USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CampaignSummaryWithStateCount::campaignId)
                .containsExactlyInAnyOrder("CAMP1", "CAMP2");
    }

    @Test
    void shouldMapStateCountToSurveyUnitsCorrectly() {
        // GIVEN
        StateCount sc = new StateCount(
                "CAMP1",
                1L,  // nvmCount
                2L,  // nnsCount
                3L,  // anvCount
                4L,  // vinCount
                5L,  // vicCount
                6L,  // prcCount
                7L,  // aocCount
                8L,  // apsCount
                9L,  // insCount
                10L, // wftCount
                11L, // wfsCount
                12L, // tbrCount
                13L, // finCount
                14L, // cloCount
                15L, // nvaCount
                100L // total
        );

        // WHEN
        var su = service.toSurveyUnitsStateCount(sc);

        // THEN
        assertThat(su.allocated()).isEqualTo(100L);  // total
        assertThat(su.toProcessInterviewer()).isEqualTo(5 + 6 + 7 + 8 + 9 + 10);  // VIC+PRC+AOC+APS+INS+WFT = 45
        assertThat(su.toReview()).isEqualTo(12L);  // tbrCount
        assertThat(su.completed()).isEqualTo(13 + 14);  // finCount + cloCount = 27
        assertThat(su.notAssigned()).isZero(); // TODO
    }

}