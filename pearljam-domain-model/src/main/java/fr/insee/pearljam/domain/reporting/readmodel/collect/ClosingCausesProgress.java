package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record ClosingCausesProgress(
        long absenceInterviewer,
        long otherReasons,
        long totalClosed
) {
    public static ClosingCausesProgress from(CampaignDailyStats campaignDailyStats) {
        return new ClosingCausesProgress(
                campaignDailyStats.getNpaClosingCauseCount(),
                campaignDailyStats.getOtherReasonClosingCauses(),
                campaignDailyStats.getTotalClosingCauses()
        );
    }
}
