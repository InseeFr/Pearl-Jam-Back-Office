package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CollectionCsvRow {

    static final String TOTAL_FRANCE = "Total France";
    static final String TOTAL_SITE = "Total Site";
    static final String TOTAL_UNAFFECTED = "TUE non affectées";


    private static final int COMMON_VALUES_SIZE = 12;

    static List<Object> commonValues(AbstractDailyStats stats) {
        return List.of(
                stats.getCollectionRate(), stats.getWasteRate(), stats.getOutOfScopeRate(),
                stats.getInaContactOutcomeCount(), stats.getRefContactOutcomeCount(), stats.getImpContactOutcomeCount(),
                stats.getOutOfScopeContactOutcomes(), stats.getTotalContactOutcomes(),
                stats.getNpaClosingCauseCount(), stats.getOtherReasonClosingCauses(), stats.getTotalClosingCauses(),
                stats.getAllocatedCount()
        );
    }

    static int commonValuesSize() {
        return COMMON_VALUES_SIZE;
    }

    static long getTotalFrance(AbstractDailyStats stats)
    {
        return stats.getAllocatedCount();
    }

    static long getTotalSite(AbstractDailyStats stats)
    {
        return stats.getAllocatedCount();
    }

    static long getTotalUnaffacted(AbstractDailyStats stats)
    {
        return stats.getAllocatedCount();
    }
}
