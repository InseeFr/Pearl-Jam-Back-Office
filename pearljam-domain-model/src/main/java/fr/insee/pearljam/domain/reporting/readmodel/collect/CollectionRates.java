package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.AbstractDailyStats;

public record CollectionRates(
        float collection,
        float waste,
        float outOfScope
) {
    public static CollectionRates from(AbstractDailyStats dailyStats) {
        return new CollectionRates(
                dailyStats.getCollectionRate(),
                dailyStats.getWasteRate(),
                dailyStats.getOutOfScopeRate()
        );
    }
}
