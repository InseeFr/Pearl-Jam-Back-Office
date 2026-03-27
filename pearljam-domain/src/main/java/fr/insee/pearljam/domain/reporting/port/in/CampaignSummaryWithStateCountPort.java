package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.model.CampaignSummaryWithStateCount;

import java.util.List;


public interface CampaignSummaryWithStateCountPort {

    List<CampaignSummaryWithStateCount> getCampaignSummaryWithStateCount(String userId);
}
