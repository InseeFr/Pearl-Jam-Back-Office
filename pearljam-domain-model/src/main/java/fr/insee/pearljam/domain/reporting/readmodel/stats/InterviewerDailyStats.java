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
    private Long nvmCount;
    private Long nnsCount;
    private Long anvCount;
    private Long vinCount;
    private Long vicCount;
    private Long prcCount;
    private Long aocCount;
    private Long apsCount;
    private Long insCount;
    private Long wftCount;
    private Long wfsCount;
    private Long tbrCount;
    private Long finCount;
    private Long cloCount;
    private Long nvaCount;
    private Long unaffected;
    private Long total;
    private Long noticeCount;
    private Long reminderCount;

    public float getProgressRate() {
        if (total == 0) {
            return 0f;
        }
        return (float) (tbrCount + finCount + cloCount) * 100 / total;
    }

    public Long getValidated() {
        return finCount + cloCount;
    }

    public Long getInProgress() {
        return prcCount + aocCount + apsCount + insCount;
    }
}
