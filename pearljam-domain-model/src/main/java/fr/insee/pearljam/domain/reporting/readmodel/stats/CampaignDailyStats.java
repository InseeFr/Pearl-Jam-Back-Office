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

    public long getToProcessInterviewer() {
        return vicCount + prcCount + aocCount + apsCount + insCount + wftCount;
    }

    public long getValidated() {
        return finCount + cloCount;
    }

    public long getInProgress() {
        return prcCount + aocCount + apsCount + insCount;
    }
}
