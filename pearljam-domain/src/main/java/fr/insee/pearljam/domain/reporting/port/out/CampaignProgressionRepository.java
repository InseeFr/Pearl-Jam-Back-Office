package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.projection.StateCountProjection;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCount;
import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignProgressionRepository {




    public List<InterviewerCount> findAllDtoByOuIds(List<String> ouIds);

    public List<String>  findAllCampaignIdsByOuIds(List<String> userOrgUnitIds);
    public List<StateCountProjection> findGroupedByCampaign(
            @Param("campaignIds") List<String> campaignIds,
            @Param("ouIds") List<String> ouIds,
            @Param("date") Long date);

    public List<CommunicationRequestCount> commRequestCountsByCampaign (List<String> campaignIds,
                                                                        List<String> ouIds,
                                                                        Long date);
}

