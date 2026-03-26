package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount;
import fr.insee.pearljam.domain.reporting.readmodel.StateCount;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CampaignProgressionRepositoryPort {

    List<CampaignSummary> getAllManagedAndNotClosedCampaignsByOrganisationUnits(List<String> ouIds, Instant date);

    List<StateCount> getStateCountByCampaignsAndOrganisationUnits(
            @Param("campaignIds") List<String> campaignIds,
            @Param("ouIds") List<String> ouIds,
            @Param("date") Instant date);

    List<CommunicationRequestCount> getComRequestCountsByCampaignsAndOrganisationUnits(List<String> campaignIds,
                                                                                       List<String> ouIds,
                                                                                       Instant date);

}

