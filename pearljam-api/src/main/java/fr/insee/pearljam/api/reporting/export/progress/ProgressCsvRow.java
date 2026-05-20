package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ProgressCsvRow {

    static final String TOTAL_FRANCE = "Total France";
    static final String TOTAL_SITE = "Total Site";
    static final String TOTAL_UNAFFECTED = "UE non affectées";

        private static final int COMMON_VALUES_SIZE = 13;

        static List<Object> commonValues(AbstractDailyStats stats) {

        return List.of(
                stats.getProgressStateRate(),
                stats.getAllocatedCount(), stats.getVicStateCount(), stats.getInProgressStateCount(),
                stats.getWftStateCount(), stats.getTbrStateCount(), stats.getCompletedStateCount(),
                stats.getPrcStateCount(), stats.getAocStateCount(), stats.getApsStateCount(),
                stats.getInsStateCount(),
                stats.getNoticeCommunicationCount(), stats.getReminderCommunicationCount()
            );
        }

        public static int commonValuesSize() {
            return COMMON_VALUES_SIZE;
        }
}
