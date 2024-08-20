package fr.insee.pearljam.infrastructure.campaign.entity;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisibilityDBTest {
    private Campaign campaign;
    private OrganizationUnit organizationUnit;

    @BeforeEach
    void setup() {
        campaign = new Campaign("id", "label", IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F,
                "email@plop.com", true);
        organizationUnit = new OrganizationUnit("OU-SOUTHWEST", "South west", OrganizationUnitType.LOCAL);
    }

    @Test
    @DisplayName("Should return model object")
    void testToModel01() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L);

        Visibility visibility = VisibilityDB.toModel(visibilityDB);
        assertThat(visibility.campaignId()).isEqualTo(visibilityDB.getVisibilityId().getCampaignId());
        assertThat(visibility.organizationalUnitId()).isEqualTo(visibilityDB.getVisibilityId().getOrganizationUnitId());
        assertThat(visibility.managementStartDate()).isEqualTo(visibilityDB.getManagementStartDate());
        assertThat(visibility.interviewerStartDate()).isEqualTo(visibilityDB.getInterviewerStartDate());
        assertThat(visibility.identificationPhaseStartDate()).isEqualTo(visibilityDB.getIdentificationPhaseStartDate());
        assertThat(visibility.collectionStartDate()).isEqualTo(visibilityDB.getCollectionStartDate());
        assertThat(visibility.collectionEndDate()).isEqualTo(visibilityDB.getCollectionEndDate());
        assertThat(visibility.endDate()).isEqualTo(visibilityDB.getEndDate());
    }

    @Test
    @DisplayName("Should return entity object")
    void testFromModel01() {
        Visibility visibility = generateVisibility(1L, 2L, 3L,
                4L, 5L, 6L);

        VisibilityDB visibilityDB = VisibilityDB.fromModel(visibility, campaign, organizationUnit);
        assertThat(visibilityDB.getVisibilityId().getCampaignId()).isEqualTo(visibility.campaignId());
        assertThat(visibilityDB.getCampaign()).isEqualTo(campaign);
        assertThat(visibilityDB.getOrganizationUnit()).isEqualTo(organizationUnit);
        assertThat(visibilityDB.getVisibilityId().getOrganizationUnitId()).isEqualTo(visibility.organizationalUnitId());
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibility.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibility.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibility.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibility.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibility.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibility.endDate());
    }

    @Test
    @DisplayName("Should update dates from entity object")
    void testUpdateDates01() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L);
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                14L, 15L, 16L);

        visibilityDB.updateDates(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
    }

    @Test
    @DisplayName("Should not update management start date")
    void testUpdateDates02() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L);
        Visibility visibilityToUpdate = generateVisibility(null, 12L, 13L,
                14L, 15L, 16L);

        visibilityDB.updateDates(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isNotNull();
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
    }

    @Test
    @DisplayName("Should not update interviewer start date")
    void testUpdateDates03() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L);
        Visibility visibilityToUpdate = generateVisibility(11L, null, 13L,
                14L, 15L, 16L);

        visibilityDB.updateDates(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isNotNull();
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
    }

    @Test
    @DisplayName("Should not update identification start date")
    void testUpdateDates04() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L);
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, null,
                14L, 15L, 16L);

        visibilityDB.updateDates(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isNotNull();
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
    }

    @Test
    @DisplayName("Should not update collection start date")
    void testUpdateDates05() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L);
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                null, 15L, 16L);

        visibilityDB.updateDates(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isNotNull();
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
    }

    @Test
    @DisplayName("Should not update collection end date")
    void testUpdateDates06() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L);
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                14L, null, 16L);

        visibilityDB.updateDates(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isNotNull();
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
    }

    @Test
    @DisplayName("Should not update end date")
    void testUpdateDates07() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L);
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                14L, 15L, null);

        visibilityDB.updateDates(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isNotNull();
    }

    private Visibility generateVisibility(Long managementStartDate, Long interviewerStartDate, Long identificationPhaseStartDate,
                                          Long collectionStartDate, Long collectionEndDate, Long endDate) {
        return new Visibility(campaign.getId(), organizationUnit.getId(), managementStartDate, interviewerStartDate,
                identificationPhaseStartDate, collectionStartDate, collectionEndDate, endDate);

    }
}