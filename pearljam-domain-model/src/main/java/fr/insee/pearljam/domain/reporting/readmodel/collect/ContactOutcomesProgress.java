package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record ContactOutcomesProgress(
        long accepted,
        long refused,
        long unreachable,
        long outOfScope,
        long total
) {
    public static ContactOutcomesProgress from(CampaignDailyStats campaignDailyStats) {
        return new ContactOutcomesProgress(
                campaignDailyStats.getInaContactOutcomeCount(),
                campaignDailyStats.getRefContactOutcomeCount(),
                campaignDailyStats.getImpContactOutcomeCount(),
                campaignDailyStats.getOutOfScopeContactOutcomes(),
                campaignDailyStats.getTotalContactOutcomes()
        );
    }
}
