package fr.insee.pearljam.infrastructure.persistence.reporting.batch;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
@Transactional
public class CampaignProgressSnapshot {

    private final DateService dateService;

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
                COUNT(DISTINCT CASE WHEN ct.type = 'NOTICE'   THEN su.id END) AS notice_count,
                COUNT(DISTINCT CASE WHEN ct.type = 'REMINDER' THEN su.id END) AS reminder_count
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
        closing_counts AS (
            SELECT
                su.campaign_id,
                su.organization_unit_id,
                su.interviewer_id,
                SUM(CASE WHEN cc.type = 'NPA' AND latest_state.type != 'CLO' THEN 1 ELSE 0 END) AS npa_provisional_count,
                SUM(CASE WHEN cc.type = 'NPI' AND latest_state.type != 'CLO' THEN 1 ELSE 0 END) AS npi_provisional_count,
                SUM(CASE WHEN cc.type = 'NPX' AND latest_state.type != 'CLO' THEN 1 ELSE 0 END) AS npx_provisional_count,
                SUM(CASE WHEN cc.type = 'ROW' AND latest_state.type != 'CLO' THEN 1 ELSE 0 END) AS row_provisional_count,
                SUM(CASE WHEN cc.type = 'NPA' AND latest_state.type = 'CLO' THEN 1 ELSE 0 END) AS npa_count,
                SUM(CASE WHEN cc.type = 'NPI' AND latest_state.type = 'CLO' THEN 1 ELSE 0 END) AS npi_count,
                SUM(CASE WHEN cc.type = 'NPX' AND latest_state.type = 'CLO' THEN 1 ELSE 0 END) AS npx_count,
                SUM(CASE WHEN cc.type = 'ROW' AND latest_state.type = 'CLO' THEN 1 ELSE 0 END) AS row_count
            FROM survey_unit su
            JOIN LATERAL (
                SELECT c.type
                FROM closing_cause c
                WHERE c.survey_unit_id = su.id
                  AND c.date < :startOfNextDayEpoch
                ORDER BY c.date DESC
                LIMIT 1
            ) cc ON true
            LEFT JOIN LATERAL (
                SELECT s.type
                FROM state s
                WHERE s.survey_unit_id = su.id
                  AND s.date < :startOfNextDayEpoch
                ORDER BY s.date DESC
                LIMIT 1
            ) latest_state ON true
            WHERE su.campaign_id IS NOT NULL
              AND su.interviewer_id IS NOT NULL
              AND su.organization_unit_id IS NOT NULL
            GROUP BY su.campaign_id, su.organization_unit_id, su.interviewer_id
        ),
        contact_outcome_counts AS (
            SELECT
                su.campaign_id,
                su.organization_unit_id,
                su.interviewer_id,
                SUM(CASE WHEN co.type = 'INA' THEN 1 ELSE 0 END) AS ina_count,
                SUM(CASE WHEN co.type = 'REF' THEN 1 ELSE 0 END) AS ref_count,
                SUM(CASE WHEN co.type = 'IMP' THEN 1 ELSE 0 END) AS imp_count,
                SUM(CASE WHEN co.type = 'UCD' THEN 1 ELSE 0 END) AS ucd_count,
                SUM(CASE WHEN co.type = 'UTR' THEN 1 ELSE 0 END) AS utr_count,
                SUM(CASE WHEN co.type = 'ALA' THEN 1 ELSE 0 END) AS ala_count,
                SUM(CASE WHEN co.type = 'DUK' THEN 1 ELSE 0 END) AS duk_count,
                SUM(CASE WHEN co.type = 'NUH' THEN 1 ELSE 0 END) AS nuh_count,
                SUM(CASE WHEN co.type = 'NOA' THEN 1 ELSE 0 END) AS noa_count
            FROM survey_unit su
            JOIN contact_outcome co
                ON co.survey_unit_id = su.id
               AND co.date < :startOfNextDayEpoch
            JOIN LATERAL (
                SELECT s.type
                FROM state s
                WHERE s.survey_unit_id = su.id
                  AND s.date < :startOfNextDayEpoch
                ORDER BY s.date DESC
                LIMIT 1
            ) latest_state
                -- only retrieve contact outcomes for finalized survey units
                ON latest_state.type IN ('TBR', 'FIN', 'CLO')
            WHERE su.campaign_id IS NOT NULL
              AND su.interviewer_id IS NOT NULL
              AND su.organization_unit_id IS NOT NULL
            GROUP BY su.campaign_id, su.organization_unit_id, su.interviewer_id
        ),
        new_data AS (
            SELECT
                CAST(:day AS DATE) AS day,
                sc.campaign_id,
                sc.organization_unit_id,
                sc.interviewer_id,
                :updatedAt AS updatedAt,
                sc.nvm_count, sc.nns_count, sc.anv_count, sc.vin_count,
                sc.vic_count, sc.prc_count, sc.aoc_count, sc.aps_count,
                sc.ins_count, sc.wft_count, sc.wfs_count,
                sc.tbr_count, sc.fin_count, sc.clo_count, sc.nva_count,
                COALESCE(cc.notice_count, 0) AS notice_count,
                COALESCE(cc.reminder_count, 0) AS reminder_count,
                COALESCE(cl.npa_provisional_count, 0) AS npa_provisional_count,
                COALESCE(cl.npi_provisional_count, 0) AS npi_provisional_count,
                COALESCE(cl.npx_provisional_count, 0) AS npx_provisional_count,
                COALESCE(cl.row_provisional_count, 0) AS row_provisional_count,
                COALESCE(cl.npa_count, 0) AS npa_count,
                COALESCE(cl.npi_count, 0) AS npi_count,
                COALESCE(cl.npx_count, 0) AS npx_count,
                COALESCE(cl.row_count, 0) AS row_count,
                COALESCE(co.ina_count, 0) AS ina_count,
                COALESCE(co.ref_count, 0) AS ref_count,
                COALESCE(co.imp_count, 0) AS imp_count,
                COALESCE(co.ucd_count, 0) AS ucd_count,
                COALESCE(co.utr_count, 0) AS utr_count,
                COALESCE(co.ala_count, 0) AS ala_count,
                COALESCE(co.duk_count, 0) AS duk_count,
                COALESCE(co.nuh_count, 0) AS nuh_count,
                COALESCE(co.noa_count, 0) AS noa_count
            FROM state_counts sc
            LEFT JOIN comm_counts cc USING (campaign_id, organization_unit_id, interviewer_id)
            LEFT JOIN closing_counts cl USING (campaign_id, organization_unit_id, interviewer_id)
            LEFT JOIN contact_outcome_counts co USING (campaign_id, organization_unit_id, interviewer_id)
        ),
        upsert AS (
            INSERT INTO campaign_daily_stats (
                day, campaign_id, organization_unit_id, interviewer_id,updated_at,
                nvm_count, nns_count, anv_count, vin_count, vic_count, prc_count,
                aoc_count, aps_count, ins_count, wft_count, wfs_count,
                tbr_count, fin_count, clo_count, nva_count,
                notice_count, reminder_count,
                npa_provisional_count, npi_provisional_count, npx_provisional_count, row_provisional_count,
                npa_count, npi_count, npx_count, row_count,
                ina_count, ref_count, imp_count, ucd_count, utr_count,
                ala_count, duk_count, nuh_count, noa_count
            )
            SELECT * FROM new_data
            ON CONFLICT (day, campaign_id, organization_unit_id, interviewer_id)
            DO UPDATE SET
                nvm_count = EXCLUDED.nvm_count,
                nns_count = EXCLUDED.nns_count,
                anv_count = EXCLUDED.anv_count,
                vin_count = EXCLUDED.vin_count,
                vic_count = EXCLUDED.vic_count,
                prc_count = EXCLUDED.prc_count,
                aoc_count = EXCLUDED.aoc_count,
                aps_count = EXCLUDED.aps_count,
                ins_count = EXCLUDED.ins_count,
                wft_count = EXCLUDED.wft_count,
                wfs_count = EXCLUDED.wfs_count,
                tbr_count = EXCLUDED.tbr_count,
                fin_count = EXCLUDED.fin_count,
                clo_count = EXCLUDED.clo_count,
                nva_count = EXCLUDED.nva_count,
                notice_count = EXCLUDED.notice_count,
                reminder_count = EXCLUDED.reminder_count,
                npa_provisional_count = EXCLUDED.npa_provisional_count,
                npi_provisional_count = EXCLUDED.npi_provisional_count,
                npx_provisional_count = EXCLUDED.npx_provisional_count,
                row_provisional_count = EXCLUDED.row_provisional_count,
                npa_count = EXCLUDED.npa_count,
                npi_count = EXCLUDED.npi_count,
                npx_count = EXCLUDED.npx_count,
                row_count = EXCLUDED.row_count,
                ina_count = EXCLUDED.ina_count,
                ref_count = EXCLUDED.ref_count,
                imp_count = EXCLUDED.imp_count,
                ucd_count = EXCLUDED.ucd_count,
                utr_count = EXCLUDED.utr_count,
                ala_count = EXCLUDED.ala_count,
                duk_count = EXCLUDED.duk_count,
                nuh_count = EXCLUDED.nuh_count,
                noa_count = EXCLUDED.noa_count,
               updated_at = EXCLUDED.updated_at
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
        
        Instant updatedAt = dateService.now();

        em.createNativeQuery(UPSERT_SNAPSHOT)
                .setParameter("day", day)
                .setParameter("startOfNextDayEpoch", startOfNextDayEpoch)
                .setParameter("updatedAt", updatedAt)
                .executeUpdate();
    }
}