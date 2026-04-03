package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.AbstractDailyStats;

public record ContactOutcomesProgress(
        long accepted,
        long refused,
        long unreachable,
        long outOfScope,
        long total
) {
    public static ContactOutcomesProgress from(AbstractDailyStats dailyStats) {
        return new ContactOutcomesProgress(
                dailyStats.getInaContactOutcomeCount(),
                dailyStats.getRefContactOutcomeCount(),
                dailyStats.getImpContactOutcomeCount(),
                dailyStats.getOutOfScopeContactOutcomes(),
                dailyStats.getTotalContactOutcomes()
        );
    }
}
