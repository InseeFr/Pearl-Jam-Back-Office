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
    private String campaignId;
    private String campaignLabel;
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
}
