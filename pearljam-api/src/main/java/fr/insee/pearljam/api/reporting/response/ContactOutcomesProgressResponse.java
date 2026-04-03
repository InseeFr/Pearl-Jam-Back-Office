package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.stats.AbstractDailyStats;

public record ContactOutcomesProgressResponse(
        long accepted,
        long refused,
        long unreachable,
        long outOfScope,
        long total
) {
    public static ContactOutcomesProgressResponse from(AbstractDailyStats dailyStats) {
        return new ContactOutcomesProgressResponse(
                dailyStats.getInaContactOutcomeCount(),
                dailyStats.getRefContactOutcomeCount(),
                dailyStats.getImpContactOutcomeCount(),
                dailyStats.getOutOfScopeContactOutcomes(),
                dailyStats.getTotalContactOutcomes()
        );
    }
}
