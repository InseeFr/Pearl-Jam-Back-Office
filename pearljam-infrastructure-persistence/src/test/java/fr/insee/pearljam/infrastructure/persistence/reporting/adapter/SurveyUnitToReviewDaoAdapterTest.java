package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitToReview;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CampaignJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.contactoutcome.jpa.ContactOutcomeJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.OrganizationUnitJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter.CommentDaoAdapter;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.StateDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.InterviewerJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.StateJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
class SurveyUnitToReviewDaoAdapterTest {

    @Autowired
    private SurveyUnitToReviewDaoAdapter adapter;

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

        CampaignDB campaign = new CampaignDB(
                CAMPAIGN_ID,
                "Test Campaign",
                IdentificationConfiguration.HOUSEF2F,
                ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                "test@test.com",
                false,
                false
        );
        campaignRepository.save(campaign);

        OrganizationUnitDB ou1 = new OrganizationUnitDB(OU1_ID, "Org Unit 1", OrganizationUnitType.LOCAL);
        OrganizationUnitDB ou2 = new OrganizationUnitDB(OU2_ID, "Org Unit 2", OrganizationUnitType.LOCAL);
        ouRepository.save(ou1);
        ouRepository.save(ou2);

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

        create("SU-1", OU1_ID, INTW1_ID, StateType.TBR, true);
        create("SU-2", OU1_ID, INTW2_ID, StateType.TBR, false);
        create("SU-3", OU2_ID, INTW1_ID, StateType.TBR, false);
        create("SU-4", OU2_ID, INTW2_ID, StateType.TBR, true);
        create("SU-5", OU1_ID, INTW1_ID, StateType.NVM, false);

        createManagementComment("SU-1", "Comment for SU-1");
        createManagementComment("SU-2", "Comment for SU-2");

        entityManager.flush();
    }

    private void create(String id, String ouId, String interviewerId, StateType state, boolean viewed) {
        SurveyUnitDB su = new SurveyUnitDB();
        su.setId(id);
        su.setCampaign(campaignRepository.findById(CAMPAIGN_ID).orElseThrow());
        su.setOrganizationUnit(ouRepository.findById(ouId).orElseThrow());
        su.setInterviewer(interviewerRepository.findById(interviewerId).orElseThrow());
        su.setViewed(viewed);

        surveyUnitRepository.save(su);

        StateDB st = new StateDB();
        st.setSurveyUnit(su);
        st.setType(state);
        st.setDate(1700000000L);

        stateRepository.save(st);
    }

    private void createManagementComment(String suId, String comment) {
        Comment managementComment = new Comment(CommentType.MANAGEMENT, comment, suId);
        commentDaoAdapter.updateComment(managementComment);
    }

    // =========================================================
    // CORE TESTS
    // =========================================================

    @Test
    void shouldReturnPaginatedResults() {

        Page<SurveyUnitToReview> result =
                adapter.findSurveyUnitsToReview(
                        List.of(CAMPAIGN_ID),
                        List.of(OU1_ID, OU2_ID),
                        null,
                        null,
                        PageRequest.of(0, 2)
                );

        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(2);

        SurveyUnitToReview first = result.getContent().getFirst();

        assertThat(first.id()).isEqualTo("SU-1");
        assertThat(first.campaignLabel()).isEqualTo("Test Campaign");
        assertThat(first.interviewerFirstName()).isEqualTo("Jean");
        assertThat(first.interviewerLastName()).isEqualTo("Dupont");
        assertThat(first.lastComment()).isEqualTo("Comment for SU-1");
    }

    @Test
    void shouldFilterByCampaign() {

        Page<SurveyUnitToReview> result =
                adapter.findSurveyUnitsToReview(
                        List.of(CAMPAIGN_ID),
                        List.of(OU1_ID, OU2_ID),
                        null,
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent())
                .extracting(SurveyUnitToReview::id)
                .containsExactlyInAnyOrder("SU-1", "SU-2", "SU-3", "SU-4");
    }

    @Test
    void shouldFilterByOrganizationUnit() {

        Page<SurveyUnitToReview> result =
                adapter.findSurveyUnitsToReview(
                        List.of(CAMPAIGN_ID),
                        List.of(OU1_ID),
                        null,
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldSearchBySurveyUnitId() {

        Page<SurveyUnitToReview> result =
                adapter.findSurveyUnitsToReview(
                        List.of(CAMPAIGN_ID),
                        List.of(OU1_ID, OU2_ID),
                        "SU-1",
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldSearchByInterviewerName() {

        Page<SurveyUnitToReview> result =
                adapter.findSurveyUnitsToReview(
                        List.of(CAMPAIGN_ID),
                        List.of(OU1_ID, OU2_ID),
                        "Dupont",
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {

        Page<SurveyUnitToReview> result =
                adapter.findSurveyUnitsToReview(
                        List.of("UNKNOWN"),
                        List.of(OU1_ID),
                        null,
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldFilterByViewed(boolean viewed) {

        Page<SurveyUnitToReview> result =
                adapter.findSurveyUnitsToReview(
                        List.of(CAMPAIGN_ID),
                        List.of(OU1_ID, OU2_ID),
                        null,
                        viewed,
                        PageRequest.of(0, 10)
                );
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent())
                .allMatch(su -> su.viewed() == viewed);
    }

    @Test
    void shouldIgnoreViewedWhenNull() {

        Page<SurveyUnitToReview> result =
                adapter.findSurveyUnitsToReview(
                        List.of(CAMPAIGN_ID),
                        List.of(OU1_ID, OU2_ID),
                        null,
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isGreaterThan(0);
    }
}