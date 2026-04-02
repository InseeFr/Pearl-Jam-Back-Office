package fr.insee.pearljam.domain.reporting.readmodel.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrganizationUnitDailyStats {
    private String ouId;
    private String ouLabel;

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
}
