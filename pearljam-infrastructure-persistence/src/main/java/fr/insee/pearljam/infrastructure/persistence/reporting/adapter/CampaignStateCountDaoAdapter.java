package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.reporting.port.out.CampaignStateCountRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.StateCount;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CampaignStateCountDaoAdapter implements CampaignStateCountRepositoryPort {

    private final EntityManager em;
    private static final String PARAM_OU_IDS = "ouIds";
    private static final String PARAM_CAMPAIGN_IDS = "campaignIds";
    private static final String PARAM_DATE = "date";


    private static final String JPQL_STATE_COUNTS_BY_CAMPAIGN = """
            SELECT new fr.insee.pearljam.domain.reporting.readmodel.StateCount(
                su.campaign.id,
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.NVM THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.NNS THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.ANV THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.VIN THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.VIC THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.PRC THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.AOC THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.APS THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.INS THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.WFT THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.WFS THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.TBR THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.FIN THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.CLO THEN 1L ELSE 0L END),
                SUM(CASE WHEN s.type = fr.insee.pearljam.domain.surveyunit.model.StateType.NVA THEN 1L ELSE 0L END),
                COUNT(su)
            )
            FROM SurveyUnitDB su
            JOIN su.states s
            WHERE su.campaign.id IN :campaignIds
            AND su.organizationUnit.id IN :ouIds
            AND s.date = (
                SELECT MAX(s2.date)
                FROM StateDB s2
                WHERE s2.surveyUnit = su
                AND (:date < 0 OR s2.date <= :date)
            )
            GROUP BY su.campaign.id
            """;

    @Override
    public List<StateCount> getStateCountByCampaignsAndOrganisationUnits(
            List<String> campaignIds,
            List<String> ouIds,
            Instant date) {

        return em.createQuery(JPQL_STATE_COUNTS_BY_CAMPAIGN, StateCount.class)
                .setParameter(PARAM_CAMPAIGN_IDS, campaignIds)
                .setParameter(PARAM_OU_IDS, ouIds)
                .setParameter(PARAM_DATE, date.toEpochMilli())
                .getResultList();
    }
}
