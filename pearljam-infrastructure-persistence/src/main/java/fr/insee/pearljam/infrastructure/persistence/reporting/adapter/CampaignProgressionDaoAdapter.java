package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CampaignProgressionDaoAdapter implements CampaignProgressionRepositoryPort {

    private final EntityManager em;
    private final CampaignRepository campaignRepository;

    private static final String PARAM_OU_IDS = "ouIds";
    private static final String PARAM_CAMPAIGN_IDS = "campaignIds";
    private static final String PARAM_DATE = "date";


    private static final String JPQL_COMM_REQUEST_COUNTS_BY_CAMPAIGN = """
            SELECT new fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount(
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

    public List<CampaignSummary> getAllManagedAndNotClosedCampaignsByOrganisationUnits(List<String> ouIds, Instant date) {
        return campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(ouIds, date);
    }


    @Override
    public List<CommunicationRequestCount> getComRequestCountsByCampaignsAndOrganisationUnits(
            List<String> campaignIds,
            List<String> ouIds,
            Instant date) {

        return em.createQuery(JPQL_COMM_REQUEST_COUNTS_BY_CAMPAIGN, CommunicationRequestCount.class)
                .setParameter(PARAM_CAMPAIGN_IDS, campaignIds)
                .setParameter(PARAM_OU_IDS, ouIds)
                .setParameter(PARAM_DATE, date.toEpochMilli())
                .getResultList();
    }
}