package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CampaignDailyStatsDaoAdapter implements CampaignDailyStatsRepositoryPort {

    private final JdbcClient jdbc;

    private static final String OU_IDS_PARAM = "ouIds";
    private static final String CAMPAIGN_ID_PARAM = "campaignId";
    private static final String DAY_PARAM = "day";

    private static final String DATA_SELECTION = """
        COALESCE(SUM(cds.nvm_count),0) AS nvmStateCount,
        COALESCE(SUM(cds.nns_count),0) AS nnsStateCount,
        COALESCE(SUM(cds.anv_count),0) AS anvStateCount,
        COALESCE(SUM(cds.vin_count),0) AS vinStateCount,
        COALESCE(SUM(cds.vic_count),0) AS vicStateCount,
        COALESCE(SUM(cds.prc_count),0) AS prcStateCount,
        COALESCE(SUM(cds.aoc_count),0) AS aocStateCount,
        COALESCE(SUM(cds.aps_count),0) AS apsStateCount,
        COALESCE(SUM(cds.ins_count),0) AS insStateCount,
        COALESCE(SUM(cds.wft_count),0) AS wftStateCount,
        COALESCE(SUM(cds.wfs_count),0) AS wfsStateCount,
        COALESCE(SUM(cds.tbr_count),0) AS tbrStateCount,
        COALESCE(SUM(cds.fin_count),0) AS finStateCount,
        COALESCE(SUM(cds.clo_count),0) AS cloStateCount,
        COALESCE(SUM(cds.nva_count),0) AS nvaStateCount,
        COALESCE(SUM(cds.notice_count),0) AS noticeCommunicationCount,
        COALESCE(SUM(cds.reminder_count),0) AS reminderCommunicationCount,
        COALESCE(SUM(cds.ina_count),0) AS inaContactOutcomeCount,
        COALESCE(SUM(cds.ref_count),0) AS refContactOutcomeCount,
        COALESCE(SUM(cds.imp_count),0) AS impContactOutcomeCount,
        COALESCE(SUM(cds.ucd_count),0) AS ucdContactOutcomeCount,
        COALESCE(SUM(cds.utr_count),0) AS utrContactOutcomeCount,
        COALESCE(SUM(cds.ala_count),0) AS alaContactOutcomeCount,
        COALESCE(SUM(cds.duk_count),0) AS dukContactOutcomeCount,
        COALESCE(SUM(cds.nuh_count),0) AS nuhContactOutcomeCount,
        COALESCE(SUM(cds.noa_count),0) AS noaContactOutcomeCount,
        COALESCE(SUM(cds.npa_provisional_count),0) AS npaProvisionalClosingCauseCount,
        COALESCE(SUM(cds.npi_provisional_count),0) AS npiProvisionalClosingCauseCount,
        COALESCE(SUM(cds.npx_provisional_count),0) AS npxProvisionalClosingCauseCount,
        COALESCE(SUM(cds.row_provisional_count),0) AS rowProvisionalClosingCauseCount,
        COALESCE(SUM(cds.npa_count),0) AS npaClosingCauseCount,
        COALESCE(SUM(cds.npi_count),0) AS npiClosingCauseCount,
        COALESCE(SUM(cds.npx_count),0) AS npxClosingCauseCount,
        COALESCE(SUM(cds.row_count),0) AS rowClosingCauseCount""";
    private static final String CAMPAIGN_SQL = """
    SELECT
        c.id AS campaignId,
        c.label AS campaignLabel,
        (
            SELECT count(id) FROM survey_unit
            WHERE campaign_id = :campaignId
            AND interviewer_id is NULL
        ) AS unaffectedCount,
        COALESCE(MAX(cds.updated_at), 0) AS updatedAt,
        %s
    FROM campaign_daily_stats cds
    JOIN campaign c ON c.id = cds.campaign_id
    WHERE cds.campaign_id = :campaignId
      AND cds.day = :day
    GROUP BY c.id, c.label
    ORDER by c.label ASC
    """.formatted(DATA_SELECTION);

    @Override
    public Optional<CampaignDailyStats> findCampaignStats(String campaignId, LocalDate day) {
        return jdbc.sql(CAMPAIGN_SQL)
                .param(CAMPAIGN_ID_PARAM, campaignId)
                .param(DAY_PARAM, day)
                .query(CampaignDailyStats.class)
                .optional();
    }

    private static final String OUS_SQL = """
    SELECT
        (
            SELECT COUNT(*)
            FROM survey_unit su
            WHERE su.campaign_id = :campaignId
              AND su.organization_unit_id IN (:ouIds)
              AND su.interviewer_id IS NULL
        ) AS unaffectedCount,
        COALESCE(MAX(cds.updated_at), 0) AS updatedAt,
        %s
        FROM campaign_daily_stats cds
        WHERE campaign_id = :campaignId
          AND organization_unit_id IN (:ouIds)
          AND day = :day
    """.formatted(DATA_SELECTION);

    @Override
    public Optional<CampaignDailyStats> findCampaignStatsForOrganizationUnits(String campaignId, List<String> ouIds, LocalDate day) {
        return jdbc.sql(OUS_SQL)
                .param(CAMPAIGN_ID_PARAM, campaignId)
                .param(OU_IDS_PARAM, ouIds)
                .param(DAY_PARAM, day)
                .query(CampaignDailyStats.class)
                .optional();
    }

    private static final String OUS_LIST_SQL = """
    WITH su_counts AS (
        SELECT
            su.organization_unit_id,
            COUNT(*) AS unaffected
        FROM survey_unit su
        WHERE su.campaign_id = :campaignId
          AND su.interviewer_id IS NULL
        GROUP BY su.organization_unit_id
    )
    SELECT
        ou.id AS ouId,
        ou.label AS ouLabel,
        COALESCE(su.unaffected, 0) AS unaffectedCount,
        COALESCE(MAX(cds.updated_at), 0) AS updatedAt,
        %s
    FROM campaign_daily_stats cds
    JOIN organization_unit ou ON ou.id = cds.organization_unit_id
    LEFT JOIN su_counts su ON su.organization_unit_id = ou.id
    WHERE cds.campaign_id = :campaignId
    AND cds.day = :day
    GROUP BY ou.id, ou.label, su.unaffected
    ORDER BY translate(
       lower(ou.label),
       'àâäáãåçèéêëìíîïñòóôöõùúûüýÿ',
       'aaaaaaceeeeiiiinooooouuuuyy'
    ) ASC;
    """.formatted(DATA_SELECTION);

    @Override
    public List<OrganizationUnitDailyStats> getOrganizationUnitsStats(
            String campaignId,
            LocalDate day) {
        return jdbc.sql(OUS_LIST_SQL)
                .param(CAMPAIGN_ID_PARAM, campaignId)
                .param(DAY_PARAM, day)
                .query(OrganizationUnitDailyStats.class)
                .list();
    }

    private static final String CAMPAIGNS_SQL = """
        WITH filtered_campaigns AS (
            SELECT c.id AS campaign_id
            FROM campaign c
            WHERE c.id IN (:campaignIds)
        ),
        su_counts AS (
            SELECT
                su.campaign_id,
                COUNT(*) AS unaffected
            FROM survey_unit su
            JOIN filtered_campaigns fc
              ON fc.campaign_id = su.campaign_id
            WHERE su.organization_unit_id IN (:ouIds)
            AND su.interviewer_id is NULL
            GROUP BY su.campaign_id
        )
        SELECT
            c.id AS campaignId,
            c.label AS campaignLabel,
            COALESCE(su.unaffected, 0) AS unaffectedCount,
            COALESCE(MIN(cds.updated_at), 0) AS updatedAt,
            %s
        FROM campaign_daily_stats cds
        JOIN campaign c ON c.id = cds.campaign_id
        LEFT JOIN su_counts su ON su.campaign_id = c.id
       WHERE cds.campaign_id IN (:campaignIds)
         AND cds.organization_unit_id IN (:ouIds)
         AND cds.day = :day
        GROUP BY c.id, c.label, su.unaffected
        ORDER by c.label ASC;
    """.formatted(DATA_SELECTION);

    @Override
    public List<CampaignDailyStats> getCampaignsStats(List<String> campaignIds, List<String> ouIds, LocalDate day) {
        return jdbc.sql(CAMPAIGNS_SQL)
                .param("campaignIds", campaignIds)
                .param(OU_IDS_PARAM, ouIds)
                .param(DAY_PARAM, day)
                .query(CampaignDailyStats.class)
                .list();
    }

    private static final String INTERVIEWER_CAMPAIGNS_SQL = """
        SELECT
            c.id AS campaignId,
            c.label AS campaignLabel,
            COALESCE(MAX(cds.updated_at), 0) AS updatedAt,
            %s
        FROM campaign_daily_stats cds
        JOIN campaign c ON c.id = cds.campaign_id
       WHERE cds.campaign_id IN (:campaignIds)
         AND cds.organization_unit_id IN (:ouIds)
         AND cds.day = :day
         AND cds.interviewer_id = :interviewerId
        GROUP BY c.id, c.label
        ORDER by c.label ASC;
    """.formatted(DATA_SELECTION);

    @Override
    public List<InterviewerCampaignDailyStats> getCampaignsStatsForInterviewer(String interviewerId,
                                                                               List<String> campaignIds,
                                                                               List<String> ouIds,
                                                                               LocalDate day) {
        return jdbc.sql(INTERVIEWER_CAMPAIGNS_SQL)
                .param("campaignIds", campaignIds)
                .param(OU_IDS_PARAM, ouIds)
                .param(DAY_PARAM, day)
                .param("interviewerId", interviewerId)
                .query(InterviewerCampaignDailyStats.class)
                .list();
    }

    private static final String INTERVIEWER_SQL = """
        SELECT
            interv.id AS interviewerId,
            interv.first_name AS interviewerFirstName,
            interv.last_name AS interviewerLastName,
            COALESCE(MAX(cds.updated_at), 0) AS updatedAt,
            %s
        FROM campaign_daily_stats cds
        JOIN interviewer interv ON interv.id = cds.interviewer_id
        WHERE cds.campaign_id = :campaignId
          AND cds.organization_unit_id IN (:ouIds)
          AND cds.day = :day
        GROUP BY interv.id, interv.first_name, interv.last_name
        ORDER by LOWER(interv.last_name) ASC, LOWER(interv.first_name) ASC
    """.formatted(DATA_SELECTION);

    @Override
    public List<InterviewerDailyStats> getInterviewerStats(
            String campaignId,
            List<String> ouIds,
            LocalDate day) {

        return jdbc.sql(INTERVIEWER_SQL)
                .param(CAMPAIGN_ID_PARAM, campaignId)
                .param(OU_IDS_PARAM, ouIds)
                .param(DAY_PARAM, day)
                .query(InterviewerDailyStats.class)
                .list();
    }

    private static final String UPDATE_STATES_SQL = """
   UPDATE campaign_daily_stats cds
   SET
   nvm_count = nvm_count
       + CASE WHEN :newState = 'NVM' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_nvm_count ELSE 0 END,

   nns_count = nns_count
       + CASE WHEN :newState = 'NNS' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_nns_count ELSE 0 END,

   anv_count = anv_count
       + CASE WHEN :newState = 'ANV' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_anv_count ELSE 0 END,

   vin_count = vin_count
       + CASE WHEN :newState = 'VIN' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_vin_count ELSE 0 END,

   vic_count = vic_count
       + CASE WHEN :newState = 'VIC' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_vic_count ELSE 0 END,

   prc_count = prc_count
       + CASE WHEN :newState = 'PRC' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_prc_count ELSE 0 END,

   aoc_count = aoc_count
       + CASE WHEN :newState = 'AOC' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_aoc_count ELSE 0 END,

   aps_count = aps_count
       + CASE WHEN :newState = 'APS' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_aps_count ELSE 0 END,

   ins_count = ins_count
       + CASE WHEN :newState = 'INS' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_ins_count ELSE 0 END,

   wft_count = wft_count
       + CASE WHEN :newState = 'WFT' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_wft_count ELSE 0 END,

   wfs_count = wfs_count
       + CASE WHEN :newState = 'WFS' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_wfs_count ELSE 0 END,

   tbr_count = tbr_count
       + CASE WHEN :newState = 'TBR' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_tbr_count ELSE 0 END,

   fin_count = fin_count
       + CASE WHEN :newState = 'FIN' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_fin_count ELSE 0 END,

   clo_count = clo_count
       + CASE WHEN :newState = 'CLO' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_clo_count ELSE 0 END,

   nva_count = nva_count
       + CASE WHEN :newState = 'NVA' THEN input.su_count ELSE 0 END
       - CASE WHEN NOT :isProvisional THEN input.prev_nva_count ELSE 0 END,

    npa_provisional_count =
        npa_provisional_count
        + CASE WHEN :isProvisional THEN input.cc_npa_count ELSE 0 END
        - CASE WHEN :newState = 'CLO' THEN input.was_npa_provisional_count ELSE 0 END,

    npi_provisional_count =
        npi_provisional_count
        + CASE WHEN :isProvisional THEN input.cc_npi_count ELSE 0 END
        - CASE WHEN :newState = 'CLO' THEN input.was_npi_provisional_count ELSE 0 END,

    npx_provisional_count =
        npx_provisional_count
        + CASE WHEN :isProvisional THEN input.cc_npx_count ELSE 0 END
        - CASE WHEN :newState = 'CLO' THEN input.was_npx_provisional_count ELSE 0 END,

    row_provisional_count =
        row_provisional_count
        + CASE WHEN :isProvisional THEN input.cc_row_count ELSE 0 END
        - CASE WHEN :newState = 'CLO' THEN input.was_row_provisional_count ELSE 0 END,

    npa_count = npa_count
        + CASE WHEN :newState = 'CLO' THEN input.cc_npa_count ELSE 0 END,

    npi_count = npi_count
        + CASE WHEN :newState = 'CLO' THEN input.cc_npi_count ELSE 0 END,

    npx_count = npx_count
        + CASE WHEN :newState = 'CLO' THEN input.cc_npx_count ELSE 0 END,

    row_count = row_count
        + CASE WHEN :newState = 'CLO' THEN input.cc_row_count ELSE 0 END,
        
    updated_at = :updatedAt
    
FROM (
    SELECT
        campaign_id,
        organization_unit_id,
        interviewer_id,

        COUNT(*) AS su_count,

        SUM(CASE WHEN prev_type = 'NVM' THEN 1 ELSE 0 END) AS prev_nvm_count,
        SUM(CASE WHEN prev_type = 'NNS' THEN 1 ELSE 0 END) AS prev_nns_count,
        SUM(CASE WHEN prev_type = 'ANV' THEN 1 ELSE 0 END) AS prev_anv_count,
        SUM(CASE WHEN prev_type = 'VIN' THEN 1 ELSE 0 END) AS prev_vin_count,
        SUM(CASE WHEN prev_type = 'VIC' THEN 1 ELSE 0 END) AS prev_vic_count,
        SUM(CASE WHEN prev_type = 'PRC' THEN 1 ELSE 0 END) AS prev_prc_count,
        SUM(CASE WHEN prev_type = 'AOC' THEN 1 ELSE 0 END) AS prev_aoc_count,
        SUM(CASE WHEN prev_type = 'APS' THEN 1 ELSE 0 END) AS prev_aps_count,
        SUM(CASE WHEN prev_type = 'INS' THEN 1 ELSE 0 END) AS prev_ins_count,
        SUM(CASE WHEN prev_type = 'WFT' THEN 1 ELSE 0 END) AS prev_wft_count,
        SUM(CASE WHEN prev_type = 'WFS' THEN 1 ELSE 0 END) AS prev_wfs_count,
        SUM(CASE WHEN prev_type = 'TBR' THEN 1 ELSE 0 END) AS prev_tbr_count,
        SUM(CASE WHEN prev_type = 'FIN' THEN 1 ELSE 0 END) AS prev_fin_count,
        SUM(CASE WHEN prev_type = 'CLO' THEN 1 ELSE 0 END) AS prev_clo_count,
        SUM(CASE WHEN prev_type = 'NVA' THEN 1 ELSE 0 END) AS prev_nva_count,

        SUM(was_npa_provisional) AS was_npa_provisional_count,
        SUM(was_npi_provisional) AS was_npi_provisional_count,
        SUM(was_npx_provisional) AS was_npx_provisional_count,
        SUM(was_row_provisional) AS was_row_provisional_count,

        SUM(CASE WHEN curr_cc_type = 'NPA' THEN 1 ELSE 0 END) AS cc_npa_count,
        SUM(CASE WHEN curr_cc_type = 'NPI' THEN 1 ELSE 0 END) AS cc_npi_count,
        SUM(CASE WHEN curr_cc_type = 'NPX' THEN 1 ELSE 0 END) AS cc_npx_count,
        SUM(CASE WHEN curr_cc_type = 'ROW' THEN 1 ELSE 0 END) AS cc_row_count

    FROM (
        SELECT
            su.campaign_id,
            su.organization_unit_id,
            su.interviewer_id,

            prev.type     AS prev_type,
            prev_cc.type  AS prev_cc_type,
            curr_cc.type  AS curr_cc_type,

            CASE
                WHEN prev.type IS DISTINCT FROM 'CLO'
                 AND prev_cc.type = 'NPA'
                THEN 1 ELSE 0
            END AS was_npa_provisional,

            CASE
                WHEN prev.type IS DISTINCT FROM 'CLO'
                 AND prev_cc.type = 'NPI'
                THEN 1 ELSE 0
            END AS was_npi_provisional,

            CASE
                WHEN prev.type IS DISTINCT FROM 'CLO'
                 AND prev_cc.type = 'NPX'
                THEN 1 ELSE 0
            END AS was_npx_provisional,

            CASE
                WHEN prev.type IS DISTINCT FROM 'CLO'
                 AND prev_cc.type = 'ROW'
                THEN 1 ELSE 0
            END AS was_row_provisional

        FROM survey_unit su

        LEFT JOIN LATERAL (
            SELECT s.type
            FROM state s
            WHERE s.survey_unit_id = su.id
            ORDER BY s.date DESC
            LIMIT 1 OFFSET :stateOffset
        ) prev ON TRUE

        -- Used for was_nXX_provisional: skips cause just written on fresh CLO
        LEFT JOIN LATERAL (
            SELECT c.type
            FROM closing_cause c
            WHERE c.survey_unit_id = su.id
            ORDER BY c.date DESC
            LIMIT 1 OFFSET :causeOffset
        ) prev_cc ON TRUE

        -- Used for cc count and cc provisional count: always reads current cause
        LEFT JOIN LATERAL (
            SELECT c.type
            FROM closing_cause c
            WHERE c.survey_unit_id = su.id
            ORDER BY c.date DESC
            LIMIT 1
        ) curr_cc ON TRUE

        WHERE su.id IN (:surveyUnitIds)
          AND su.interviewer_id IS NOT NULL
          AND su.organization_unit_id IS NOT NULL
          AND su.campaign_id IS NOT NULL
    ) raw_input

    GROUP BY
        campaign_id,
        organization_unit_id,
        interviewer_id
    ) input

    WHERE cds.day = :targetDay
      AND cds.campaign_id = input.campaign_id
      AND cds.organization_unit_id = input.organization_unit_id
      AND cds.interviewer_id = input.interviewer_id;
  """;
    @Override
    public void updateDailyStatsForSurveyUnits(List<String> surveyUnitIds,
                                               @Nullable StateType newState,
                                               @Nullable ClosingCauseType closingCause,
                                               Instant updatedAt
    ) {
        if (surveyUnitIds.isEmpty()) {
            return;
        }
        jdbc.sql(UPDATE_STATES_SQL)
                .param("surveyUnitIds", surveyUnitIds)
                .param("newState", newState != null ? newState.name() : null)
                .param("isProvisional", newState == null)
                .param("stateOffset", newState != null ? 1 : 0)
                .param("updatedAt", updatedAt.toEpochMilli())
                .param("causeOffset", StateType.CLO.equals(newState) && closingCause != null ? 1 : 0)
                .param("targetDay", LocalDate.now(ZoneId.of("UTC")))
                .update();
    }
}
