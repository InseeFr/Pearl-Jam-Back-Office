package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.AbstractDailyStats;

public record CampaignCollection(String campaignId,
                                 String campaignLabel,
                                 long allocated,
                                 CollectionRates rates,
                                 ContactOutcomesProgress outcomes,
                                 ClosingCausesProgress closingCauses) {
    public static CampaignCollection from(String id, String label, AbstractDailyStats dailyStats) {
        return new CampaignCollection(id,
                label,
                dailyStats.getAllocatedStateCount(),
                CollectionRates.from(dailyStats),
                ContactOutcomesProgress.from(dailyStats),
                ClosingCausesProgress.from(dailyStats));
    }
}
