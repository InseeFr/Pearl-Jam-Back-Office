package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CampaignProgressionRepositoryPort {

    List<CampaignSummary> getAllManagedAndNotClosedCampaignsByOrganisationUnits(List<String> ouIds, Instant date);
    
    List<CommunicationRequestCount> getComRequestCountsByCampaignsAndOrganisationUnits(List<String> campaignIds,
                                                                                       List<String> ouIds,
                                                                                       Instant date);

}

