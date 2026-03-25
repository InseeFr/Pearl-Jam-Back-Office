package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.reporting.query.CampaignQueryResponse;
import fr.insee.pearljam.domain.reporting.query.CommunicationRequestCountQueryResponse;
import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepository;
import fr.insee.pearljam.domain.reporting.query.StateCountQueryResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CampaignProgressionDaoAdapter implements CampaignProgressionRepository {

    private final EntityManager em;
    private final CampaignRepository campaignRepository;

    private static final String PARAM_OU_IDS       = "ouIds";
    private static final String PARAM_CAMPAIGN_IDS = "campaignIds";
    private static final String PARAM_DATE         = "date";

    private static final String JPQL_STATE_COUNTS_BY_CAMPAIGN = """
        SELECT new fr.insee.pearljam.domain.reporting.query.StateCountQueryResponse(
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

    private static final String JPQL_COMM_REQUEST_COUNTS_BY_CAMPAIGN = """
            SELECT new fr.insee.pearljam.domain.reporting.query.CommunicationRequestCountQueryResponse(
                su.campaign.id,
                SUM(CASE WHEN ct.type = 'NOTICE'   THEN 1L ELSE 0L END),
                SUM(CASE WHEN ct.type = 'REMINDER' THEN 1L ELSE 0L END)
            )
            FROM CommunicationRequestDB cr
            JOIN cr.surveyUnit su
            JOIN cr.communicationTemplate ct
            WHERE su.campaign.id          IN :campaignIds
              AND su.organizationUnit.id  IN :ouIds
              AND EXISTS (
                  SELECT 1
                  FROM CommunicationRequestStatusDB crs
                  WHERE crs.communicationRequest = cr
                    AND (crs.date <= :date OR :date < 0)
              )
            GROUP BY su.campaign.id
            """;

    @Override
    public List<CampaignQueryResponse> getCampaignsByOrganisationUnits(List<String> ouIds) {
        return campaignRepository.findAllCampaignsByOuIds(ouIds);
    }

    @Override
    public List<StateCountQueryResponse> getStateCountByCampaignsAndOrganisationUnits(
            List<String> campaignIds,
            List<String> ouIds,
            Long date) {

        return em.createQuery(JPQL_STATE_COUNTS_BY_CAMPAIGN, StateCountQueryResponse.class)
                .setParameter(PARAM_CAMPAIGN_IDS, campaignIds)
                .setParameter(PARAM_OU_IDS, ouIds)
                .setParameter(PARAM_DATE, date)
                .getResultList();
    }

    @Override
    public List<CommunicationRequestCountQueryResponse> getComRequestCountsByCampaignsAndOrganisationUnits(
            List<String> campaignIds,
            List<String> ouIds,
            Long date) {

        return em.createQuery(JPQL_COMM_REQUEST_COUNTS_BY_CAMPAIGN, CommunicationRequestCountQueryResponse.class)
                .setParameter(PARAM_CAMPAIGN_IDS, campaignIds)
                .setParameter(PARAM_OU_IDS, ouIds)
                .setParameter(PARAM_DATE, date)
                .getResultList();
    }
}