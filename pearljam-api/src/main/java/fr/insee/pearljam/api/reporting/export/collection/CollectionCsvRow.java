package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
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

    static List<Object> commonValues(CollectionRatesResponse rates,
                                     ContactOutcomesProgressResponse outcomes,
                                     ClosingCausesProgressResponse closingCauses,
                                     long allocated) {
        return List.of(
                rates.collection(), rates.waste(), rates.outOfScope(),
                outcomes.accepted(), outcomes.refused(), outcomes.unreachable(),
                outcomes.outOfScope(), outcomes.total(),
                closingCauses.absenceInterviewer(), closingCauses.otherReasons(), closingCauses.totalClosed(),
                allocated
        );
    }
}
