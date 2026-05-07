package fr.insee.pearljam.domain.reporting.readmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractDailyStats {

    private long nvmStateCount;
    private long nnsStateCount;
    private long anvStateCount;
    private long vinStateCount;
    private long vicStateCount;
    private long prcStateCount;
    private long aocStateCount;
    private long apsStateCount;
    private long insStateCount;
    private long wftStateCount;
    private long wfsStateCount;
    private long tbrStateCount;
    private long finStateCount;
    private long cloStateCount;
    private long nvaStateCount;

    private long noticeCommunicationCount;
    private long reminderCommunicationCount;

    private long npaClosingCauseCount;
    private long npiClosingCauseCount;
    private long npxClosingCauseCount;
    private long rowClosingCauseCount;

    private long inaContactOutcomeCount;
    private long refContactOutcomeCount;
    private long impContactOutcomeCount;
    private long ucdContactOutcomeCount;
    private long utrContactOutcomeCount;
    private long alaContactOutcomeCount;
    private long dukContactOutcomeCount;
    private long nuhContactOutcomeCount;
    private long noaContactOutcomeCount;

    public float getProgressStateRate() {
        long allocated = getAllocatedCount();
        if (allocated == 0) {
            return 0f;
        }
        return (float) (tbrStateCount + finStateCount + cloStateCount) * 100 / allocated;
    }

    public long getToProcessInterviewerStateCount() {
        return vicStateCount + prcStateCount + aocStateCount + apsStateCount + insStateCount + wftStateCount;
    }

    public long getCompletedStateCount() {
        return finStateCount + cloStateCount;
    }

    public long getInProgressStateCount() {
        return prcStateCount + aocStateCount + apsStateCount + insStateCount;
    }

    public abstract long getAllocatedCount();

    protected long getAllocatedFromStateCounts() {
        return nnsStateCount + anvStateCount + vinStateCount + vicStateCount
                + prcStateCount + aocStateCount + apsStateCount + insStateCount + wftStateCount
                + wfsStateCount + tbrStateCount + finStateCount + cloStateCount;
    }

    public long getOutOfScopeContactOutcomes() {
        return ucdContactOutcomeCount + utrContactOutcomeCount + alaContactOutcomeCount
                + nuhContactOutcomeCount + dukContactOutcomeCount + noaContactOutcomeCount;
    }

    public float getCollectionRate() {
        long allocated = getAllocatedCount();
        if (allocated == 0) {
            return 0f;
        }
        return (float) inaContactOutcomeCount * 100 / allocated;
    }

    public float getWasteRate() {
        long allocated = getAllocatedCount();
        if (allocated == 0) {
            return 0f;
        }
        return (float) (refContactOutcomeCount + impContactOutcomeCount + npaClosingCauseCount) * 100 / allocated;
    }

    public float getOutOfScopeRate() {
        long allocated = getAllocatedCount();
        if (allocated == 0) {
            return 0f;
        }
        return (float) (getOutOfScopeContactOutcomes() + npxClosingCauseCount + rowClosingCauseCount) * 100 / allocated;
    }

    public long getTotalContactOutcomes() {
        return inaContactOutcomeCount + impContactOutcomeCount + refContactOutcomeCount + getOutOfScopeContactOutcomes();
    }

    public long getOtherReasonClosingCauses() {
        return npiClosingCauseCount + npxClosingCauseCount + rowClosingCauseCount;
    }

    public long getTotalClosingCauses() {
        return npaClosingCauseCount + getOtherReasonClosingCauses();
    }
}
