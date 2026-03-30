package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.readmodel.StateCount;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface CampaignStateCountRepositoryPort {

    
    List<StateCount> getStateCountByCampaignsAndOrganisationUnits(
            @Param("campaignIds") List<String> campaignIds,
            @Param("ouIds") List<String> ouIds,
            @Param("date") Instant date);
}
