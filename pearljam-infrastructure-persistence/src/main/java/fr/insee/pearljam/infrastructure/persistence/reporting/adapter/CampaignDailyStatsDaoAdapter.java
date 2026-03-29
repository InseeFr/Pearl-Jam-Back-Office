package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.OrganizationUnitDailyStats;
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

    private static final String CAMPAIGN_SQL = """
    SELECT
        c.id AS campaignId,
        c.label AS campaignLabel,
        COALESCE(SUM(cds.nvm_count),0) AS nvmCount,
        COALESCE(SUM(cds.nns_count),0) AS nnsCount,
        COALESCE(SUM(cds.anv_count),0) AS anvCount,
        COALESCE(SUM(cds.vin_count),0) AS vinCount,
        COALESCE(SUM(cds.vic_count),0) AS vicCount,
        COALESCE(SUM(cds.prc_count),0) AS prcCount,
        COALESCE(SUM(cds.aoc_count),0) AS aocCount,
        COALESCE(SUM(cds.aps_count),0) AS apsCount,
        COALESCE(SUM(cds.ins_count),0) AS insCount,
        COALESCE(SUM(cds.wft_count),0) AS wftCount,
        COALESCE(SUM(cds.wfs_count),0) AS wfsCount,
        COALESCE(SUM(cds.tbr_count),0) AS tbrCount,
        COALESCE(SUM(cds.fin_count),0) AS finCount,
        COALESCE(SUM(cds.clo_count),0) AS cloCount,
        COALESCE(SUM(cds.nva_count),0) AS nvaCount,
            COALESCE(
                SUM(cds.nvm_count)
                + SUM(cds.nns_count)
                + SUM(cds.anv_count)
                + SUM(cds.vin_count)
                + SUM(cds.vic_count)
                + SUM(cds.prc_count)
                + SUM(cds.aoc_count)
                + SUM(cds.aps_count)
                + SUM(cds.ins_count)
                + SUM(cds.wft_count)
                + SUM(cds.wfs_count)
                + SUM(cds.tbr_count)
                + SUM(cds.fin_count)
                + SUM(cds.clo_count)
                , 0) AS total,
        (
            SELECT count(id) FROM survey_unit
            WHERE campaign_id = :campaignId
            AND interviewer_id is NULL
        ) AS unaffected,
        COALESCE(SUM(cds.notice_count),0) AS noticeCount,
        COALESCE(SUM(cds.reminder_count),0) AS reminderCount
    FROM campaign_daily_stats cds
    JOIN campaign c ON c.id = cds.campaign_id
    WHERE cds.campaign_id = :campaignId
      AND cds.day = :day
    GROUP BY c.id, c.label
    """;

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
            COALESCE(SUM(nvm_count),0) AS nvmCount,
            COALESCE(SUM(nns_count),0) AS nnsCount,
            COALESCE(SUM(anv_count),0) AS anvCount,
            COALESCE(SUM(vin_count),0) AS vinCount,
            COALESCE(SUM(vic_count),0) AS vicCount,
            COALESCE(SUM(prc_count),0) AS prcCount,
            COALESCE(SUM(aoc_count),0) AS aocCount,
            COALESCE(SUM(aps_count),0) AS apsCount,
            COALESCE(SUM(ins_count),0) AS insCount,
            COALESCE(SUM(wft_count),0) AS wftCount,
            COALESCE(SUM(wfs_count),0) AS wfsCount,
            COALESCE(SUM(tbr_count),0) AS tbrCount,
            COALESCE(SUM(fin_count),0) AS finCount,
            COALESCE(SUM(clo_count),0) AS cloCount,
            COALESCE(SUM(nva_count),0) AS nvaCount,
            COALESCE(
                SUM(cds.nvm_count)
                + SUM(cds.nns_count)
                + SUM(cds.anv_count)
                + SUM(cds.vin_count)
                + SUM(cds.vic_count)
                + SUM(cds.prc_count)
                + SUM(cds.aoc_count)
                + SUM(cds.aps_count)
                + SUM(cds.ins_count)
                + SUM(cds.wft_count)
                + SUM(cds.wfs_count)
                + SUM(cds.tbr_count)
                + SUM(cds.fin_count)
                + SUM(cds.clo_count)
                , 0) AS total,
            COALESCE(SUM(notice_count),0) AS noticeCount,
            COALESCE(SUM(reminder_count),0) AS reminderCount
        FROM campaign_daily_stats cds
        WHERE campaign_id = :campaignId
          AND organization_unit_id IN (:ouIds)
          AND day = :day
    """;

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
        COALESCE(SUM(nvm_count),0) AS nvmCount,
        COALESCE(SUM(nns_count),0) AS nnsCount,
        COALESCE(SUM(anv_count),0) AS anvCount,
        COALESCE(SUM(vin_count),0) AS vinCount,
        COALESCE(SUM(vic_count),0) AS vicCount,
        COALESCE(SUM(prc_count),0) AS prcCount,
        COALESCE(SUM(aoc_count),0) AS aocCount,
        COALESCE(SUM(aps_count),0) AS apsCount,
        COALESCE(SUM(ins_count),0) AS insCount,
        COALESCE(SUM(wft_count),0) AS wftCount,
        COALESCE(SUM(wfs_count),0) AS wfsCount,
        COALESCE(SUM(tbr_count),0) AS tbrCount,
        COALESCE(SUM(fin_count),0) AS finCount,
        COALESCE(SUM(clo_count),0) AS cloCount,
        COALESCE(SUM(nva_count),0) AS nvaCount,
        COALESCE(
            SUM(cds.nns_count + cds.anv_count + cds.vin_count + cds.vic_count
                  + cds.prc_count + cds.aoc_count + cds.aps_count + cds.ins_count + cds.wft_count
                  + cds.wfs_count + cds.tbr_count + cds.fin_count + cds.clo_count), 0) AS total,
        COALESCE(SUM(notice_count),0) AS noticeCount,
        COALESCE(SUM(reminder_count),0) AS reminderCount
    FROM campaign_daily_stats cds
    JOIN organization_unit ou ON ou.id = cds.organization_unit_id
    WHERE cds.campaign_id = :campaignId
    AND cds.organization_unit_id IN (:ouIds)
    AND cds.day = :day
    GROUP BY ou.id, ou.label;
    """;
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
            COALESCE(SUM(cds.nvm_count),0) AS nvmCount,
            COALESCE(SUM(cds.nns_count),0) AS nnsCount,
            COALESCE(SUM(cds.anv_count),0) AS anvCount,
            COALESCE(SUM(cds.vin_count),0) AS vinCount,
            COALESCE(SUM(cds.vic_count),0) AS vicCount,
            COALESCE(SUM(cds.prc_count),0) AS prcCount,
            COALESCE(SUM(cds.aoc_count),0) AS aocCount,
            COALESCE(SUM(cds.aps_count),0) AS apsCount,
            COALESCE(SUM(cds.ins_count),0) AS insCount,
            COALESCE(SUM(cds.wft_count),0) AS wftCount,
            COALESCE(SUM(cds.wfs_count),0) AS wfsCount,
            COALESCE(SUM(cds.tbr_count),0) AS tbrCount,
            COALESCE(SUM(cds.fin_count),0) AS finCount,
            COALESCE(SUM(cds.clo_count),0) AS cloCount,
            COALESCE(
                SUM(cds.nns_count + cds.anv_count + cds.vin_count + cds.vic_count
                  + cds.prc_count + cds.aoc_count + cds.aps_count + cds.ins_count + cds.wft_count
                  + cds.wfs_count + cds.tbr_count + cds.fin_count + cds.clo_count), 0) AS total,
            COALESCE(su.unaffected, 0) AS unaffected,
            COALESCE(SUM(cds.notice_count),0) AS noticeCount,
            COALESCE(SUM(cds.reminder_count),0) AS reminderCount
        FROM campaign_daily_stats cds
        JOIN campaign c ON c.id = cds.campaign_id
        LEFT JOIN su_counts su ON su.campaign_id = c.id
       WHERE cds.campaign_id IN (:campaignIds)
         AND cds.organization_unit_id IN (:ouIds)
         AND cds.day = :day
        GROUP BY c.id, c.label, su.unaffected;
    """;

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
            COALESCE(SUM(nvm_count),0) AS nvmCount,
            COALESCE(SUM(nns_count),0) AS nnsCount,
            COALESCE(SUM(anv_count),0) AS anvCount,
            COALESCE(SUM(vin_count),0) AS vinCount,
            COALESCE(SUM(vic_count),0) AS vicCount,
            COALESCE(SUM(prc_count),0) AS prcCount,
            COALESCE(SUM(aoc_count),0) AS aocCount,
            COALESCE(SUM(aps_count),0) AS apsCount,
            COALESCE(SUM(ins_count),0) AS insCount,
            COALESCE(SUM(wft_count),0) AS wftCount,
            COALESCE(SUM(wfs_count),0) AS wfsCount,
            COALESCE(SUM(tbr_count),0) AS tbrCount,
            COALESCE(SUM(fin_count),0) AS finCount,
            COALESCE(SUM(clo_count),0) AS cloCount,
            COALESCE(SUM(nva_count),0) AS nvaCount,
            COALESCE(SUM(cds.nns_count + cds.anv_count + cds.vin_count + cds.vic_count
                  + cds.prc_count + cds.aoc_count + cds.aps_count + cds.ins_count + cds.wft_count
                  + cds.wfs_count + cds.tbr_count + cds.fin_count + cds.clo_count), 0) as total,
            COALESCE(SUM(notice_count),0) AS noticeCount,
            COALESCE(SUM(reminder_count),0) AS reminderCount
        FROM campaign_daily_stats cds
        JOIN interviewer interv ON interv.id = cds.interviewer_id
        WHERE cds.campaign_id = :campaignId
          AND cds.organization_unit_id IN (:ouIds)
          AND cds.day = :day
        GROUP BY interv.id, interv.first_name, interv.last_name
    """;

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