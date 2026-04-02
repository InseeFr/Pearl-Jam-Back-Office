package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record CampaignCollection(String campaignId,
                                 String campaignLabel,
                                 long allocated,
                                 CollectionRates rates,
                                 ContactOutcomesProgress outcomes,
                                 ClosingCausesProgress closingCauses) {
    public static CampaignCollection from(String id, String label, CampaignDailyStats campaignDailyStats) {
        return new CampaignCollection(id,
                label,
                campaignDailyStats.getAllocatedStateCount(),
                CollectionRates.from(campaignDailyStats),
                ContactOutcomesProgress.from(campaignDailyStats),
                ClosingCausesProgress.from(campaignDailyStats));
    }
}
