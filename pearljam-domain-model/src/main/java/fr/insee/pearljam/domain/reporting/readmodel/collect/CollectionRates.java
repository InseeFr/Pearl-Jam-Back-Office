package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record CollectionRates(
        float collection,
        float waste,
        float outOfScope
) {
    public static CollectionRates from(CampaignDailyStats campaignDailyStats) {
        return new CollectionRates(
                campaignDailyStats.getCollectionRate(),
                campaignDailyStats.getWasteRate(),
                campaignDailyStats.getOutOfScopeRate()
        );
    }
}
