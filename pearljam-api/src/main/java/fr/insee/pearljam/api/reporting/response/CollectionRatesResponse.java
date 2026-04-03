package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CollectionRates")
public record CollectionRatesResponse(
        float collection,
        float waste,
        float outOfScope
) {
    public static CollectionRatesResponse from(AbstractDailyStats dailyStats) {
        return new CollectionRatesResponse(
                dailyStats.getCollectionRate(),
                dailyStats.getWasteRate(),
                dailyStats.getOutOfScopeRate()
        );
    }
}
