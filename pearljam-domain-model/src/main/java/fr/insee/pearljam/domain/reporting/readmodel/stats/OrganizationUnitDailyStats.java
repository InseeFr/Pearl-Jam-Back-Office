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
    private long nvmCount;
    private long nnsCount;
    private long anvCount;
    private long vinCount;
    private long vicCount;
    private long prcCount;
    private long aocCount;
    private long apsCount;
    private long insCount;
    private long wftCount;
    private long wfsCount;
    private long tbrCount;
    private long finCount;
    private long cloCount;
    private long nvaCount;
    private long unaffected;
    private long total;
    private long noticeCount;
    private long reminderCount;
}
