package fr.insee.pearljam.domain.reporting.readmodel.stats;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CampaignDailyStats extends AbstractDailyStats {

    private String campaignId;
    private String campaignLabel;
    private long unaffectedCount;

    public static CampaignDailyStats empty(String id, String label) {
        CampaignDailyStats campaignDailyStats = new CampaignDailyStats();
        campaignDailyStats.setCampaignId(id);
        campaignDailyStats.setCampaignLabel(label);
        return campaignDailyStats;
    }

    public static CampaignDailyStats empty(String id) {
        return empty(id, null);
    }
}
