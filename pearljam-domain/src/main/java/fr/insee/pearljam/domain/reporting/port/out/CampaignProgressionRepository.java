package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.projection.CampaignProjection;
import fr.insee.pearljam.domain.reporting.projection.CommunicationRequestCountProjection;
import fr.insee.pearljam.domain.reporting.projection.StateCountProjection;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignProgressionRepository {

    public List<CampaignProjection> findAllDtoByOuIds(List<String> ouIds);

    public List<String>  findAllCampaignIdsByOuIds(List<String> userOrgUnitIds);

    public List<StateCountProjection> findGroupedByCampaign(
            @Param("campaignIds") List<String> campaignIds,
            @Param("ouIds") List<String> ouIds,
            @Param("date") Long date);

    public List<CommunicationRequestCountProjection> commRequestCountsByCampaign (List<String> campaignIds,
                                                                                  List<String> ouIds,
                                                                                  Long date);
}

