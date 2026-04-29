package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CampaignJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.OrganizationUnitJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.reporting.batch.PartitionManager;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.InterviewerJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

    static final LocalDate DAY = LocalDate.of(2025, 6, 15);
    static final String CAMPAIGN_ID = "CAMP-TEST";
    static final String OU1_ID = "OU-TEST-1";
    static final String OU2_ID = "OU-TEST-2";
    static final String INTW1_ID = "INTW-TEST-1";
    static final String INTW2_ID = "INTW-TEST-2";

    @BeforeEach
    void setup() {
        // reference data
        CampaignDB campaign = new CampaignDB(CAMPAIGN_ID, "Test Campaign",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "test@test.com", false, false);
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

        // survey unit without interviewer (for unaffected count)
        SurveyUnitDB suUnaffected = new SurveyUnitDB();
        suUnaffected.setId("SU-UNAFF-1");
        suUnaffected.setCampaign(campaign);
        suUnaffected.setOrganizationUnit(ou1);
        suUnaffected.setInterviewer(null);
        surveyUnitRepository.save(suUnaffected);
        entityManager.flush();

        // create partition and insert stats
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
        // SUM of OU1 + OU2
        assertThat(stats.getNvmStateCount()).isEqualTo(11);   // 1+10
        assertThat(stats.getNnsStateCount()).isEqualTo(22);   // 2+20
        assertThat(stats.getTbrStateCount()).isEqualTo(132);  // 12+120
        assertThat(stats.getFinStateCount()).isEqualTo(143);  // 13+130
        assertThat(stats.getCloStateCount()).isEqualTo(154);  // 14+140
        assertThat(stats.getNoticeCommunicationCount()).isEqualTo(176); // 16+160
        assertThat(stats.getNpaClosingCauseCount()).isEqualTo(11);     // 1+10
        assertThat(stats.getInaContactOutcomeCount()).isEqualTo(55);   // 5+50
        assertThat(stats.getUnaffectedCount()).isEqualTo(1);           // 1 survey unit without interviewer
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
        // only OU1 values
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
        // SUM over zero rows with COALESCE returns a single row of zeros
        assertThat(result).isPresent();
        assertThat(result.get().getAllocatedCount()).isZero();
    }

    // ---- getOrganizationUnitsStats ----

    @Test
    @DisplayName("Should return per-OU breakdown")
    void getOrganizationUnitsStats_shouldReturnPerOU() {
        List<OrganizationUnitDailyStats> result =
                adapter.getOrganizationUnitsStats(CAMPAIGN_ID, List.of(OU1_ID, OU2_ID), DAY);

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

    @Test
    @DisplayName("Should return empty list when no stats for OUs")
    void getOrganizationUnitsStats_shouldReturnEmpty_whenNoData() {
        List<OrganizationUnitDailyStats> result =
                adapter.getOrganizationUnitsStats(CAMPAIGN_ID, List.of("UNKNOWN-OU"), DAY);
        assertThat(result).isEmpty();
    }

    // ---- getCampaignsStats ----

    @Test
    @DisplayName("Should return stats for multiple campaigns filtered by OUs")
    void getCampaignsStats_shouldReturnForMultipleCampaigns() {
        // add a second campaign
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
        assertThat(stats.getNvmStateCount()).isEqualTo(1);  // only OU1
        assertThat(stats.getUnaffectedCount()).isEqualTo(1); // 1 unaffected SU in OU1
    }

    @Test
    @DisplayName("Should count unaffected SUs only within requested OUs")
    void getCampaignsStats_shouldFilterUnaffectedByOU() {
        // Add an unaffected SU in OU2
        SurveyUnitDB suUnaffected2 = new SurveyUnitDB();
        suUnaffected2.setId("SU-UNAFF-2");
        CampaignDB camp = campaignRepository.findById(CAMPAIGN_ID).orElseThrow();
        OrganizationUnitDB ou2 = ouRepository.findById(OU2_ID).orElseThrow();
        suUnaffected2.setCampaign(camp);
        suUnaffected2.setOrganizationUnit(ou2);
        suUnaffected2.setInterviewer(null);
        surveyUnitRepository.save(suUnaffected2);
        entityManager.flush();

        // Filter on OU1 only → should see only 1 unaffected (the one from setup in OU1)
        List<CampaignDailyStats> resultOU1 = adapter.getCampaignsStats(
                List.of(CAMPAIGN_ID), List.of(OU1_ID), DAY);
        assertThat(resultOU1).hasSize(1);
        assertThat(resultOU1.getFirst().getUnaffectedCount()).isEqualTo(1);

        // Filter on OU2 only → should see only 1 unaffected (the one we just added in OU2)
        List<CampaignDailyStats> resultOU2 = adapter.getCampaignsStats(
                List.of(CAMPAIGN_ID), List.of(OU2_ID), DAY);
        assertThat(resultOU2).hasSize(1);
        assertThat(resultOU2.getFirst().getUnaffectedCount()).isEqualTo(1);

        // Both OUs → should see 2 unaffected
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

        InterviewerDailyStats intw1 = result.stream()
                .filter(s -> s.getInterviewerId().equals(INTW1_ID)).findFirst().orElseThrow();
        assertThat(intw1.getInterviewerFirstName()).isEqualTo("Jean");
        assertThat(intw1.getInterviewerLastName()).isEqualTo("Dupont");
        assertThat(intw1.getNvmStateCount()).isEqualTo(1);
        assertThat(intw1.getTbrStateCount()).isEqualTo(12);
        assertThat(intw1.getNpaClosingCauseCount()).isEqualTo(1);
        assertThat(intw1.getInaContactOutcomeCount()).isEqualTo(5);

        InterviewerDailyStats intw2 = result.stream()
                .filter(s -> s.getInterviewerId().equals(INTW2_ID)).findFirst().orElseThrow();
        assertThat(intw2.getInterviewerFirstName()).isEqualTo("Marie");
        assertThat(intw2.getInterviewerLastName()).isEqualTo("Martin");
        assertThat(intw2.getNvmStateCount()).isEqualTo(10);
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
        // Add stats for INTW1 in OU2 (INTW1 already has stats in OU1 from setup)
        insertStats(DAY, CAMPAIGN_ID, OU2_ID, INTW1_ID,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3);

        List<InterviewerDailyStats> result =
                adapter.getInterviewerStats(CAMPAIGN_ID, List.of(OU1_ID, OU2_ID), DAY);

        // INTW1 should appear once with aggregated values (OU1 + OU2)
        // INTW2 stays with OU2 values only
        InterviewerDailyStats intw1 = result.stream()
                .filter(s -> s.getInterviewerId().equals(INTW1_ID)).findFirst().orElseThrow();
        assertThat(intw1.getNvmStateCount()).isEqualTo(4);  // 1 (OU1) + 3 (OU2)
        assertThat(intw1.getTbrStateCount()).isEqualTo(15);  // 12 (OU1) + 3 (OU2)
        assertThat(intw1.getNpaClosingCauseCount()).isEqualTo(4);  // 1 + 3
        assertThat(intw1.getInaContactOutcomeCount()).isEqualTo(8); // 5 + 3
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
        // Only INTW1's stats (from OU1)
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
        // Add a second campaign
        CampaignDB campaign2 = new CampaignDB("CAMP-TEST-2", "Test Campaign 2",
                IdentificationConfiguration.HOUSEF2F, ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F, "t@t.com", false, false);
        campaignRepository.save(campaign2);
        entityManager.flush();
        insertStats(DAY, "CAMP-TEST-2", OU1_ID, INTW1_ID,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5);

        // Get stats for both campaigns
        List<InterviewerCampaignDailyStats> result = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID, "CAMP-TEST-2"), List.of(OU1_ID), DAY);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(InterviewerCampaignDailyStats::getCampaignId)
                .containsExactlyInAnyOrder(CAMPAIGN_ID, "CAMP-TEST-2");

        // Verify INTW1's stats for each campaign
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
        // INTW1 has stats in OU1, let's add stats in OU2 as well
        insertStats(DAY, CAMPAIGN_ID, OU2_ID, INTW1_ID,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3);

        // Get stats for OU1 only
        List<InterviewerCampaignDailyStats> resultOU1 = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID), DAY);
        assertThat(resultOU1).hasSize(1);
        assertThat(resultOU1.getFirst().getNvmStateCount()).isEqualTo(1);

        // Get stats for OU2 only
        List<InterviewerCampaignDailyStats> resultOU2 = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU2_ID), DAY);
        assertThat(resultOU2).hasSize(1);
        assertThat(resultOU2.getFirst().getNvmStateCount()).isEqualTo(3);

        // Get stats for both OUs (should aggregate)
        List<InterviewerCampaignDailyStats> resultBoth = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID, OU2_ID), DAY);
        assertThat(resultBoth).hasSize(1);
        assertThat(resultBoth.getFirst().getNvmStateCount()).isEqualTo(4); // 1 + 3
    }

    @Test
    @DisplayName("Should count correct affected SUs within requested OUs for interviewer when unaffected SUs exist")
    void getCampaignsStatsForInterviewer_shouldFilterUnaffectedByOU() {
        // Add stats for INTW1 in OU2 first
        insertStats(DAY, CAMPAIGN_ID, OU2_ID, INTW1_ID,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3);
        
        // Add an unaffected SU in OU2
        SurveyUnitDB suUnaffected2 = new SurveyUnitDB();
        suUnaffected2.setId("SU-UNAFF-2");
        CampaignDB camp = campaignRepository.findById(CAMPAIGN_ID).orElseThrow();
        OrganizationUnitDB ou2 = ouRepository.findById(OU2_ID).orElseThrow();
        suUnaffected2.setCampaign(camp);
        suUnaffected2.setOrganizationUnit(ou2);
        suUnaffected2.setInterviewer(null);
        surveyUnitRepository.save(suUnaffected2);
        entityManager.flush();

        // Filter on OU1 only → should see only 1 unaffected (the one from setup in OU1)
        List<InterviewerCampaignDailyStats> resultOU1 = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU1_ID), DAY);
        assertThat(resultOU1).hasSize(1);

        // Filter on OU2 only → should see only 1 unaffected (the one we just added in OU2)
        List<InterviewerCampaignDailyStats> resultOU2 = adapter.getCampaignsStatsForInterviewer(
                INTW1_ID, List.of(CAMPAIGN_ID), List.of(OU2_ID), DAY);
        assertThat(resultOU2).hasSize(1);

        // Both OUs → should see 2 unaffected
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
}
