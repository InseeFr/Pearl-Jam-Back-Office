package fr.insee.pearljam.infrastructure.persistence.reporting.batch;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
@Transactional
public class CampaignProgressionSnapshot {

    private final EntityManager em;

    /**
     * Two CTEs computed independently (different source tables, different granularity),
     * then merged into one row per (campaign, ou, interviewer) via LEFT JOIN.
     */
    private static final String UPSERT_SNAPSHOT = """
        WITH state_counts AS (
            SELECT
                su.campaign_id,
                su.organization_unit_id,
                su.interviewer_id,
                SUM(CASE WHEN latest.type = 'NVM' THEN 1 ELSE 0 END) AS nvm_count,
                SUM(CASE WHEN latest.type = 'NNS' THEN 1 ELSE 0 END) AS nns_count,
                SUM(CASE WHEN latest.type = 'ANV' THEN 1 ELSE 0 END) AS anv_count,
                SUM(CASE WHEN latest.type = 'VIN' THEN 1 ELSE 0 END) AS vin_count,
                SUM(CASE WHEN latest.type = 'VIC' THEN 1 ELSE 0 END) AS vic_count,
                SUM(CASE WHEN latest.type = 'PRC' THEN 1 ELSE 0 END) AS prc_count,
                SUM(CASE WHEN latest.type = 'AOC' THEN 1 ELSE 0 END) AS aoc_count,
                SUM(CASE WHEN latest.type = 'APS' THEN 1 ELSE 0 END) AS aps_count,
                SUM(CASE WHEN latest.type = 'INS' THEN 1 ELSE 0 END) AS ins_count,
                SUM(CASE WHEN latest.type = 'WFT' THEN 1 ELSE 0 END) AS wft_count,
                SUM(CASE WHEN latest.type = 'WFS' THEN 1 ELSE 0 END) AS wfs_count,
                SUM(CASE WHEN latest.type = 'TBR' THEN 1 ELSE 0 END) AS tbr_count,
                SUM(CASE WHEN latest.type = 'FIN' THEN 1 ELSE 0 END) AS fin_count,
                SUM(CASE WHEN latest.type = 'CLO' THEN 1 ELSE 0 END) AS clo_count,
                SUM(CASE WHEN latest.type = 'NVA' THEN 1 ELSE 0 END) AS nva_count
            FROM survey_unit su
            JOIN LATERAL (
                SELECT s.type
                FROM state s
                WHERE s.survey_unit_id = su.id
                  AND s.date < :startOfNextDayEpoch
                ORDER BY s.date DESC
                LIMIT 1
            ) latest ON true
            WHERE su.campaign_id IS NOT NULL
              AND su.interviewer_id IS NOT NULL
              AND su.organization_unit_id IS NOT NULL
            GROUP BY su.campaign_id, su.organization_unit_id, su.interviewer_id
        ),
        comm_counts AS (
            SELECT
                su.campaign_id,
                su.organization_unit_id,
                su.interviewer_id,
                SUM(CASE WHEN ct.type = 'NOTICE'   THEN 1 ELSE 0 END) AS notice_count,
                SUM(CASE WHEN ct.type = 'REMINDER' THEN 1 ELSE 0 END) AS reminder_count
            FROM communication_request cr
            JOIN survey_unit su ON su.id = cr.survey_unit_id
            JOIN communication_template ct
                ON ct.campaign_id  = cr.campaign_id
               AND ct.meshuggah_id = cr.meshuggah_id
            WHERE EXISTS (
                SELECT 1
                FROM communication_request_status crs
                WHERE crs.communication_request_id = cr.id
                  AND crs.date < :startOfNextDayEpoch
                  AND crs.status = 'READY'
            )
            GROUP BY su.campaign_id, su.organization_unit_id, su.interviewer_id
        ),
        new_data AS (
            SELECT
                CAST(:day AS DATE) AS day,
                sc.campaign_id,
                sc.organization_unit_id,
                sc.interviewer_id,
                sc.nvm_count, sc.nns_count, sc.anv_count, sc.vin_count,
                sc.vic_count, sc.prc_count, sc.aoc_count, sc.aps_count,
                sc.ins_count, sc.wft_count, sc.wfs_count,
                sc.tbr_count, sc.fin_count, sc.clo_count, sc.nva_count,
                COALESCE(cc.notice_count, 0) AS notice_count,
                COALESCE(cc.reminder_count, 0) AS reminder_count
            FROM state_counts sc
            LEFT JOIN comm_counts cc
            USING (campaign_id, organization_unit_id, interviewer_id)
        ),
        upsert AS (
            INSERT INTO campaign_daily_stats (
                day, campaign_id, organization_unit_id, interviewer_id,
                nvm_count, nns_count, anv_count, vin_count, vic_count, prc_count,
                aoc_count, aps_count, ins_count, wft_count, wfs_count,
                tbr_count, fin_count, clo_count, nva_count,
                notice_count, reminder_count
            )
            SELECT * FROM new_data
            ON CONFLICT (day, campaign_id, organization_unit_id, interviewer_id)
            DO UPDATE SET
                nvm_count      = EXCLUDED.nvm_count,
                nns_count      = EXCLUDED.nns_count,
                anv_count      = EXCLUDED.anv_count,
                vin_count      = EXCLUDED.vin_count,
                vic_count      = EXCLUDED.vic_count,
                prc_count      = EXCLUDED.prc_count,
                aoc_count      = EXCLUDED.aoc_count,
                aps_count      = EXCLUDED.aps_count,
                ins_count      = EXCLUDED.ins_count,
                wft_count      = EXCLUDED.wft_count,
                wfs_count      = EXCLUDED.wfs_count,
                tbr_count      = EXCLUDED.tbr_count,
                fin_count      = EXCLUDED.fin_count,
                clo_count      = EXCLUDED.clo_count,
                nva_count      = EXCLUDED.nva_count,
                notice_count   = EXCLUDED.notice_count,
                reminder_count = EXCLUDED.reminder_count
            RETURNING 1
        )
        DELETE FROM campaign_daily_stats cds
        WHERE cds.day = :day
        AND NOT EXISTS (
            SELECT 1
            FROM new_data nd
            WHERE nd.day = cds.day
              AND nd.campaign_id = cds.campaign_id
              AND nd.organization_unit_id = cds.organization_unit_id
              AND nd.interviewer_id = cds.interviewer_id
        );
    """;

    public void computeAndStoreSnapshot(LocalDate day) {
        long startOfNextDayEpoch = day.plusDays(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();

        em.createNativeQuery(UPSERT_SNAPSHOT)
                .setParameter("day", day)
                .setParameter("startOfNextDayEpoch", startOfNextDayEpoch)
                .executeUpdate();
    }
}
