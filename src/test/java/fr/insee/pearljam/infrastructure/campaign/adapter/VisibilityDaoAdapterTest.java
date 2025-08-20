package fr.insee.pearljam.infrastructure.campaign.adapter;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDB;
import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDBId;
import fr.insee.pearljam.infrastructure.campaign.jpa.VisibilityJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
@Slf4j
class VisibilityDaoAdapterTest {
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private SurveyUnitRepository surveyUnitRepository;

    @Autowired
    private OrganizationUnitRepository organizationUnitRepository;

    @Autowired
    private VisibilityDaoAdapter visibilityDaoAdapter;

    @Autowired
    private VisibilityJpaRepository crudRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Campaign campaign;
    private OrganizationUnit organizationUnit;
    private OrganizationUnit organizationUnit2;
    private VisibilityDB visibilityDB1, visibilityDB2;

    @BeforeEach
    void setup() {
        campaign = new Campaign("id", "label", IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F,
                "email@plop.com", false);

        organizationUnit = new OrganizationUnit("OU-SOUTHWEST", "South west", OrganizationUnitType.LOCAL);
        organizationUnit2 = new OrganizationUnit("OU-NORTHWEST", "North west", OrganizationUnitType.NATIONAL);
        campaignRepository.save(campaign);
        organizationUnit = organizationUnitRepository.save(organizationUnit);
        organizationUnit2 = organizationUnitRepository.save(organizationUnit2);
        visibilityDB1 = new VisibilityDB(new VisibilityDBId(organizationUnit.getId(), campaign.getId()), organizationUnit, campaign,
                10L, 20L,
                11L, 30L, 12L, 40L, true, "mail1", "tel1");
        visibilityDB2 = new VisibilityDB(new VisibilityDBId(organizationUnit2.getId(), campaign.getId()), organizationUnit2, campaign,
                 11L, 12L,
                13L, 14L, 15L, 16L, true, "mail2", "tel2");
        List<VisibilityDB> visibilities = new ArrayList<>();
        visibilities.add(visibilityDB1);
        visibilities.add(visibilityDB2);
        campaign.setVisibilities(visibilities);
        campaign = campaignRepository.save(campaign);
    }

    @Test
    @DisplayName("Should update visibility")
    void testUpdateDatesVisibilities01() throws VisibilityNotFoundException {
        Visibility visibilityToUpdate = new Visibility(campaign.getId(), organizationUnit.getId(),
                21L, 22L, 23L,
                24L, 25L, 26L, true, "mail", "tel");
        visibilityDaoAdapter.updateDates(visibilityToUpdate);

        Optional<Campaign> campaignOptional = campaignRepository.findById(campaign.getId());
        assertThat(campaignOptional).isPresent();
        campaign = campaignOptional.get();
        List<VisibilityDB> visibilities = campaign.getVisibilities();

        assertThat(visibilities)
                .hasSize(2)
                .anySatisfy(visibilityDBUpdated -> assertEquals(visibilityDBUpdated, visibilityToUpdate))
                .anySatisfy(visibilityDBNotUpdated -> assertEquals(visibilityDBNotUpdated, visibilityDB2));

        Optional<Visibility> visibilityUpdatedOptional = visibilityDaoAdapter.findVisibility(campaign.getId(), organizationUnit.getId());
        assertThat(visibilityUpdatedOptional).isPresent();
        Visibility visibilityUpdated = visibilityUpdatedOptional.get();
        assertThat(visibilityUpdated).isEqualTo(visibilityToUpdate);
    }

    @Test
    @DisplayName("Should throw exception when visibility doesn't exist")
    void testUpdateDatesVisibility02() {
        Visibility visibilityToUpdate = new Visibility(campaign.getId(), "invalid-id",
                21L, 22L, 23L,
                24L, 25L, 26L, true, "mail", "tel");

        assertThatThrownBy(() -> visibilityDaoAdapter.updateDates(visibilityToUpdate))
                .isInstanceOf(VisibilityNotFoundException.class)
                .hasMessage(VisibilityNotFoundException.MESSAGE);
    }

    @Test
    @DisplayName("Should retrieve campaign visibility")
    void testGetCampaignVisibility01() {
        // Given
        List<String> ouIds = List.of(organizationUnit.getId(),
                organizationUnit2.getId());

        // When
        CampaignVisibility campaignVisibility = visibilityDaoAdapter.getCampaignVisibility(campaign.getId(), ouIds);

        // Then
        assertThat(campaignVisibility).isNotNull();
        assertThat(campaignVisibility.managementStartDate()).isEqualTo(visibilityDB1.getManagementStartDate());
        assertThat(campaignVisibility.interviewerStartDate()).isEqualTo(visibilityDB2.getInterviewerStartDate());
        assertThat(campaignVisibility.identificationPhaseStartDate()).isEqualTo(visibilityDB1.getIdentificationPhaseStartDate());
        assertThat(campaignVisibility.collectionStartDate()).isEqualTo(visibilityDB2.getCollectionStartDate());
        assertThat(campaignVisibility.collectionEndDate()).isEqualTo(visibilityDB2.getCollectionEndDate());
        assertThat(campaignVisibility.endDate()).isEqualTo(visibilityDB1.getEndDate());
    }

    @Test
    @DisplayName("Should find visibility")
    void testFindVisibility() {
        // When
        Optional<Visibility> optionalVisibility = visibilityDaoAdapter.findVisibility(campaign.getId(), organizationUnit.getId());

        // Then
        assertThat(optionalVisibility).isPresent();
        assertEquals(optionalVisibility.get(), visibilityDB1);
    }

    @Test
    @DisplayName("Should find visibilities")
    void testFindVisibilities() {
        // When
        List<Visibility> visibilities = visibilityDaoAdapter.findVisibilities(campaign.getId());

        // Then
        assertThat(visibilities)
                .hasSize(2)
                .anySatisfy(visibility -> assertEquals(visibility, visibilityDB1))
                .anySatisfy(visibility -> assertEquals(visibility, visibilityDB2));
    }

    @Test
    @DisplayName("Should retrieve visibility for a survey unit")
    void testGetVisibilityBySurveyUnitId() {
        // Given
        String surveyUnitId = "SU1";
        SurveyUnit surveyUnit = new SurveyUnit(surveyUnitId, true, true, null,
                null, campaign, null, organizationUnit, null);
        surveyUnitRepository.save(surveyUnit);

        // When
        Visibility visibility = visibilityDaoAdapter.getVisibilityBySurveyUnitId(surveyUnitId);

        // Then
        assertThat(visibility).isNotNull();
        assertEquals(visibility, visibilityDB1);
    }

    private void assertEquals(Visibility visibilityToCheck, VisibilityDB visibilityDBExpected) {
        assertThat(visibilityToCheck.campaignId()).isEqualTo(visibilityDBExpected.getCampaign().getId());
        assertThat(visibilityToCheck.organizationalUnitId()).isEqualTo(visibilityDBExpected.getOrganizationUnit().getId());
        assertThat(visibilityToCheck.managementStartDate()).isEqualTo(visibilityDBExpected.getManagementStartDate());
        assertThat(visibilityToCheck.interviewerStartDate()).isEqualTo(visibilityDBExpected.getInterviewerStartDate());
        assertThat(visibilityToCheck.identificationPhaseStartDate()).isEqualTo(visibilityDBExpected.getIdentificationPhaseStartDate());
        assertThat(visibilityToCheck.collectionStartDate()).isEqualTo(visibilityDBExpected.getCollectionStartDate());
        assertThat(visibilityToCheck.collectionEndDate()).isEqualTo(visibilityDBExpected.getCollectionEndDate());
        assertThat(visibilityToCheck.endDate()).isEqualTo(visibilityDBExpected.getEndDate());
        assertThat(visibilityToCheck.mail()).isEqualTo(visibilityDBExpected.getMail());
        assertThat(visibilityToCheck.tel()).isEqualTo(visibilityDBExpected.getTel());
    }

    private void assertEquals(VisibilityDB visibilityDBToCheck, Visibility visibilityExpected) {
        assertThat(visibilityDBToCheck.getCampaign().getId()).isEqualTo(visibilityExpected.campaignId());
        assertThat(visibilityDBToCheck.getOrganizationUnit().getId()).isEqualTo(visibilityExpected.organizationalUnitId());
        assertThat(visibilityDBToCheck.getManagementStartDate()).isEqualTo(visibilityExpected.managementStartDate());
        assertThat(visibilityDBToCheck.getInterviewerStartDate()).isEqualTo(visibilityExpected.interviewerStartDate());
        assertThat(visibilityDBToCheck.getIdentificationPhaseStartDate()).isEqualTo(visibilityExpected.identificationPhaseStartDate());
        assertThat(visibilityDBToCheck.getCollectionStartDate()).isEqualTo(visibilityExpected.collectionStartDate());
        assertThat(visibilityDBToCheck.getCollectionEndDate()).isEqualTo(visibilityExpected.collectionEndDate());
        assertThat(visibilityDBToCheck.getEndDate()).isEqualTo(visibilityExpected.endDate());
        assertThat(visibilityDBToCheck.getMail()).isEqualTo(visibilityExpected.mail());
        assertThat(visibilityDBToCheck.getTel()).isEqualTo(visibilityExpected.tel());
    }

    private void assertEquals(VisibilityDB visibilityDBToCheck, VisibilityDB visibilityExpected) {
        assertThat(visibilityDBToCheck.getCampaign().getId()).isEqualTo(visibilityExpected.getCampaign().getId());
        assertThat(visibilityDBToCheck.getOrganizationUnit().getId()).isEqualTo(visibilityExpected.getOrganizationUnit().getId());
        assertThat(visibilityDBToCheck.getManagementStartDate()).isEqualTo(visibilityExpected.getManagementStartDate());
        assertThat(visibilityDBToCheck.getInterviewerStartDate()).isEqualTo(visibilityExpected.getInterviewerStartDate());
        assertThat(visibilityDBToCheck.getIdentificationPhaseStartDate()).isEqualTo(visibilityExpected.getIdentificationPhaseStartDate());
        assertThat(visibilityDBToCheck.getCollectionStartDate()).isEqualTo(visibilityExpected.getCollectionStartDate());
        assertThat(visibilityDBToCheck.getCollectionEndDate()).isEqualTo(visibilityExpected.getCollectionEndDate());
        assertThat(visibilityDBToCheck.getEndDate()).isEqualTo(visibilityExpected.getEndDate());
        assertThat(visibilityDBToCheck.getMail()).isEqualTo(visibilityExpected.getMail());
        assertThat(visibilityDBToCheck.getTel()).isEqualTo(visibilityExpected.getTel());
    }
}
