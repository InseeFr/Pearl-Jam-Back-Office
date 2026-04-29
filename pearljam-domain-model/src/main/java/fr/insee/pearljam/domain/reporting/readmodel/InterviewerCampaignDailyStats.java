package fr.insee.pearljam.domain.reporting.readmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InterviewerCampaignDailyStats extends AbstractDailyStats {

    private String campaignId;
    private String campaignLabel;

    public static InterviewerCampaignDailyStats empty(String id, String label) {
        InterviewerCampaignDailyStats interviewerCampaignDailyStats = new InterviewerCampaignDailyStats();
        interviewerCampaignDailyStats.setCampaignId(id);
        interviewerCampaignDailyStats.setCampaignLabel(label);
        return interviewerCampaignDailyStats;
    }

    public static InterviewerCampaignDailyStats empty(String id) {
        return empty(id, null);
    }

    @Override
    public long getAllocatedCount() {
        return getAllocatedFromStateCounts();
    }
}
