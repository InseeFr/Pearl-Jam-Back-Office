package fr.insee.pearljam.domain.reporting.readmodel.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDailyStats {
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

    public static CampaignDailyStats empty(String id, String label) {
        CampaignDailyStats campaignDailyStats =  new CampaignDailyStats();
        campaignDailyStats.setCampaignId(id);
        campaignDailyStats.setCampaignLabel(label);
        return campaignDailyStats;
    }

    public static CampaignDailyStats empty(String id) {
        return empty(id, null);
    }

    public float progressRate() {
        if (total == 0) {
            return 0f;
        }
        return (float) (tbrCount + finCount + cloCount) * 100 / total;
    }

    public Long getToProcessInterviewer() {
        return vicCount + prcCount + aocCount + apsCount + insCount + wftCount;
    }

    public Long getValidated() {
        return finCount + cloCount;
    }

    public Long getInProgress() {
        return prcCount + aocCount + apsCount + insCount;
    }
}
