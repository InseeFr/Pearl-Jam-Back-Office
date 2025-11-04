package fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity;

import fr.insee.pearljam.campaign.domain.model.ContactAttemptConfiguration;
import fr.insee.pearljam.campaign.domain.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Campaign;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.VisibilityDB;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.VisibilityDBId;
import fr.insee.pearljam.campaign.domain.model.Visibility;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.OrganizationUnit;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.OrganizationUnitType;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.IdentificationConfiguration;
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
                "email@plop.com", false);
        organizationUnit = new OrganizationUnit("OU-SOUTHWEST", "South west", OrganizationUnitType.LOCAL);
    }

    @Test
    @DisplayName("Should return model object")
    void testToModel01() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, true, "mail", "tel");

        Visibility visibility = VisibilityDB.toModel(visibilityDB);
        assertThat(visibility.campaignId()).isEqualTo(visibilityDB.getVisibilityId().getCampaignId());
        assertThat(visibility.organizationalUnitId()).isEqualTo(visibilityDB.getVisibilityId().getOrganizationUnitId());
        assertThat(visibility.managementStartDate()).isEqualTo(visibilityDB.getManagementStartDate());
        assertThat(visibility.interviewerStartDate()).isEqualTo(visibilityDB.getInterviewerStartDate());
        assertThat(visibility.identificationPhaseStartDate()).isEqualTo(visibilityDB.getIdentificationPhaseStartDate());
        assertThat(visibility.collectionStartDate()).isEqualTo(visibilityDB.getCollectionStartDate());
        assertThat(visibility.collectionEndDate()).isEqualTo(visibilityDB.getCollectionEndDate());
        assertThat(visibility.endDate()).isEqualTo(visibilityDB.getEndDate());
        assertThat(visibility.useLetterCommunication().booleanValue()).isEqualTo(visibilityDB.isUseLetterCommunication());
        assertThat(visibility.mail()).isEqualTo(visibilityDB.getMail());
        assertThat(visibility.tel()).isEqualTo(visibilityDB.getTel());
    }

    @Test
    @DisplayName("Should return entity object")
    void testFromModel01() {
        Visibility visibility = generateVisibility(1L, 2L, 3L,
                4L, 5L, 6L, true, "mail", "tel");

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
        assertThat(visibilityDB.isUseLetterCommunication()).isEqualTo(visibility.useLetterCommunication().booleanValue());
        assertThat(visibilityDB.getMail()).isEqualTo(visibility.mail());
        assertThat(visibilityDB.getTel()).isEqualTo(visibility.tel());
    }

    @Test
    @DisplayName("Should update infos from entity object")
    void testUpdate01() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, true, "mail", "tel");
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                14L, 15L, 16L, false, "mailUpdate", "telUpdate");

        visibilityDB.update(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
        assertThat(visibilityDB.isUseLetterCommunication()).isEqualTo(visibilityToUpdate.useLetterCommunication().booleanValue());
        assertThat(visibilityDB.getMail()).isEqualTo(visibilityToUpdate.mail());
        assertThat(visibilityDB.getTel()).isEqualTo(visibilityToUpdate.tel());
    }

    @Test
    @DisplayName("Should not update management start date")
    void testUpdate02() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, true, "mail", "tel");
        Visibility visibilityToUpdate = generateVisibility(null, 12L, 13L,
                14L, 15L, 16L, true, "mail", "tel");

        visibilityDB.update(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isNotNull();
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
        assertThat(visibilityDB.isUseLetterCommunication()).isEqualTo(visibilityToUpdate.useLetterCommunication().booleanValue());
        assertThat(visibilityDB.getMail()).isEqualTo(visibilityToUpdate.mail());
        assertThat(visibilityDB.getTel()).isEqualTo(visibilityToUpdate.tel());
    }

    @Test
    @DisplayName("Should not update interviewer start date")
    void testUpdate03() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, true, "mail", "tel");
        Visibility visibilityToUpdate = generateVisibility(11L, null, 13L,
                14L, 15L, 16L, true, "mail", "tel");

        visibilityDB.update(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isNotNull();
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
        assertThat(visibilityDB.isUseLetterCommunication()).isEqualTo(visibilityToUpdate.useLetterCommunication().booleanValue());
        assertThat(visibilityDB.getMail()).isEqualTo(visibilityToUpdate.mail());
        assertThat(visibilityDB.getTel()).isEqualTo(visibilityToUpdate.tel());
    }

    @Test
    @DisplayName("Should not update identification start date")
    void testUpdate04() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, true, "mail", "tel");
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, null,
                14L, 15L, 16L, true, "mail", "tel");

        visibilityDB.update(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isNotNull();
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
        assertThat(visibilityDB.getMail()).isEqualTo(visibilityToUpdate.mail());
        assertThat(visibilityDB.getTel()).isEqualTo(visibilityToUpdate.tel());
    }

    @Test
    @DisplayName("Should not update collection start date")
    void testUpdate05() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, true, "mail", "tel");
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                null, 15L, 16L, true, "mail", "tel");

        visibilityDB.update(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isNotNull();
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
    }

    @Test
    @DisplayName("Should not update collection end date")
    void testUpdate06() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, true, "mail", "tel");
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                14L, null, 16L, true, "mail", "tel");

        visibilityDB.update(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isNotNull();
        assertThat(visibilityDB.getEndDate()).isEqualTo(visibilityToUpdate.endDate());
        assertThat(visibilityDB.isUseLetterCommunication()).isEqualTo(visibilityToUpdate.useLetterCommunication().booleanValue());
        assertThat(visibilityDB.getMail()).isEqualTo(visibilityToUpdate.mail());
        assertThat(visibilityDB.getTel()).isEqualTo(visibilityToUpdate.tel());
    }

    @Test
    @DisplayName("Should not update end date")
    void testUpdate07() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, true, "mail", "tel");
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                14L, 15L, null, true, "mail", "tel");

        visibilityDB.update(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isNotNull();
        assertThat(visibilityDB.isUseLetterCommunication()).isEqualTo(visibilityToUpdate.useLetterCommunication().booleanValue());
        assertThat(visibilityDB.getMail()).isEqualTo(visibilityToUpdate.mail());
        assertThat(visibilityDB.getTel()).isEqualTo(visibilityToUpdate.tel());
    }

    @Test
    @DisplayName("Should not update communication informations")
    void testUpdate08() {
        VisibilityDB visibilityDB = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                1L, 2L,
                3L, 4L, 5L, 6L, false, "mail", "tel");
        Visibility visibilityToUpdate = generateVisibility(11L, 12L, 13L,
                14L, 15L, null, null, null, null);

        visibilityDB.update(visibilityToUpdate);
        assertThat(visibilityDB.getManagementStartDate()).isEqualTo(visibilityToUpdate.managementStartDate());
        assertThat(visibilityDB.getInterviewerStartDate()).isEqualTo(visibilityToUpdate.interviewerStartDate());
        assertThat(visibilityDB.getIdentificationPhaseStartDate()).isEqualTo(visibilityToUpdate.identificationPhaseStartDate());
        assertThat(visibilityDB.getCollectionStartDate()).isEqualTo(visibilityToUpdate.collectionStartDate());
        assertThat(visibilityDB.getCollectionEndDate()).isEqualTo(visibilityToUpdate.collectionEndDate());
        assertThat(visibilityDB.getEndDate()).isNotNull();
        assertThat(visibilityDB.isUseLetterCommunication()).isFalse();
        assertThat(visibilityDB.getMail()).isEqualTo("mail");
        assertThat(visibilityDB.getTel()).isEqualTo("tel");
    }

    private Visibility generateVisibility(Long managementStartDate, Long interviewerStartDate, Long identificationPhaseStartDate,
                                          Long collectionStartDate, Long collectionEndDate, Long endDate, Boolean useLetterCommunication,
                                          String mail, String tel) {
        return new Visibility(campaign.getId(), organizationUnit.getId(), managementStartDate, interviewerStartDate,
                identificationPhaseStartDate, collectionStartDate, collectionEndDate, endDate, useLetterCommunication, mail, tel);

    }
}
