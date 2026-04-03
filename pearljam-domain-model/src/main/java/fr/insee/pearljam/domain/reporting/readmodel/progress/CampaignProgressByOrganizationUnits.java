package fr.insee.pearljam.domain.reporting.readmodel.progress;


import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.OrganizationUnitDailyStats;

import java.util.List;

public record CampaignProgressByOrganizationUnits(
        List<OrganizationUnit> organizationUnits,
        Campaign campaign) {

    public static CampaignProgressByOrganizationUnits from(
            List<OrganizationUnitDailyStats> organizationUnitDailyStats,
            CampaignDailyStats campaignDailyStats) {

        List<OrganizationUnit> ous = organizationUnitDailyStats.stream()
                .map(ouStats -> new OrganizationUnit(
                        ouStats.getOuLabel(),
                        ouStats.getProgressStateRate(),
                        StatesProgress.from(ouStats),
                        CommunicationsProgress.from(ouStats)
                ))
                .toList();

        Campaign campaign = new Campaign(campaignDailyStats.getProgressStateRate(),
                StatesProgress.from(campaignDailyStats),
                CommunicationsProgress.from(campaignDailyStats));
        return new CampaignProgressByOrganizationUnits(ous, campaign);
    }

    public record OrganizationUnit(
            String organizationUnitLabel,
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {}

    public record Campaign(
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {}
}
