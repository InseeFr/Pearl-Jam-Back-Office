package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CollectionCsvRow {

    static List<Object> commonValues(AbstractDailyStats stats) {
        return List.of(
                stats.getCollectionRate(), stats.getWasteRate(), stats.getOutOfScopeRate(),
                stats.getInaContactOutcomeCount(), stats.getRefContactOutcomeCount(), stats.getImpContactOutcomeCount(),
                stats.getOutOfScopeContactOutcomes(), stats.getTotalContactOutcomes(),
                stats.getNpaClosingCauseCount(), stats.getOtherReasonClosingCauses(), stats.getTotalClosingCauses(),
                stats.getAllocatedCount()
        );
    }
}
