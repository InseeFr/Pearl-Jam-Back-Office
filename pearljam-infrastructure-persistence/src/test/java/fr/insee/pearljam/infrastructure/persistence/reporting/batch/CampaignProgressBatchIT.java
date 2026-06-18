package fr.insee.pearljam.infrastructure.persistence.reporting.batch;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestEmitter;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CommunicationTemplateDBId;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CampaignJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.OrganizationUnitJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.reporting.adapter.CampaignDailyStatsDaoAdapter;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.*;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.InterviewerJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
class CampaignProgressBatchIT {

    @Autowired
    private CampaignProgressBatch batch;

    @Autowired
    private CampaignDailyStatsDaoAdapter statsAdapter;

    @Autowired
    private CampaignJpaRepository campaignRepository;

    @Autowired
    private OrganizationUnitJpaRepository ouRepository;

    @Autowired
    private InterviewerJpaRepository interviewerRepository;

    @Autowired
    private SurveyUnitJpaRepository surveyUnitRepository;

    @Autowired
    private EntityManager entityManager;

    static final LocalDate DAY = LocalDate.of(2020, 4, 15);
    static final long BEFORE_DAY = DAY.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1000;
    static final long AFTER_DAY = DAY.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() + 1000;

    private CampaignDB campaign;
    private OrganizationUnitDB ou1;
    private InterviewerDB interviewer1;
    private InterviewerDB interviewer2;

    @BeforeEach
    void setup() {
        campaign = new CampaignDB("CAMP-BATCH", "Batch Campaign",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "batch@test.com", false, false);
        campaignRepository.save(campaign);

        ou1 = new OrganizationUnitDB("OU-BATCH-1", "Batch OU 1", OrganizationUnitType.LOCAL);
        ouRepository.save(ou1);

        interviewer1 = createInterviewer("INTW-B1", "Jean", "Dupont");
        interviewer2 = createInterviewer("INTW-B2", "Marie", "Martin");
        interviewerRepository.save(interviewer1);
        interviewerRepository.save(interviewer2);
    }

    @Test
    @DisplayName("Should compute state counts from latest state before the day")
    void shouldComputeStateCounts() {
        // SU1: latest state = VIC (before day)
        SurveyUnitDB su1 = createSurveyUnit("SU-B1", campaign, ou1, interviewer1);
        su1.getStates().add(new StateDB(BEFORE_DAY - 5000, su1, StateType.NVM));
        su1.getStates().add(new StateDB(BEFORE_DAY, su1, StateType.VIC));
        // state after the day — should be ignored
        su1.getStates().add(new StateDB(AFTER_DAY, su1, StateType.FIN));

        // SU2: latest state = TBR
        SurveyUnitDB su2 = createSurveyUnit("SU-B2", campaign, ou1, interviewer1);
        su2.getStates().add(new StateDB(BEFORE_DAY, su2, StateType.TBR));

        surveyUnitRepository.save(su1);
        surveyUnitRepository.save(su2);
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        CampaignDailyStats stats = result.get();
        assertThat(stats.getVicStateCount()).isEqualTo(1);
        assertThat(stats.getTbrStateCount()).isEqualTo(1);
        assertThat(stats.getFinStateCount()).isZero(); // future state ignored
    }

    @Test
    @DisplayName("Should compute closing cause counts only for CLO state survey units")
    void shouldComputeClosingCauseCounts() {
        // SU in CLO state with NPA closing cause
        SurveyUnitDB su1 = createSurveyUnit("SU-CC1", campaign, ou1, interviewer1);
        su1.getStates().add(new StateDB(BEFORE_DAY, su1, StateType.CLO));
        su1.setClosingCause(new ClosingCauseDB(null, BEFORE_DAY, ClosingCauseType.NPA, su1));

        // SU in CLO state with ROW closing cause
        SurveyUnitDB su2 = createSurveyUnit("SU-CC2", campaign, ou1, interviewer1);
        su2.getStates().add(new StateDB(BEFORE_DAY, su2, StateType.CLO));
        su2.setClosingCause(new ClosingCauseDB(null, BEFORE_DAY, ClosingCauseType.ROW, su2));

        // SU in FIN state with closing cause — should NOT be counted (not CLO)
        SurveyUnitDB su3 = createSurveyUnit("SU-CC3", campaign, ou1, interviewer1);
        su3.getStates().add(new StateDB(BEFORE_DAY, su3, StateType.FIN));
        su3.setClosingCause(new ClosingCauseDB(null, BEFORE_DAY, ClosingCauseType.NPI, su3));

        // SU in NVA state with closing cause (provisional)
        SurveyUnitDB su4 = createSurveyUnit("SU-CC4", campaign, ou1, interviewer1);
        su4.getStates().add(new StateDB(BEFORE_DAY, su4, StateType.NVA));
        su4.setClosingCause(new ClosingCauseDB(null, BEFORE_DAY, ClosingCauseType.NPA, su4));

        surveyUnitRepository.saveAll(List.of(su1, su2, su3, su4));
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        CampaignDailyStats stats = result.get();
        assertThat(stats.getNpaClosingCauseCount()).isEqualTo(1);
        assertThat(stats.getRowClosingCauseCount()).isEqualTo(1);
        assertThat(stats.getNpiClosingCauseCount()).isZero(); // FIN state, not CLO
        assertThat(stats.getNpaProvisionalClosingCauseCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should compute contact outcome counts only for TBR/FIN/CLO state survey units")
    void shouldComputeContactOutcomeCounts() {
        // SU in FIN state with INA outcome
        SurveyUnitDB su1 = createSurveyUnit("SU-CO1", campaign, ou1, interviewer1);
        su1.getStates().add(new StateDB(BEFORE_DAY, su1, StateType.FIN));
        su1.setContactOutcome(new ContactOutcomeDB(null, BEFORE_DAY, ContactOutcomeType.INA, 3, su1));

        // SU in TBR state with REF outcome
        SurveyUnitDB su2 = createSurveyUnit("SU-CO2", campaign, ou1, interviewer1);
        su2.getStates().add(new StateDB(BEFORE_DAY, su2, StateType.TBR));
        su2.setContactOutcome(new ContactOutcomeDB(null, BEFORE_DAY, ContactOutcomeType.REF, 2, su2));

        // SU in VIC state with INA outcome — should NOT be counted (not finalized)
        SurveyUnitDB su3 = createSurveyUnit("SU-CO3", campaign, ou1, interviewer1);
        su3.getStates().add(new StateDB(BEFORE_DAY, su3, StateType.VIC));
        su3.setContactOutcome(new ContactOutcomeDB(null, BEFORE_DAY, ContactOutcomeType.INA, 1, su3));

        surveyUnitRepository.saveAll(List.of(su1, su2, su3));
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        CampaignDailyStats stats = result.get();
        assertThat(stats.getInaContactOutcomeCount()).isEqualTo(1); // only FIN/TBR/CLO
        assertThat(stats.getRefContactOutcomeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should compute communication counts from READY status requests")
    void shouldComputeCommunicationCounts() {
        // setup communication template
        CommunicationTemplateDBId templateId = new CommunicationTemplateDBId("meshuggah-1", campaign.getId());
        CommunicationTemplateDB template = new CommunicationTemplateDB(
                templateId, CommunicationMedium.LETTER, CommunicationType.NOTICE, campaign);
        campaign.setCommunicationTemplates(List.of(template));
        campaignRepository.save(campaign);

        CommunicationTemplateDBId templateId2 = new CommunicationTemplateDBId("meshuggah-2", campaign.getId());
        CommunicationTemplateDB template2 = new CommunicationTemplateDB(
                templateId2, CommunicationMedium.LETTER, CommunicationType.REMINDER, campaign);
        campaign.setCommunicationTemplates(List.of(template, template2));
        campaignRepository.save(campaign);

        // SU with NOTICE communication request with READY status
        SurveyUnitDB su1 = createSurveyUnit("SU-CM1", campaign, ou1, interviewer1);
        su1.getStates().add(new StateDB(BEFORE_DAY, su1, StateType.PRC));
        CommunicationRequestDB req1 = new CommunicationRequestDB(
                null, templateId, CommunicationRequestReason.UNREACHABLE,
                CommunicationRequestEmitter.TOOL, su1, new ArrayList<>());
        CommunicationRequestStatusDB status1 = new CommunicationRequestStatusDB(
                null, BEFORE_DAY, CommunicationStatusType.READY, req1);
        req1.getStatus().add(status1);
        su1.getCommunicationRequests().add(req1);

        // SU with REMINDER communication request with READY status
        SurveyUnitDB su2 = createSurveyUnit("SU-CM2", campaign, ou1, interviewer1);
        su2.getStates().add(new StateDB(BEFORE_DAY, su2, StateType.INS));
        CommunicationRequestDB req2 = new CommunicationRequestDB(
                null, templateId2, CommunicationRequestReason.REFUSAL,
                CommunicationRequestEmitter.INTERVIEWER, su2, new ArrayList<>());
        CommunicationRequestStatusDB status2 = new CommunicationRequestStatusDB(
                null, BEFORE_DAY, CommunicationStatusType.READY, req2);
        req2.getStatus().add(status2);
        su2.getCommunicationRequests().add(req2);

        // SU with communication request but only INITIATED status — should NOT count
        SurveyUnitDB su3 = createSurveyUnit("SU-CM3", campaign, ou1, interviewer1);
        su3.getStates().add(new StateDB(BEFORE_DAY, su3, StateType.AOC));
        CommunicationRequestDB req3 = new CommunicationRequestDB(
                null, templateId, CommunicationRequestReason.UNREACHABLE,
                CommunicationRequestEmitter.TOOL, su3, new ArrayList<>());
        CommunicationRequestStatusDB status3 = new CommunicationRequestStatusDB(
                null, BEFORE_DAY, CommunicationStatusType.INITIATED, req3);
        req3.getStatus().add(status3);
        su3.getCommunicationRequests().add(req3);

        surveyUnitRepository.saveAll(List.of(su1, su2, su3));
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        CampaignDailyStats stats = result.get();
        assertThat(stats.getNoticeCommunicationCount()).isEqualTo(1);
        assertThat(stats.getReminderCommunicationCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should group stats by interviewer")
    void shouldGroupByInterviewer() {
        SurveyUnitDB su1 = createSurveyUnit("SU-GI1", campaign, ou1, interviewer1);
        su1.getStates().add(new StateDB(BEFORE_DAY, su1, StateType.FIN));

        SurveyUnitDB su2 = createSurveyUnit("SU-GI2", campaign, ou1, interviewer1);
        su2.getStates().add(new StateDB(BEFORE_DAY, su2, StateType.FIN));

        SurveyUnitDB su3 = createSurveyUnit("SU-GI3", campaign, ou1, interviewer2);
        su3.getStates().add(new StateDB(BEFORE_DAY, su3, StateType.VIC));

        surveyUnitRepository.saveAll(List.of(su1, su2, su3));
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        var interviewerStats = statsAdapter.getInterviewerStats("CAMP-BATCH", List.of("OU-BATCH-1"), DAY);
        assertThat(interviewerStats).hasSize(2);

        var intw1Stats = interviewerStats.stream()
                .filter(s -> s.getInterviewerId().equals("INTW-B1")).findFirst().orElseThrow();
        assertThat(intw1Stats.getFinStateCount()).isEqualTo(2);

        var intw2Stats = interviewerStats.stream()
                .filter(s -> s.getInterviewerId().equals("INTW-B2")).findFirst().orElseThrow();
        assertThat(intw2Stats.getVicStateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should be idempotent — running twice produces same results")
    void shouldBeIdempotent() {
        SurveyUnitDB su = createSurveyUnit("SU-IDEM", campaign, ou1, interviewer1);
        su.getStates().add(new StateDB(BEFORE_DAY, su, StateType.TBR));
        surveyUnitRepository.save(su);
        entityManager.flush();

        batch.run(DAY);
        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        assertThat(result.get().getTbrStateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should exclude survey units without interviewer")
    void shouldExcludeSurveyUnitsWithoutInterviewer() {
        SurveyUnitDB suAssigned = createSurveyUnit("SU-ASSGN", campaign, ou1, interviewer1);
        suAssigned.getStates().add(new StateDB(BEFORE_DAY, suAssigned, StateType.FIN));

        SurveyUnitDB suUnassigned = createSurveyUnit("SU-UNASSGN", campaign, ou1, null);
        suUnassigned.getStates().add(new StateDB(BEFORE_DAY, suUnassigned, StateType.VIC));

        surveyUnitRepository.saveAll(List.of(suAssigned, suUnassigned));
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        assertThat(result.get().getFinStateCount()).isEqualTo(1);
        assertThat(result.get().getVicStateCount()).isZero(); // unassigned excluded
    }

    @Test
    @DisplayName("Should delete orphan rows when interviewer no longer has survey units")
    void shouldDeleteOrphanRows() {
        // First run: interviewer1 has a SU
        SurveyUnitDB su = createSurveyUnit("SU-ORPH", campaign, ou1, interviewer1);
        su.getStates().add(new StateDB(BEFORE_DAY, su, StateType.FIN));
        surveyUnitRepository.save(su);
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        var statsBefore = statsAdapter.getInterviewerStats("CAMP-BATCH", List.of("OU-BATCH-1"), DAY);
        assertThat(statsBefore).hasSize(1);
        assertThat(statsBefore.getFirst().getInterviewerId()).isEqualTo("INTW-B1");

        // Reassign SU to interviewer2 — interviewer1 becomes orphan
        su = surveyUnitRepository.findById("SU-ORPH").orElseThrow();
        su.setInterviewer(interviewer2);
        surveyUnitRepository.save(su);
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        var statsAfter = statsAdapter.getInterviewerStats("CAMP-BATCH", List.of("OU-BATCH-1"), DAY);
        assertThat(statsAfter).hasSize(1);
        assertThat(statsAfter.getFirst().getInterviewerId()).isEqualTo("INTW-B2");
    }

    @Test
    @DisplayName("Should group stats by organization unit")
    void shouldGroupByOrganizationUnit() {
        OrganizationUnitDB ou2 = new OrganizationUnitDB("OU-BATCH-2", "Batch OU 2", OrganizationUnitType.LOCAL);
        ouRepository.save(ou2);

        SurveyUnitDB su1 = createSurveyUnit("SU-OU1", campaign, ou1, interviewer1);
        su1.getStates().add(new StateDB(BEFORE_DAY, su1, StateType.FIN));

        SurveyUnitDB su2 = createSurveyUnit("SU-OU2", campaign, ou1, interviewer1);
        su2.getStates().add(new StateDB(BEFORE_DAY, su2, StateType.FIN));

        SurveyUnitDB su3 = createSurveyUnit("SU-OU3", campaign, ou2, interviewer1);
        su3.getStates().add(new StateDB(BEFORE_DAY, su3, StateType.VIC));

        surveyUnitRepository.saveAll(List.of(su1, su2, su3));
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();


        var stats = statsAdapter.getOrganizationUnitsStats("CAMP-BATCH", DAY);

        assertThat(stats).hasSize(2);

        var ou1Stats = stats.stream()
                .filter(s -> s.getOuId().equals("OU-BATCH-1"))
                .findFirst()
                .orElseThrow();

        assertThat(ou1Stats.getFinStateCount()).isEqualTo(2);
        assertThat(ou1Stats.getVicStateCount()).isZero();

        var ou2Stats = stats.stream()
                .filter(s -> s.getOuId().equals("OU-BATCH-2"))
                .findFirst()
                .orElseThrow();

        assertThat(ou2Stats.getVicStateCount()).isEqualTo(1);
        assertThat(ou2Stats.getFinStateCount()).isZero();

        var allStats = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);

        assertThat(allStats).isPresent();
        assertThat(allStats.get().getFinStateCount()).isEqualTo(2);
        assertThat(allStats.get().getVicStateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update stats when data changes between runs")
    void shouldUpdateStatsOnRerun() {
        SurveyUnitDB su = createSurveyUnit("SU-UPD", campaign, ou1, interviewer1);
        su.getStates().add(new StateDB(BEFORE_DAY, su, StateType.VIC));
        surveyUnitRepository.save(su);
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> firstRun = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(firstRun).isPresent();
        assertThat(firstRun.get().getVicStateCount()).isEqualTo(1);
        assertThat(firstRun.get().getFinStateCount()).isZero();

        // Add a newer state (still before day) — SU moves from VIC to FIN
        su = surveyUnitRepository.findById("SU-UPD").orElseThrow();
        su.getStates().add(new StateDB(BEFORE_DAY + 500, su, StateType.FIN));
        surveyUnitRepository.save(su);
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> secondRun = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(secondRun).isPresent();
        assertThat(secondRun.get().getVicStateCount()).isZero();
        assertThat(secondRun.get().getFinStateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should separate stats between campaigns")
    void shouldSeparateStatsBetweenCampaigns() {
        CampaignDB campaign2 = new CampaignDB("CAMP-BATCH-2", "Batch Campaign 2",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "batch2@test.com", false, false);
        campaignRepository.save(campaign2);

        SurveyUnitDB su1 = createSurveyUnit("SU-MC1", campaign, ou1, interviewer1);
        su1.getStates().add(new StateDB(BEFORE_DAY, su1, StateType.FIN));

        SurveyUnitDB su2 = createSurveyUnit("SU-MC2", campaign, ou1, interviewer1);
        su2.getStates().add(new StateDB(BEFORE_DAY, su2, StateType.FIN));

        SurveyUnitDB su3 = createSurveyUnit("SU-MC3", campaign2, ou1, interviewer1);
        su3.getStates().add(new StateDB(BEFORE_DAY, su3, StateType.VIC));

        surveyUnitRepository.saveAll(List.of(su1, su2, su3));
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> camp1Stats = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(camp1Stats).isPresent();
        assertThat(camp1Stats.get().getFinStateCount()).isEqualTo(2);
        assertThat(camp1Stats.get().getVicStateCount()).isZero();

        Optional<CampaignDailyStats> camp2Stats = statsAdapter.findCampaignStats("CAMP-BATCH-2", DAY);
        assertThat(camp2Stats).isPresent();
        assertThat(camp2Stats.get().getVicStateCount()).isEqualTo(1);
        assertThat(camp2Stats.get().getFinStateCount()).isZero();
    }

    @Test
    @DisplayName("Should use day-filtered latest state for closing cause filter, ignoring future states")
    void shouldUseDayFilteredStateForClosingCause() {
        // SU has CLO state before day, but a FIN state after the day
        // closing_counts uses day-filtered latest state → latest before day = CLO → closing cause counted
        SurveyUnitDB su = createSurveyUnit("SU-CCDAY", campaign, ou1, interviewer1);
        su.getStates().add(new StateDB(BEFORE_DAY, su, StateType.CLO));
        su.getStates().add(new StateDB(AFTER_DAY, su, StateType.FIN));
        su.setClosingCause(new ClosingCauseDB(null, BEFORE_DAY, ClosingCauseType.NPA, su));

        surveyUnitRepository.save(su);
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        // Both state_counts and closing_counts see CLO (day-filtered) → npa counted
        assertThat(result.get().getCloStateCount()).isEqualTo(1);
        assertThat(result.get().getNpaClosingCauseCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should ignore future contact outcomes and communication statuses")
    void shouldIgnoreFutureOutcomesAndCommunications() {
        // Communication template setup
        CommunicationTemplateDBId templateId = new CommunicationTemplateDBId("meshuggah-fut", campaign.getId());
        CommunicationTemplateDB template = new CommunicationTemplateDB(
                templateId, CommunicationMedium.LETTER, CommunicationType.NOTICE, campaign);
        campaign.setCommunicationTemplates(List.of(template));
        campaignRepository.save(campaign);

        // SU in FIN state with a contact outcome dated AFTER the day — should be ignored
        SurveyUnitDB su1 = createSurveyUnit("SU-FUT1", campaign, ou1, interviewer1);
        su1.getStates().add(new StateDB(BEFORE_DAY, su1, StateType.FIN));
        su1.setContactOutcome(new ContactOutcomeDB(null, AFTER_DAY, ContactOutcomeType.INA, 1, su1));

        // SU with communication request where READY status is dated AFTER the day — should be ignored
        SurveyUnitDB su2 = createSurveyUnit("SU-FUT2", campaign, ou1, interviewer1);
        su2.getStates().add(new StateDB(BEFORE_DAY, su2, StateType.PRC));
        CommunicationRequestDB req = new CommunicationRequestDB(
                null, templateId, CommunicationRequestReason.UNREACHABLE,
                CommunicationRequestEmitter.TOOL, su2, new ArrayList<>());
        CommunicationRequestStatusDB status = new CommunicationRequestStatusDB(
                null, AFTER_DAY, CommunicationStatusType.READY, req);
        req.getStatus().add(status);
        su2.getCommunicationRequests().add(req);

        surveyUnitRepository.saveAll(List.of(su1, su2));
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        CampaignDailyStats stats = result.get();
        assertThat(stats.getInaContactOutcomeCount()).isZero();  // future outcome ignored
        assertThat(stats.getNoticeCommunicationCount()).isZero(); // future READY ignored
    }

    @Test
    @DisplayName("Should count notice_count once per survey unit even if multiple NOTICE requests are READY")
    void shouldDeduplicateNoticeCountPerSurveyUnit() {
        CommunicationTemplateDBId templateId = new CommunicationTemplateDBId("meshuggah-multi", campaign.getId());
        CommunicationTemplateDB template = new CommunicationTemplateDB(
                templateId, CommunicationMedium.LETTER, CommunicationType.NOTICE, campaign);
        campaign.setCommunicationTemplates(List.of(template));
        campaignRepository.save(campaign);

        // 1 SU with 2 separate NOTICE communication requests, both READY → notice_count must be 1
        SurveyUnitDB su = createSurveyUnit("SU-MULTI", campaign, ou1, interviewer1);
        su.getStates().add(new StateDB(BEFORE_DAY, su, StateType.PRC));

        CommunicationRequestDB req1 = new CommunicationRequestDB(
                null, templateId, CommunicationRequestReason.UNREACHABLE,
                CommunicationRequestEmitter.TOOL, su, new ArrayList<>());
        req1.getStatus().add(new CommunicationRequestStatusDB(
                null, BEFORE_DAY, CommunicationStatusType.READY, req1));
        su.getCommunicationRequests().add(req1);

        CommunicationRequestDB req2 = new CommunicationRequestDB(
                null, templateId, CommunicationRequestReason.UNREACHABLE,
                CommunicationRequestEmitter.TOOL, su, new ArrayList<>());
        req2.getStatus().add(new CommunicationRequestStatusDB(
                null, BEFORE_DAY - 1000, CommunicationStatusType.READY, req2));
        su.getCommunicationRequests().add(req2);

        surveyUnitRepository.save(su);
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        assertThat(result.get().getNoticeCommunicationCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should count reminder_count once per survey unit even if multiple REMINDER requests are READY")
    void shouldDeduplicateReminderCountPerSurveyUnit() {
        CommunicationTemplateDBId templateId = new CommunicationTemplateDBId("meshuggah-remind-multi", campaign.getId());
        CommunicationTemplateDB template = new CommunicationTemplateDB(
                templateId, CommunicationMedium.LETTER, CommunicationType.REMINDER, campaign);
        campaign.setCommunicationTemplates(List.of(template));
        campaignRepository.save(campaign);

        // 1 SU with 2 separate REMINDER communication requests, both READY → reminder_count must be 1
        SurveyUnitDB su = createSurveyUnit("SU-REMIND-MULTI", campaign, ou1, interviewer1);
        su.getStates().add(new StateDB(BEFORE_DAY, su, StateType.PRC));

        CommunicationRequestDB req1 = new CommunicationRequestDB(
                null, templateId, CommunicationRequestReason.UNREACHABLE,
                CommunicationRequestEmitter.TOOL, su, new ArrayList<>());
        req1.getStatus().add(new CommunicationRequestStatusDB(
                null, BEFORE_DAY, CommunicationStatusType.READY, req1));
        su.getCommunicationRequests().add(req1);

        CommunicationRequestDB req2 = new CommunicationRequestDB(
                null, templateId, CommunicationRequestReason.UNREACHABLE,
                CommunicationRequestEmitter.TOOL, su, new ArrayList<>());
        req2.getStatus().add(new CommunicationRequestStatusDB(
                null, BEFORE_DAY - 1000, CommunicationStatusType.READY, req2));
        su.getCommunicationRequests().add(req2);

        surveyUnitRepository.save(su);
        entityManager.flush();

        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isPresent();
        assertThat(result.get().getReminderCommunicationCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should produce no rows when no survey units exist")
    void shouldProduceNoRows_whenNoSurveyUnits() {
        batch.run(DAY);
        entityManager.clear();

        Optional<CampaignDailyStats> result = statsAdapter.findCampaignStats("CAMP-BATCH", DAY);
        assertThat(result).isEmpty();
    }

    // ---- helpers ----

    private SurveyUnitDB createSurveyUnit(String id, CampaignDB camp, OrganizationUnitDB ou, InterviewerDB interviewer) {
        SurveyUnitDB su = new SurveyUnitDB();
        su.setId(id);
        su.setCampaign(camp);
        su.setOrganizationUnit(ou);
        su.setInterviewer(interviewer);
        return su;
    }

    private InterviewerDB createInterviewer(String id, String firstName, String lastName) {
        InterviewerDB intw = new InterviewerDB();
        intw.setId(id);
        intw.setFirstName(firstName);
        intw.setLastName(lastName);
        return intw;
    }
}
