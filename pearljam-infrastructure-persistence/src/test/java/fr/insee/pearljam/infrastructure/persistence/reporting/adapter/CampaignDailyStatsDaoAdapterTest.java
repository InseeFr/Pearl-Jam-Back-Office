package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.contracts.surveyunit.dto.closingcause.ClosingCauseDto;
import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.service.CurrentDateService;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CampaignJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.OrganizationUnitJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.reporting.batch.PartitionManager;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.ClosingCauseDB;
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
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
class CampaignDailyStatsDaoAdapterTest {

    @Autowired
    private CampaignDailyStatsDaoAdapter adapter;

    @Autowired
    private JdbcClient jdbc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PartitionManager partitionManager;

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

    DateService dateService;

    static final LocalDate DAY = LocalDate.of(2025, 6, 15);
    static final String CAMPAIGN_ID = "CAMP-TEST";
    static final String OU1_ID = "OU-TEST-1";
    static final String OU2_ID = "OU-TEST-2";
    static final String INTW1_ID = "INTW-TEST-1";
    static final String INTW2_ID = "INTW-TEST-2";

    private CampaignDB campaign;
    private OrganizationUnitDB ou1;
    private OrganizationUnitDB ou2;
    private InterviewerDB intw1;
    private InterviewerDB intw2;

    @BeforeEach
    void setup() {
        dateService = new CurrentDateService(Clock.systemUTC());
        campaign = new CampaignDB(CAMPAIGN_ID, "Test Campaign",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "test@test.com", false, false);
        campaignRepository.save(campaign);

        ou1 = new OrganizationUnitDB(OU1_ID, "Org Unit 1", OrganizationUnitType.LOCAL);
        ou2 = new OrganizationUnitDB(OU2_ID, "Org Unit 2", OrganizationUnitType.LOCAL);
        ouRepository.save(ou1);
        ouRepository.save(ou2);

        intw1 = new InterviewerDB();
        intw1.setId(INTW1_ID);
        intw1.setFirstName("Jean");
        intw1.setLastName("Dupont");
        intw2 = new InterviewerDB();
        intw2.setId(INTW2_ID);
        intw2.setFirstName("Marie");
        intw2.setLastName("Martin");
        interviewerRepository.save(intw1);
        interviewerRepository.save(intw2);

        // survey unit without interviewer (for unaffected count)
        SurveyUnitDB suUnaffected = new SurveyUnitDB();
        suUnaffected.setId("SU-UNAFF-1");
        suUnaffected.setCampaign(campaign);
        suUnaffected.setOrganizationUnit(ou1);
        suUnaffected.setInterviewer(null);
        surveyUnitRepository.save(suUnaffected);
        entityManager.flush();

        partitionManager.ensureMonthlyPartitionExists(DAY);
        insertStats(DAY, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
        insertStats(DAY, CAMPAIGN_ID, OU2_ID, INTW2_ID,
                10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170,
                10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130);
    }

    private void insertStats(LocalDate day, String campaignId, String ouId, String interviewerId,
                             int nvm, int nns, int anv, int vin, int vic, int prc, int aoc,
                             int aps, int ins, int wft, int wfs, int tbr, int fin, int clo, int nva,
                             int notice, int reminder,
                             int npa, int npi, int npx, int row,
                             int ina, int ref, int imp, int ucd, int utr, int ala,
                             int duk, int nuh, int noa) {
        jdbc.sql("""
            INSERT INTO campaign_daily_stats (
                day, campaign_id, organization_unit_id, interviewer_id,
                nvm_count, nns_count, anv_count, vin_count, vic_count,
                prc_count, aoc_count, aps_count, ins_count, wft_count, wfs_count,
                tbr_count, fin_count, clo_count, nva_count,
                notice_count, reminder_count,
                npa_count, npi_count, npx_count, row_count,
                ina_count, ref_count, imp_count, ucd_count, utr_count, ala_count,
                duk_count, nuh_count, noa_count
            ) VALUES (
                :day, :campaignId, :ouId, :interviewerId,
                :nvm, :nns, :anv, :vin, :vic,
                :prc, :aoc, :aps, :ins, :wft, :wfs,
                :tbr, :fin, :clo, :nva,
                :notice, :reminder,
                :npa, :npi, :npx, :row,
                :ina, :ref, :imp, :ucd, :utr, :ala,
                :duk, :nuh, :noa
            )
            """)
                .param("day", day).param("campaignId", campaignId)
                .param("ouId", ouId).param("interviewerId", interviewerId)
                .param("nvm", nvm).param("nns", nns).param("anv", anv).param("vin", vin)
                .param("vic", vic).param("prc", prc).param("aoc", aoc).param("aps", aps)
                .param("ins", ins).param("wft", wft).param("wfs", wfs).param("tbr", tbr)
                .param("fin", fin).param("clo", clo).param("nva", nva)
                .param("notice", notice).param("reminder", reminder)
                .param("npa", npa).param("npi", npi).param("npx", npx).param("row", row)
                .param("ina", ina).param("ref", ref).param("imp", imp)
                .param("ucd", ucd).param("utr", utr).param("ala", ala)
                .param("duk", duk).param("nuh", nuh).param("noa", noa)
                .update();
    }

    // ---- findCampaignStats ----

    @Test
    @DisplayName("Should return aggregated stats across all OUs for a campaign")
    void findCampaignStats_shouldAggregateAllOUs() {
        Optional<CampaignDailyStats> result = adapter.findCampaignStats(CAMPAIGN_ID, DAY);

        assertThat(result).isPresent();
        CampaignDailyStats stats = result.get();
        assertThat(stats.getCampaignId()).isEqualTo(CAMPAIGN_ID);
        assertThat(stats.getCampaignLabel()).isEqualTo("Test Campaign");
        assertThat(stats.getNvmStateCount()).isEqualTo(11);   // 1+10
        assertThat(stats.getNnsStateCount()).isEqualTo(22);   // 2+20
        assertThat(stats.getTbrStateCount()).isEqualTo(132);  // 12+120
        assertThat(stats.getFinStateCount()).isEqualTo(143);  // 13+130
        assertThat(stats.getCloStateCount()).isEqualTo(154);  // 14+140
        assertThat(stats.getNoticeCommunicationCount()).isEqualTo(176); // 16+160
        assertThat(stats.getNpaClosingCauseCount()).isEqualTo(11);     // 1+10
        assertThat(stats.getInaContactOutcomeCount()).isEqualTo(55);   // 5+50
        assertThat(stats.getUnaffectedCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return empty when campaign not found")
    void findCampaignStats_shouldReturnEmpty_whenCampaignNotFound() {
        Optional<CampaignDailyStats> result = adapter.findCampaignStats("UNKNOWN", DAY);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when day has no data")
    void findCampaignStats_shouldReturnEmpty_whenDayHasNoData() {
        LocalDate otherDay = DAY.plusDays(1);
        partitionManager.ensureMonthlyPartitionExists(otherDay);
        Optional<CampaignDailyStats> result = adapter.findCampaignStats(CAMPAIGN_ID, otherDay);
        assertThat(result).isEmpty();
    }

    // ---- findCampaignStatsForOrganizationUnits ----

    @Test
    @DisplayName("Should return stats filtered by OU")
    void findCampaignStatsForOUs_shouldFilterByOU() {
        Optional<CampaignDailyStats> result =
                adapter.findCampaignStatsForOrganizationUnits(CAMPAIGN_ID, List.of(OU1_ID), DAY);

        assertThat(result).isPresent();
        CampaignDailyStats stats = result.get();
        assertThat(stats.getNvmStateCount()).isEqualTo(1);
        assertThat(stats.getTbrStateCount()).isEqualTo(12);
        assertThat(stats.getFinStateCount()).isEqualTo(13);
        assertThat(stats.getNpaClosingCauseCount()).isEqualTo(1);
        assertThat(stats.getInaContactOutcomeCount()).isEqualTo(5);
        assertThat(stats.getUnaffectedCount()).isEqualTo(1);
        assertThat(stats.getAllocatedCount()).isEqualTo(105);
    }

    @Test
    @DisplayName("Should aggregate when multiple OUs provided")
    void findCampaignStatsForOUs_shouldAggregate_whenMultipleOUs() {
        Optional<CampaignDailyStats> result =
                adapter.findCampaignStatsForOrganizationUnits(CAMPAIGN_ID, List.of(OU1_ID, OU2_ID), DAY);

        assertThat(result).isPresent();
        assertThat(result.get().getNvmStateCount()).isEqualTo(11);
        assertThat(result.get().getTbrStateCount()).isEqualTo(132);
    }

    @Test
    @DisplayName("Should return zeros when OU has no data")
    void findCampaignStatsForOUs_shouldReturnZeros_whenOUNotFound() {
        Optional<CampaignDailyStats> result =
                adapter.findCampaignStatsForOrganizationUnits(CAMPAIGN_ID, List.of("UNKNOWN-OU"), DAY);
        assertThat(result).isPresent();
        assertThat(result.get().getAllocatedCount()).isZero();
    }

    // ---- getOrganizationUnitsStats ----

    @Test
    @DisplayName("Should return per-OU breakdown")
    void getOrganizationUnitsStats_shouldReturnPerOU() {
        List<OrganizationUnitDailyStats> result =
                adapter.getOrganizationUnitsStats(CAMPAIGN_ID, DAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(OrganizationUnitDailyStats::getOuId)
                .containsExactlyInAnyOrder(OU1_ID, OU2_ID);

        OrganizationUnitDailyStats ou1Stats = result.stream()
                .filter(s -> s.getOuId().equals(OU1_ID)).findFirst().orElseThrow();
        assertThat(ou1Stats.getOuLabel()).isEqualTo("Org Unit 1");
        assertThat(ou1Stats.getNvmStateCount()).isEqualTo(1);
        assertThat(ou1Stats.getTbrStateCount()).isEqualTo(12);
        assertThat(ou1Stats.getUnaffectedCount()).isEqualTo(1);
        assertThat(ou1Stats.getAllocatedCount()).isEqualTo(105);

        OrganizationUnitDailyStats ou2Stats = result.stream()
                .filter(s -> s.getOuId().equals(OU2_ID)).findFirst().orElseThrow();
        assertThat(ou2Stats.getOuLabel()).isEqualTo("Org Unit 2");
        assertThat(ou2Stats.getNvmStateCount()).isEqualTo(10);
        assertThat(ou2Stats.getTbrStateCount()).isEqualTo(120);
        assertThat(ou2Stats.getUnaffectedCount()).isZero();
    }

    // ---- getCampaignsStats ----

    @Test
    @DisplayName("Should return stats for multiple campaigns filtered by OUs")
    void getCampaignsStats_shouldReturnForMultipleCampaigns() {
        CampaignDB campaign2 = new CampaignDB("CAMP-TEST-2", "Test Campaign 2",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "t@t.com", false, false);
        campaignRepository.save(campaign2);
        entityManager.flush();
        insertStats(DAY, "CAMP-TEST-2", OU1_ID, INTW1_ID,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5);

        List<CampaignDailyStats> result = adapter.getCampaignsStats(
                List.of(CAMPAIGN_ID, "CAMP-TEST-2"), List.of(OU1_ID), DAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CampaignDailyStats::getCampaignId)
                .containsExactlyInAnyOrder(CAMPAIGN_ID, "CAMP-TEST-2");
    }

    @Test
    @DisplayName("Should filter by OU when getting campaigns stats")
    void getCampaignsStats_shouldFilterByOU() {
        List<CampaignDailyStats> result = adapter.getCampaignsStats(
                List.of(CAMPAIGN_ID), List.of(OU1_ID), DAY);

        assertThat(result).hasSize(1);
        CampaignDailyStats stats = result.getFirst();
        assertThat(stats.getCampaignId()).isEqualTo(CAMPAIGN_ID);
        assertThat(stats.getCampaignLabel()).isEqualTo("Test Campaign");
        assertThat(stats.getNvmStateCount()).isEqualTo(1);
        assertThat(stats.getUnaffectedCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should count unaffected SUs only within requested OUs")
    void getCampaignsStats_shouldFilterUnaffectedByOU() {
        SurveyUnitDB suUnaffected2 = new SurveyUnitDB();
        suUnaffected2.setId("SU-UNAFF-2");
        CampaignDB camp = campaignRepository.findById(CAMPAIGN_ID).orElseThrow();
        OrganizationUnitDB ou2 = ouRepository.findById(OU2_ID).orElseThrow();
        suUnaffected2.setCampaign(camp);
        suUnaffected2.setOrganizationUnit(ou2);
        suUnaffected2.setInterviewer(null);
        surveyUnitRepository.save(suUnaffected2);
        entityManager.flush();

        List<CampaignDailyStats> resultOU1 = adapter.getCampaignsStats(
                List.of(CAMPAIGN_ID), List.of(OU1_ID), DAY);
        assertThat(resultOU1).hasSize(1);
        assertThat(resultOU1.getFirst().getUnaffectedCount()).isEqualTo(1);

        List<CampaignDailyStats> resultOU2 = adapter.getCampaignsStats(
                List.of(CAMPAIGN_ID), List.of(OU2_ID), DAY);
        assertThat(resultOU2).hasSize(1);
        assertThat(resultOU2.getFirst().getUnaffectedCount()).isEqualTo(1);

        List<CampaignDailyStats> resultBoth = adapter.getCampaignsStats(
                List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), DAY);
        assertThat(resultBoth).hasSize(1);
        assertThat(resultBoth.getFirst().getUnaffectedCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return empty list when no campaigns match")
    void getCampaignsStats_shouldReturnEmpty_whenNoCampaigns() {
        List<CampaignDailyStats> result = adapter.getCampaignsStats(
                List.of("UNKNOWN"), List.of(OU1_ID), DAY);
        assertThat(result).isEmpty();
    }

    // ---- getInterviewerStats ----

    @Test
    @DisplayName("Should return per-interviewer stats for campaign and OUs")
    void getInterviewerStats_shouldReturnPerInterviewer() {
        List<InterviewerDailyStats> result =
                adapter.getInterviewerStats(CAMPAIGN_ID, List.of(OU1_ID, OU2_ID), DAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(InterviewerDailyStats::getInterviewerId)
                .containsExactlyInAnyOrder(INTW1_ID, INTW2_ID);

        InterviewerDailyStats intw1Stats = result.stream()
                .filter(s -> s.getInterviewerId().equals(INTW1_ID)).findFirst().orElseThrow();
        assertThat(intw1Stats.getInterviewerFirstName()).isEqualTo("Jean");
        assertThat(intw1Stats.getInterviewerLastName()).isEqualTo("Dupont");
        assertThat(intw1Stats.getNvmStateCount()).isEqualTo(1);
        assertThat(intw1Stats.getTbrStateCount()).isEqualTo(12);
        assertThat(intw1Stats.getNpaClosingCauseCount()).isEqualTo(1);
        assertThat(intw1Stats.getInaContactOutcomeCount()).isEqualTo(5);

        InterviewerDailyStats intw2Stats = result.stream()
                .filter(s -> s.getInterviewerId().equals(INTW2_ID)).findFirst().orElseThrow();
        assertThat(intw2Stats.getInterviewerFirstName()).isEqualTo("Marie");
        assertThat(intw2Stats.getInterviewerLastName()).isEqualTo("Martin");
        assertThat(intw2Stats.getNvmStateCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should filter interviewer stats by OU")
    void getInterviewerStats_shouldFilterByOU() {
        List<InterviewerDailyStats> result =
                adapter.getInterviewerStats(CAMPAIGN_ID, List.of(OU1_ID), DAY);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getInterviewerId()).isEqualTo(INTW1_ID);
    }

    @Test
    @DisplayName("Should aggregate interviewer stats across multiple OUs")
    void getInterviewerStats_shouldAggregateAcrossOUs() {
        insertStats(DAY, CAMPAIGN_ID, OU2_ID, INTW1_ID,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3);

        List<InterviewerDailyStats> result =
                adapter.getInterviewerStats(CAMPAIGN_ID, List.of(OU1_ID, OU2_ID), DAY);

        InterviewerDailyStats intw1Stats = result.stream()
                .filter(s -> s.getInterviewerId().equals(INTW1_ID)).findFirst().orElseThrow();
        assertThat(intw1Stats.getNvmStateCount()).isEqualTo(4);   // 1 (OU1) + 3 (OU2)
        assertThat(intw1Stats.getTbrStateCount()).isEqualTo(15);  // 12 (OU1) + 3 (OU2)
        assertThat(intw1Stats.getNpaClosingCauseCount()).isEqualTo(4);  // 1 + 3
        assertThat(intw1Stats.getInaContactOutcomeCount()).isEqualTo(8); // 5 + 3
    }

    @Test
    @DisplayName("Should return empty list when no interviewer stats")
    void getInterviewerStats_shouldReturnEmpty_whenNoData() {
        List<InterviewerDailyStats> result =
                adapter.getInterviewerStats("UNKNOWN", List.of(OU1_ID), DAY);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return campaign stats filtered by interviewer")
    void getCampaignsStatsForInterviewer_shouldFilterByInterviewer() {
        List<InterviewerCampaignDailyStats> result = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), DAY);

        assertThat(result).hasSize(1);
        InterviewerCampaignDailyStats stats = result.getFirst();
        assertThat(stats.getCampaignId()).isEqualTo(CAMPAIGN_ID);
        assertThat(stats.getCampaignLabel()).isEqualTo("Test Campaign");
        assertThat(stats.getNvmStateCount()).isEqualTo(1);
        assertThat(stats.getTbrStateCount()).isEqualTo(12);
        assertThat(stats.getNpaClosingCauseCount()).isEqualTo(1);
        assertThat(stats.getInaContactOutcomeCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return empty list when interviewer has no stats")
    void getCampaignsStatsForInterviewer_shouldReturnEmpty_whenNoStats() {
        List<InterviewerCampaignDailyStats> result = adapter.getCampaignsStatsForInterviewer(
                "UNKNOWN-INTW", List.of(CAMPAIGN_ID), List.of(OU1_ID), DAY);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should filter by campaign IDs when getting interviewer stats")
    void getCampaignsStatsForInterviewer_shouldFilterByCampaignIds() {
        CampaignDB campaign2 = new CampaignDB("CAMP-TEST-2", "Test Campaign 2",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "t@t.com", false, false);
        campaignRepository.save(campaign2);
        entityManager.flush();
        insertStats(DAY, "CAMP-TEST-2", OU1_ID, INTW1_ID,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5);

        List<InterviewerCampaignDailyStats> result = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID, "CAMP-TEST-2"), List.of(OU1_ID), DAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(InterviewerCampaignDailyStats::getCampaignId)
                .containsExactlyInAnyOrder(CAMPAIGN_ID, "CAMP-TEST-2");

        InterviewerCampaignDailyStats camp1Stats = result.stream()
                .filter(s -> s.getCampaignId().equals(CAMPAIGN_ID))
                .findFirst().orElseThrow();
        assertThat(camp1Stats.getNvmStateCount()).isEqualTo(1);

        InterviewerCampaignDailyStats camp2Stats = result.stream()
                .filter(s -> s.getCampaignId().equals("CAMP-TEST-2"))
                .findFirst().orElseThrow();
        assertThat(camp2Stats.getNvmStateCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should filter by OUs when getting interviewer stats")
    void getCampaignsStatsForInterviewer_shouldFilterByOUs() {
        insertStats(DAY, CAMPAIGN_ID, OU2_ID, INTW1_ID,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3);

        List<InterviewerCampaignDailyStats> resultOU1 = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID), DAY);
        assertThat(resultOU1).hasSize(1);
        assertThat(resultOU1.getFirst().getNvmStateCount()).isEqualTo(1);

        List<InterviewerCampaignDailyStats> resultOU2 = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU2_ID), DAY);
        assertThat(resultOU2).hasSize(1);
        assertThat(resultOU2.getFirst().getNvmStateCount()).isEqualTo(3);

        List<InterviewerCampaignDailyStats> resultBoth = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), DAY);
        assertThat(resultBoth).hasSize(1);
        assertThat(resultBoth.getFirst().getNvmStateCount()).isEqualTo(4); // 1 + 3
    }

    @Test
    @DisplayName("Should count correct affected SUs within requested OUs for interviewer when unaffected SUs exist")
    void getCampaignsStatsForInterviewer_shouldFilterUnaffectedByOU() {
        insertStats(DAY, CAMPAIGN_ID, OU2_ID, INTW1_ID,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3);

        SurveyUnitDB suUnaffected2 = new SurveyUnitDB();
        suUnaffected2.setId("SU-UNAFF-2");
        CampaignDB camp = campaignRepository.findById(CAMPAIGN_ID).orElseThrow();
        OrganizationUnitDB ou2 = ouRepository.findById(OU2_ID).orElseThrow();
        suUnaffected2.setCampaign(camp);
        suUnaffected2.setOrganizationUnit(ou2);
        suUnaffected2.setInterviewer(null);
        surveyUnitRepository.save(suUnaffected2);
        entityManager.flush();

        List<InterviewerCampaignDailyStats> resultOU1 = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID), DAY);
        assertThat(resultOU1).hasSize(1);

        List<InterviewerCampaignDailyStats> resultOU2 = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU2_ID), DAY);
        assertThat(resultOU2).hasSize(1);

        List<InterviewerCampaignDailyStats> resultBoth = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), DAY);
        assertThat(resultBoth).hasSize(1);
    }

    @Test
    @DisplayName("Should return empty list when no campaigns match for interviewer")
    void getCampaignsStatsForInterviewer_shouldReturnEmpty_whenNoCampaigns() {
        List<InterviewerCampaignDailyStats> result = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of("UNKNOWN-CAMP"), List.of(OU1_ID), DAY);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when day has no data for interviewer")
    void getCampaignsStatsForInterviewer_shouldReturnEmpty_whenDayHasNoData() {
        LocalDate otherDay = DAY.plusDays(1);
        partitionManager.ensureMonthlyPartitionExists(otherDay);
        List<InterviewerCampaignDailyStats> result = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID), otherDay);
        assertThat(result).isEmpty();
    }

    // ====================================================================================
    // Tests for updateDailyStatsForSurveyUnits
    // ====================================================================================

    /**
     * Helper: assign an interviewer to SU-UNAFF-1 and return it, so it is eligible
     * for stats updates (the SQL filters out SUs without interviewer).
     */
    private SurveyUnitDB assignInterviewerToDefaultSU() {
        SurveyUnitDB su = surveyUnitRepository.findById("SU-UNAFF-1").orElseThrow();
        su.setInterviewer(intw1);
        surveyUnitRepository.save(su);
        entityManager.flush();
        return su;
    }

    @Test
    @DisplayName("Should do nothing when surveyUnitIds is empty")
    void updateDailyStatsForSurveyUnits_shouldDoNothing_whenEmptyList() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);

        adapter.updateDailyStatsForSurveyUnits(List.of(), StateType.VIN, ClosingCauseType.NPA);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getVinStateCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should update state counts when transitioning between non-CLO states")
    void updateDailyStatsForSurveyUnits_shouldUpdateStateCounts_whenNonCloTransition() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        // Previous state, then the new state that was just persisted before this call
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su, StateType.NVM));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su, StateType.VIN));
        entityManager.flush();

        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.VIN, ClosingCauseType.NPA);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getNvmStateCount()).isZero();
        assertThat(stats.getVinStateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle null new state — only provisional closing cause, no state transition")
    void updateDailyStatsForSurveyUnits_shouldHandleNullNewState() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su, StateType.NVM));

        su.setClosingCause(new ClosingCauseDB(new ClosingCauseDto(dateService.getCurrentTimestamp(), ClosingCauseType.NPI), su));
        surveyUnitRepository.save(su);
        entityManager.flush();

        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), null, ClosingCauseType.NPI);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();

        CampaignDailyStats cds = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        // Debug: print all relevant counts
        System.out.println("nvm=" + cds.getNvmStateCount());
        System.out.println("npa_prov=" + cds.getNpaProvisionalClosingCauseCount());
        System.out.println("npi_prov=" + cds.getNpiProvisionalClosingCauseCount());
        System.out.println("npx_prov=" + cds.getNpxProvisionalClosingCauseCount());
        System.out.println("row_prov=" + cds.getRowProvisionalClosingCauseCount());
        assertThat(stats.getNvmStateCount()).isEqualTo(1);


        assertThat(stats.getNpiProvisionalClosingCauseCount()).isEqualTo(1);
        assertThat(stats.getNpaProvisionalClosingCauseCount()).isZero();
    }

    @Test
    @DisplayName("Should update CLO count and finalise NPA closing cause")
    void updateDailyStatsForSurveyUnits_shouldUpdateCounts_whenNpa() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        long now = dateService.getCurrentTimestamp();
        stateRepository.save(new StateDB(now - 1000, su, StateType.TBR));
        stateRepository.save(new StateDB(now, su, StateType.CLO));

        // closing cause must be persisted so the SQL can read it via prev_cc
        su.setClosingCause(new ClosingCauseDB(new ClosingCauseDto(now, ClosingCauseType.NPA), su));
        surveyUnitRepository.save(su);

        entityManager.flush();

        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.CLO, ClosingCauseType.NPA);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getTbrStateCount()).isZero();
        assertThat(stats.getCloStateCount()).isEqualTo(1);
        assertThat(stats.getNpaProvisionalClosingCauseCount()).isZero();
        assertThat(stats.getNpaClosingCauseCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update CLO count and finalise NPI closing cause")
    void updateDailyStatsForSurveyUnits_shouldUpdateCounts_whenNpi() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
                0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su, StateType.TBR));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su, StateType.CLO));

        entityManager.flush();

        su.setClosingCause(new ClosingCauseDB(new ClosingCauseDto(dateService.getCurrentTimestamp(), ClosingCauseType.NPI), su));
        surveyUnitRepository.save(su);
        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.CLO, ClosingCauseType.NPI);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getTbrStateCount()).isZero();
        assertThat(stats.getCloStateCount()).isEqualTo(1);
        assertThat(stats.getNpiProvisionalClosingCauseCount()).isZero();
        assertThat(stats.getNpiClosingCauseCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update CLO count and finalise NPX closing cause")
    void updateDailyStatsForSurveyUnits_shouldUpdateCounts_whenNpx() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);

        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su, StateType.TBR));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su, StateType.CLO));
        entityManager.flush();

        su.setClosingCause(new ClosingCauseDB(new ClosingCauseDto(dateService.getCurrentTimestamp(), ClosingCauseType.NPX), su));
        surveyUnitRepository.save(su);
        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.CLO, ClosingCauseType.NPX);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getTbrStateCount()).isZero();
        assertThat(stats.getCloStateCount()).isEqualTo(1);
        assertThat(stats.getNpxProvisionalClosingCauseCount()).isZero();
        assertThat(stats.getNpxClosingCauseCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update CLO count and finalise ROW closing cause")
    void updateDailyStatsForSurveyUnits_shouldUpdateCounts_whenRow() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);

        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su, StateType.TBR));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su, StateType.CLO));
        entityManager.flush();

        su.setClosingCause(new ClosingCauseDB(new ClosingCauseDto(dateService.getCurrentTimestamp(), ClosingCauseType.ROW), su));
        surveyUnitRepository.save(su);
        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.CLO, ClosingCauseType.ROW);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getTbrStateCount()).isZero();
        assertThat(stats.getCloStateCount()).isEqualTo(1);
        assertThat(stats.getRowProvisionalClosingCauseCount()).isZero();
        assertThat(stats.getRowClosingCauseCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update multiple survey units in a single call")
    void updateDailyStatsForSurveyUnits_shouldUpdateMultipleSurveyUnits() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);

        SurveyUnitDB su1 = assignInterviewerToDefaultSU();
        SurveyUnitDB su2 = new SurveyUnitDB();
        su2.setId("SU-TEST-MULTI");
        su2.setCampaign(campaign);
        su2.setOrganizationUnit(ou1);
        su2.setInterviewer(intw1);
        surveyUnitRepository.save(su2);
        entityManager.flush();


        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);


        // Both SUs transition NVM → VIN: save old state then new state for each
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 2000, su1, StateType.NVM));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su1, StateType.VIN));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 2000, su2, StateType.NVM));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su2, StateType.VIN));

        su1.setClosingCause(new ClosingCauseDB(new ClosingCauseDto(dateService.getCurrentTimestamp() - 2000, ClosingCauseType.NPA), su1));
        su2.setClosingCause(new ClosingCauseDB(new ClosingCauseDto(dateService.getCurrentTimestamp() - 1000, ClosingCauseType.NPA), su2));
        surveyUnitRepository.save(su1);
        surveyUnitRepository.save(su2);
        entityManager.flush();

        adapter.updateDailyStatsForSurveyUnits(List.of(su1.getId(), su2.getId()), null, ClosingCauseType.NPA);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getNvmStateCount()).isEqualTo(2);
        assertThat(stats.getNpaProvisionalClosingCauseCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should only increment new state when SU has no previous state")
    void updateDailyStatsForSurveyUnits_shouldHandleNoPreviousState() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        // Only the new state row — no previous state exists
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su, StateType.VIN));
        entityManager.flush();

        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.VIN, ClosingCauseType.NPA);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getVinStateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update stats for the correct OU and interviewer only")
    void updateDailyStatsForSurveyUnits_shouldUpdateCorrectOUAndInterviewer() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);

        SurveyUnitDB su1 = assignInterviewerToDefaultSU(); // intw1, ou1
        SurveyUnitDB su2 = new SurveyUnitDB();
        su2.setId("SU-TEST-OU2");
        su2.setCampaign(campaign);
        su2.setOrganizationUnit(ou2);
        su2.setInterviewer(intw2);
        surveyUnitRepository.save(su2);
        entityManager.flush();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        insertStats(today, CAMPAIGN_ID, OU2_ID, INTW2_ID,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        // su1 transitions NVM → VIN
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su1, StateType.NVM));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su1, StateType.VIN));
        // su2 stays at NVM (no new state row)
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su2, StateType.NVM));
        entityManager.flush();

        // Only update su1
        adapter.updateDailyStatsForSurveyUnits(List.of(su1.getId()), StateType.VIN, ClosingCauseType.NPA);

        CampaignDailyStats statsOU1 = adapter.findCampaignStatsForOrganizationUnits(
                CAMPAIGN_ID, List.of(OU1_ID), today).orElseThrow();
        CampaignDailyStats statsOU2 = adapter.findCampaignStatsForOrganizationUnits(
                CAMPAIGN_ID, List.of(OU2_ID), today).orElseThrow();

        assertThat(statsOU1.getNvmStateCount()).isZero();
        assertThat(statsOU1.getVinStateCount()).isEqualTo(1);
        assertThat(statsOU2.getNvmStateCount()).isEqualTo(1);
        assertThat(statsOU2.getVinStateCount()).isZero();
    }

    @Test
    @DisplayName("Should update today's partition only, not yesterday's")
    void updateDailyStatsForSurveyUnits_shouldUseCurrentDate() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        partitionManager.ensureMonthlyPartitionExists(today);
        partitionManager.ensureMonthlyPartitionExists(yesterday);

        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(yesterday, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su, StateType.NVM));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su, StateType.VIN));
        entityManager.flush();

        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.VIN, ClosingCauseType.NPA);

        CampaignDailyStats statsYesterday = adapter.findCampaignStats(CAMPAIGN_ID, yesterday).orElseThrow();
        CampaignDailyStats statsToday = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();

        assertThat(statsYesterday.getNvmStateCount()).isEqualTo(1);
        assertThat(statsYesterday.getVinStateCount()).isZero();
        assertThat(statsToday.getNvmStateCount()).isZero();
        assertThat(statsToday.getVinStateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle all non-CLO state types in a transition")
    void updateDailyStatsForSurveyUnits_shouldHandleAllStateTypes() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);
        SurveyUnitDB su = assignInterviewerToDefaultSU();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su, StateType.NNS));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su, StateType.APS));
        entityManager.flush();

        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.APS, ClosingCauseType.NPA);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getNnsStateCount()).isZero();
        assertThat(stats.getApsStateCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should not update stats for SUs without interviewer")
    void updateDailyStatsForSurveyUnits_shouldNotUpdate_whenMissingInterviewer() {
        LocalDate today = LocalDate.now();
        partitionManager.ensureMonthlyPartitionExists(today);

        // SU-UNAFF-1 deliberately left without an interviewer
        SurveyUnitDB su = surveyUnitRepository.findById("SU-UNAFF-1").orElseThrow();
        assertThat(su.getInterviewer()).isNull();

        insertStats(today, CAMPAIGN_ID, OU1_ID, INTW1_ID,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        stateRepository.save(new StateDB(dateService.getCurrentTimestamp() - 1000, su, StateType.NVM));
        stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su, StateType.VIN));
        entityManager.flush();

        adapter.updateDailyStatsForSurveyUnits(List.of(su.getId()), StateType.VIN, ClosingCauseType.NPA);

        CampaignDailyStats stats = adapter.findCampaignStats(CAMPAIGN_ID, today).orElseThrow();
        assertThat(stats.getNvmStateCount()).isEqualTo(1);
        assertThat(stats.getVinStateCount()).isZero();
    }
}