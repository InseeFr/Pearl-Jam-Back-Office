package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignSummaryWithStateCountRepositoryPort {

    List<CampaignWithVisibility> findByUserAndManagementVisibility(List<String> organizationUnitIds, String userId, Long currentTimestamp);
}

