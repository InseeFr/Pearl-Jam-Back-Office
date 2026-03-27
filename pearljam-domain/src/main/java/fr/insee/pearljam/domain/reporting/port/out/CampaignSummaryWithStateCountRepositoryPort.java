package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount;
import fr.insee.pearljam.domain.reporting.readmodel.StateCount;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CampaignSummaryWithStateCountRepositoryPort {

    List<CampaignWithVisibility> findByUserAndManagementVisibility(List<String> organizationUnitIds, String userId, Long currentTimestamp);
}

