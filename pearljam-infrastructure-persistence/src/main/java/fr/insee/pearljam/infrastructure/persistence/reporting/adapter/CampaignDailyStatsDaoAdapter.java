package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
        %s
    FROM campaign_daily_stats cds
    JOIN campaign c ON c.id = cds.campaign_id
    WHERE cds.campaign_id = :campaignId
      AND cds.day = :day
    GROUP BY c.id, c.label
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
    SELECT
        ou.id AS ouId,
        ou.label AS ouLabel,
        %s
    FROM campaign_daily_stats cds
    JOIN organization_unit ou ON ou.id = cds.organization_unit_id
    WHERE cds.campaign_id = :campaignId
    AND cds.organization_unit_id IN (:ouIds)
    AND cds.day = :day
    GROUP BY ou.id, ou.label;
    """.formatted(DATA_SELECTION);

    @Override
    public List<OrganizationUnitDailyStats> getOrganizationUnitsStats(
            String campaignId,
            List<String> ouIds,
            LocalDate day) {
        return jdbc.sql(OUS_LIST_SQL)
                .param(CAMPAIGN_ID_PARAM, campaignId)
                .param(OU_IDS_PARAM, ouIds)
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
            %s
        FROM campaign_daily_stats cds
        JOIN campaign c ON c.id = cds.campaign_id
        LEFT JOIN su_counts su ON su.campaign_id = c.id
       WHERE cds.campaign_id IN (:campaignIds)
         AND cds.organization_unit_id IN (:ouIds)
         AND cds.day = :day
        GROUP BY c.id, c.label, su.unaffected;
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

    private static final String INTERVIEWER_SQL = """
        SELECT
            interv.id AS interviewerId,
            interv.first_name AS interviewerFirstName,
            interv.last_name AS interviewerLastName,
            %s
        FROM campaign_daily_stats cds
        JOIN interviewer interv ON interv.id = cds.interviewer_id
        WHERE cds.campaign_id = :campaignId
          AND cds.organization_unit_id IN (:ouIds)
          AND cds.day = :day
        GROUP BY interv.id, interv.first_name, interv.last_name
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
}