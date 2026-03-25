package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.query.CampaignQueryResponse;
import fr.insee.pearljam.domain.reporting.query.CommunicationRequestCountQueryResponse;
import fr.insee.pearljam.domain.reporting.query.StateCountQueryResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignProgressionRepository {

    List<CampaignQueryResponse> getOpenedCampaignsByOrganisationUnits(List<String> ouIds, Long date);

    List<StateCountQueryResponse> getStateCountByCampaignsAndOrganisationUnits(
            @Param("campaignIds") List<String> campaignIds,
            @Param("ouIds") List<String> ouIds,
            @Param("date") Long date);

    List<CommunicationRequestCountQueryResponse> getComRequestCountsByCampaignsAndOrganisationUnits(List<String> campaignIds,
                                                                                                           List<String> ouIds,
                                                                                                           Long date);

}

