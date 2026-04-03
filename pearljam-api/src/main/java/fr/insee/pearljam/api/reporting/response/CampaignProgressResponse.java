package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.progress.CommunicationsProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesProgress;

public record CampaignProgressResponse(
        String campaignId,
        String campaignLabel,
        float progressRate,
        StatesProgress states,
        CommunicationsProgress communications
) {
}
