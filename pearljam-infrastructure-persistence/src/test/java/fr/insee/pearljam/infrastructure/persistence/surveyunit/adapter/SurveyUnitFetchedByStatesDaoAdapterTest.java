package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.VisibilityDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.VisibilityDBId;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CampaignJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.VisibilityJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.contactoutcome.jpa.ContactOutcomeJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.OrganizationUnitJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.reporting.adapter.SurveyUnitFetchedByStatesDaoAdapter;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.ContactOutcomeDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.StateDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.InterviewerJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.StateJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
class SurveyUnitFetchedByStatesDaoAdapterTest {

    @Autowired private SurveyUnitFetchedByStatesDaoAdapter adapter;
    @Autowired private EntityManager entityManager;
    @Autowired private CampaignJpaRepository campaignRepository;
    @Autowired private OrganizationUnitJpaRepository ouRepository;
    @Autowired private InterviewerJpaRepository interviewerRepository;
    @Autowired private SurveyUnitJpaRepository surveyUnitRepository;
    @Autowired private StateJpaRepository stateRepository;
    @Autowired private ContactOutcomeJpaRepository contactOutcomeRepository;
    @Autowired private VisibilityJpaRepository visibilityJpaRepository;
    @Autowired private CommentDaoAdapter commentDaoAdapter;

    static final String CAMPAIGN_ID = "CAMP-FETCH-TEST";
    static final String OU_ID       = "OU-FETCH-1";
    static final String OU_OTHER_ID = "OU-FETCH-2";
    static final String INTW1_ID    = "INTW-FETCH-1";
    static final String INTW2_ID    = "INTW-FETCH-2";

    @BeforeEach
    void setup() {
        campaignRepository.save(new CampaignDB(CAMPAIGN_ID, "Fetch Campaign",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "test@test.com", false, false));

        ouRepository.save(new OrganizationUnitDB(OU_ID,       "Org Unit",       OrganizationUnitType.LOCAL));
        ouRepository.save(new OrganizationUnitDB(OU_OTHER_ID, "Other Org Unit", OrganizationUnitType.LOCAL));

        saveVisibility(OU_ID);
        saveVisibility(OU_OTHER_ID);

        saveInterviewer(INTW1_ID, "Dupont");
        saveInterviewer(INTW2_ID, "Martin");

        // SU-F1: TBR, intw1, with comment and contact outcome, in OU_ID
        createSurveyUnit("SU-F1", INTW1_ID, OU_ID, StateType.TBR);
        commentDaoAdapter.updateComment(new Comment(CommentType.MANAGEMENT, "Comment SU-F1", "SU-F1"));
        saveContactOutcome();

        // SU-F2: TBR, intw2, no extras, in OU_ID
        createSurveyUnit("SU-F2", INTW2_ID, OU_ID, StateType.TBR);

        // SU-F3: NVM, should be excluded from TBR-only queries, in OU_ID
        createSurveyUnit("SU-F3", INTW1_ID, OU_ID, StateType.NVM);

        // SU-F4: TBR, in OU_OTHER_ID — should be excluded when filtering by OU_ID only
        createSurveyUnit("SU-F4", INTW1_ID, OU_OTHER_ID, StateType.TBR);

        entityManager.flush();
    }

    private void saveInterviewer(String id, String lastName) {
        InterviewerDB i = new InterviewerDB();
        i.setId(id); i.setFirstName("Jean"); i.setLastName(lastName);
        interviewerRepository.save(i);
    }

    private void saveVisibility(String ouId) {
        VisibilityDB v = new VisibilityDB();
        v.setVisibilityId(new VisibilityDBId(ouId, CAMPAIGN_ID));
        visibilityJpaRepository.save(v);
    }

    private void createSurveyUnit(String id, String interviewerId, String ouId, StateType state) {
        SurveyUnitDB su = new SurveyUnitDB();
        su.setId(id);
        su.setCampaign(campaignRepository.findById(CAMPAIGN_ID).orElseThrow());
        su.setOrganizationUnit(ouRepository.findById(ouId).orElseThrow());
        su.setInterviewer(interviewerRepository.findById(interviewerId).orElseThrow());
        surveyUnitRepository.save(su);

        StateDB st = new StateDB();
        st.setSurveyUnit(su); st.setType(state); st.setDate(1700000000L);
        stateRepository.save(st);
    }

    private void saveContactOutcome() {
        ContactOutcomeDB co = new ContactOutcomeDB();
        co.setSurveyUnit(surveyUnitRepository.findById("SU-F1").orElseThrow());
        co.setType(ContactOutcomeType.INA);
        contactOutcomeRepository.save(co);
    }

    private Page<SurveyUnitFetchedByStatesAndCampaignIdView> fetch(
            List<StateType> states, String search, PageRequest page) {
        return adapter.getSurveyUnitsByStatesAndCampaignId(
                states, CAMPAIGN_ID, search, List.of(OU_ID), page);
    }

    // =========================================================

    @Test
    void shouldReturnPaginatedResults() {
        var result = fetch(List.of(StateType.TBR), null, PageRequest.of(0, 1));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldFilterBySingleState() {
        var result = fetch(List.of(StateType.TBR), null, PageRequest.of(0, 10));
        assertThat(result.getContent())
                .extracting(SurveyUnitFetchedByStatesAndCampaignIdView::surveyUnitId)
                .containsExactlyInAnyOrder("SU-F1", "SU-F2");
    }

    @Test
    void shouldFilterByMultipleStates() {
        var result = fetch(List.of(StateType.TBR, StateType.NVM), null, PageRequest.of(0, 10));
        assertThat(result.getContent())
                .extracting(SurveyUnitFetchedByStatesAndCampaignIdView::surveyUnitId)
                .containsExactlyInAnyOrder("SU-F1", "SU-F2", "SU-F3");
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {
        var result = adapter.getSurveyUnitsByStatesAndCampaignId(
                List.of(StateType.TBR), "UNKNOWN-CAMPAIGN", null, List.of(OU_ID), PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void shouldMapFieldsCorrectly() {
        var view = fetch(List.of(StateType.TBR), "SU-F1", PageRequest.of(0, 10))
                .getContent().getFirst();

        assertThat(view.surveyUnitId()).isEqualTo("SU-F1");
        assertThat(view.interviewerFirstName()).isEqualTo("Jean");
        assertThat(view.interviewerLastName()).isEqualTo("Dupont");
        assertThat(view.comment()).isEqualTo("Comment SU-F1");
        assertThat(view.contactOutcome()).isEqualTo("INA");
    }

    @Test
    void shouldReturnNullsForMissingOptionalFields() {
        var view = fetch(List.of(StateType.TBR), "SU-F2", PageRequest.of(0, 10))
                .getContent().getFirst();

        assertThat(view.comment()).isNull();
        assertThat(view.contactOutcome()).isNull();
    }

    @Test
    void shouldSearchByInterviewerName() {
        assertThat(fetch(List.of(StateType.TBR), "jean",  PageRequest.of(0, 10)).getTotalElements()).isEqualTo(2);
        assertThat(fetch(List.of(StateType.TBR), "martin",PageRequest.of(0, 10)).getTotalElements()).isEqualTo(1);
        assertThat(fetch(List.of(StateType.TBR), "Marie", PageRequest.of(0, 10)).getTotalElements()).isZero();
    }

    @Test
    void shouldSearchBySurveyUnitId() {
        assertThat(fetch(List.of(StateType.TBR), "SU-F1", PageRequest.of(0, 10)).getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldReturnAllWhenSearchNullOrBlank() {
        assertThat(fetch(List.of(StateType.TBR), null,   PageRequest.of(0, 10)).getTotalElements()).isEqualTo(2);
        assertThat(fetch(List.of(StateType.TBR), "  ",   PageRequest.of(0, 10)).getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyWhenSearchMatchesNothing() {
        assertThat(fetch(List.of(StateType.TBR), "ZZZNOMATCH", PageRequest.of(0, 10)).getContent()).isEmpty();
    }

    @Test
    void shouldSortBySurveyUnitIdAscending() {
        var ids = fetch(List.of(StateType.TBR), null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "surveyUnitId")))
                .getContent().stream()
                .map(SurveyUnitFetchedByStatesAndCampaignIdView::surveyUnitId)
                .toList();
        assertThat(ids).isSortedAccordingTo(String::compareTo);
    }

    @Test
    void shouldFilterByOrganizationUnit() {
        // only OU_OTHER_ID -> returns SU-F4 only
        var result = adapter.getSurveyUnitsByStatesAndCampaignId(
                List.of(StateType.TBR), CAMPAIGN_ID, null, List.of(OU_OTHER_ID),  PageRequest.of(0, 10));
        assertThat(result.getContent())
                .extracting(SurveyUnitFetchedByStatesAndCampaignIdView::surveyUnitId)
                .containsExactly("SU-F4");

        // both OUs -> returns SU-F1, SU-F2, SU-F4
        var resultBoth = adapter.getSurveyUnitsByStatesAndCampaignId(
                List.of(StateType.TBR), CAMPAIGN_ID, null, List.of(OU_ID, OU_OTHER_ID),  PageRequest.of(0, 10));
        assertThat(resultBoth.getContent())
                .extracting(SurveyUnitFetchedByStatesAndCampaignIdView::surveyUnitId)
                .containsExactlyInAnyOrder("SU-F1", "SU-F2", "SU-F4");

        // null ouIds -> empty page (guard)
        var resultNull = adapter.getSurveyUnitsByStatesAndCampaignId(
                List.of(StateType.TBR), CAMPAIGN_ID, null, null, PageRequest.of(0, 10));
        assertThat(resultNull.getContent()).isEmpty();
    }
}