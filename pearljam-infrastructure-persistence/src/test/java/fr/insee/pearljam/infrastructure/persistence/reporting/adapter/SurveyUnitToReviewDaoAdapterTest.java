package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitToReview;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CampaignJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.contactoutcome.jpa.ContactOutcomeJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.OrganizationUnitJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter.CommentDaoAdapter;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.ContactOutcomeDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.StateDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.InterviewerJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.StateJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
class SurveyUnitToReviewDaoAdapterTest {

    @Autowired
    private SurveyUnitToReviewDaoAdapter adapter;

    @Autowired
    private JdbcClient jdbc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CampaignJpaRepository campaignRepository;

    @Autowired
    private OrganizationUnitJpaRepository ouRepository;

    @Autowired
    private InterviewerJpaRepository interviewerRepository;

    @Autowired
    private SurveyUnitJpaRepository surveyUnitRepository;

    @Autowired
    private StateJpaRepository stateRepository;

    @Autowired
    private ContactOutcomeJpaRepository contactOutcomeRepository;

    @Autowired
    private CommentDaoAdapter commentDaoAdapter;

    static final String CAMPAIGN_ID = "CAMP-TEST";
    static final String OU1_ID = "OU-TEST-1";
    static final String OU2_ID = "OU-TEST-2";
    static final String INTW1_ID = "INTW-TEST-1";
    static final String INTW2_ID = "INTW-TEST-2";

    @BeforeEach
    void setup() {
        // 1. Create campaign
        CampaignDB campaign = new CampaignDB(CAMPAIGN_ID, "Test Campaign",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "test@test.com", false, false);
        campaignRepository.save(campaign);

        // 2. Create organization units
        OrganizationUnitDB ou1 = new OrganizationUnitDB(OU1_ID, "Org Unit 1", OrganizationUnitType.LOCAL);
        OrganizationUnitDB ou2 = new OrganizationUnitDB(OU2_ID, "Org Unit 2", OrganizationUnitType.LOCAL);
        ouRepository.save(ou1);
        ouRepository.save(ou2);

        // 3. Create interviewers
        InterviewerDB intw1 = new InterviewerDB();
        intw1.setId(INTW1_ID);
        intw1.setFirstName("Jean");
        intw1.setLastName("Dupont");

        InterviewerDB intw2 = new InterviewerDB();
        intw2.setId(INTW2_ID);
        intw2.setFirstName("Marie");
        intw2.setLastName("Martin");

        interviewerRepository.save(intw1);
        interviewerRepository.save(intw2);

        // 4. Create survey units with TBR state
        createSurveyUnitWithState("SU-1", CAMPAIGN_ID, OU1_ID, INTW1_ID, StateType.TBR);
        createSurveyUnitWithState("SU-2", CAMPAIGN_ID, OU1_ID, INTW2_ID, StateType.TBR);
        createSurveyUnitWithState("SU-3", CAMPAIGN_ID, OU2_ID, INTW1_ID, StateType.TBR);
        createSurveyUnitWithState("SU-4", CAMPAIGN_ID, OU2_ID, INTW2_ID, StateType.TBR);
        createSurveyUnitWithState("SU-5", CAMPAIGN_ID, OU1_ID, INTW1_ID, StateType.NVM); // Not TBR, should not appear

        // 5. Create contact outcomes
        createContactOutcome("SU-1", ContactOutcomeType.INA);
        createContactOutcome("SU-2", ContactOutcomeType.IMP);
        createContactOutcome("SU-3", ContactOutcomeType.REF);
        createContactOutcome("SU-4", ContactOutcomeType.NOA);

        // 6. Create management comments
        createManagementComment("SU-1", "First comment for SU-1");
        createManagementComment("SU-2", "Comment for SU-2");
        createManagementComment("SU-1", "Last comment for SU-1"); // Should be the last one

        entityManager.flush();
    }

    private void createSurveyUnitWithState(String suId, String campaignId, String ouId, String interviewerId, StateType state) {
        SurveyUnitDB su = new SurveyUnitDB();
        su.setId(suId);
        su.setCampaign(campaignRepository.findById(campaignId).orElseThrow());
        su.setOrganizationUnit(ouRepository.findById(ouId).orElseThrow());
        su.setInterviewer(interviewerId != null ? interviewerRepository.findById(interviewerId).orElseThrow(() -> new IllegalArgumentException("Interviewer not found: " + interviewerId)) : null);
        su.setViewed(suId.equals("SU-1")); // SU-1 is marked as viewed
        surveyUnitRepository.save(su);

        StateDB st = new StateDB();
        st.setSurveyUnit(su);
        st.setType(state); // StateType.TBR ou "NVM"
        st.setDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        stateRepository.save(st);
    }

    private void createContactOutcome(String suId, ContactOutcomeType outcomeType) {
        ContactOutcomeDB contactOutcome = new ContactOutcomeDB();
        contactOutcome.setSurveyUnit(surveyUnitRepository.findById(suId).orElseThrow());
        contactOutcome.setType(outcomeType);
        contactOutcome.setDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        contactOutcomeRepository.save(contactOutcome);
    }

    private void createManagementComment(String suId, String comment) {
        Comment managementComment = new Comment(CommentType.MANAGEMENT, comment, suId);
        commentDaoAdapter.updateComment(managementComment);
    }

    @Test
    @DisplayName("Should return paginated results with correct total count")
    void findSurveyUnitsToReview_shouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(4); // SU-1, SU-2, SU-3, SU-4 (SU-5 is not TBR)
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        // Check first element
        SurveyUnitToReview first = result.getContent().getFirst();
        assertThat(first.id()).isEqualTo("SU-1");
        assertThat(first.campaignLabel()).isEqualTo("Test Campaign");
        assertThat(first.interviewerName()).isEqualTo("Jean Dupont");
        assertThat(first.viewed()).isTrue();
        assertThat(first.lastComment()).isEqualTo("Last comment for SU-1");
    }

    @Test
    @DisplayName("Should filter by campaign IDs")
    void findSurveyUnitsToReview_shouldFilterByCampaignIds() {
        // Create second campaign with survey units
        CampaignDB campaign2 = new CampaignDB("CAMP-TEST-2", "Test Campaign 2",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "t@t.com", false, false);
        campaignRepository.save(campaign2);
        createSurveyUnitWithState("SU-C2-1", "CAMP-TEST-2", OU1_ID, INTW1_ID, StateType.TBR);
        entityManager.flush();

        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(4); // Only those from CAMPAIGN_ID
        assertThat(result.getContent()).extracting(SurveyUnitToReview::id)
                .containsExactly("SU-1", "SU-2", "SU-3", "SU-4");
    }

    @Test
    @DisplayName("Should filter by organization unit IDs")
    void findSurveyUnitsToReview_shouldFilterByOrganizationUnitIds() {
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID), null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2); // SU-1 and SU-2 only
        assertThat(result.getContent()).extracting(SurveyUnitToReview::id)
                .containsExactly("SU-1", "SU-2");
    }

    @Test
    @DisplayName("Should apply search condition on campaign label")
    void findSurveyUnitsToReview_shouldApplySearchCondition_onCampaignLabel() {
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), "Test Camp", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should apply search condition on survey unit ID")
    void findSurveyUnitsToReview_shouldApplySearchCondition_onSurveyUnitId() {
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), "SU-1", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).id()).isEqualTo("SU-1");
    }

    @Test
    @DisplayName("Should apply search condition on interviewer name")
    void findSurveyUnitsToReview_shouldApplySearchCondition_onInterviewerName() {
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), "Dupont", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2); // SU-1 and SU-3
    }

    @Test
    @DisplayName("Should sort by survey unit ID by default")
    void findSurveyUnitsToReview_shouldSortBySurveyUnitIdByDefault() {
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), null, PageRequest.of(0, 10));

        assertThat(result.getContent()).extracting(SurveyUnitToReview::id)
                .isSortedAccordingTo(String::compareTo);
    }

    @Test
    @DisplayName("Should sort by campaign label when requested")
    void findSurveyUnitsToReview_shouldSortByCampaignLabel() {
        // Create second campaign with different name
        CampaignDB campaign2 = new CampaignDB("CAMP-TEST-2", "Zebra Campaign",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "t@t.com", false, false);
        campaignRepository.save(campaign2);
        createSurveyUnitWithState("SU-C2-1", "CAMP-TEST-2", OU1_ID, INTW1_ID, StateType.TBR);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("campaignLabel").ascending());
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID, "CAMP-TEST-2"), List.of(OU1_ID), null, pageable);

        assertThat(result.getContent()).extracting(SurveyUnitToReview::campaignLabel)
                .isSortedAccordingTo(String::compareTo);
    }

    @Test
    @DisplayName("Should return empty results when no TBR survey units")
    void findSurveyUnitsToReview_shouldReturnEmpty_whenNoTBRSurveyUnits() {
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of("NON-EXISTANT"), List.of(OU1_ID), null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null search parameter")
    void findSurveyUnitsToReview_shouldHandleNullSearch() {
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID), null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle empty search parameter")
    void findSurveyUnitsToReview_shouldHandleEmptySearch() {
        Page<SurveyUnitToReview> result = adapter.findSurveyUnitsToReview(
                List.of(CAMPAIGN_ID), List.of(OU1_ID), "", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }
}