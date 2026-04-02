package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.AbstractDailyStats;

public record ClosingCausesProgress(
        long absenceInterviewer,
        long otherReasons,
        long totalClosed
) {
    public static ClosingCausesProgress from(AbstractDailyStats dailyStats) {
        return new ClosingCausesProgress(
                dailyStats.getNpaClosingCauseCount(),
                dailyStats.getOtherReasonClosingCauses(),
                dailyStats.getTotalClosingCauses()
        );
    }
}
