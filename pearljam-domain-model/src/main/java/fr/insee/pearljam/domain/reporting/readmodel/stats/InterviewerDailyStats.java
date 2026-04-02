package fr.insee.pearljam.domain.reporting.readmodel.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InterviewerDailyStats {
    private String interviewerId;
    private String interviewerFirstName;
    private String interviewerLastName;

    // =========================
    // STATES
    // =========================
    // =========================
    // STATES
    // =========================
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

    private long unaffectedCount;


    // =========================
    // COMMUNICATION
    // =========================
    private long noticeCommunicationCount;
    private long reminderCommunicationCount;

    // =========================
    // CLOSING CAUSE
    // =========================
    private long npaClosingCauseCount;
    private long npiClosingCauseCount;
    private long npxClosingCauseCount;
    private long rowClosingCauseCount;

    // =========================
    // CONTACT OUTCOME
    // =========================
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
        long allocated = getAllocatedStateCount();
        if (allocated == 0) {
            return 0f;
        }
        return (float) (tbrStateCount + finStateCount + cloStateCount) * 100 / allocated;
    }

    public long getCompletedStateCount() {
        return finStateCount + cloStateCount;
    }

    public long getInProgressStateCount() {
        return prcStateCount + aocStateCount + apsStateCount + insStateCount;
    }

    public long getAllocatedStateCount() {
        return nnsStateCount + anvStateCount + vinStateCount + vicStateCount +
                prcStateCount + aocStateCount + apsStateCount + insStateCount + wftStateCount +
                wfsStateCount + tbrStateCount + finStateCount + cloStateCount;
    }
}
