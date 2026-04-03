package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.collect.ClosingCausesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.collect.CollectionRates;
import fr.insee.pearljam.domain.reporting.readmodel.collect.ContactOutcomesProgress;

public record CampaignCollectionResponse(
        String campaignId,
        String campaignLabel,
        long allocated,
        CollectionRates rates,
        ContactOutcomesProgress outcomes,
        ClosingCausesProgress closingCauses
) {
}
