package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ClosingCausesProgress")
public record ClosingCausesProgressResponse(
        long absenceInterviewer,
        long otherReasons,
        long totalClosed
) {
    public static ClosingCausesProgressResponse from(AbstractDailyStats dailyStats) {
        return new ClosingCausesProgressResponse(
                dailyStats.getNpaClosingCauseCount(),
                dailyStats.getOtherReasonClosingCauses(),
                dailyStats.getTotalClosingCauses()
        );
    }
}
