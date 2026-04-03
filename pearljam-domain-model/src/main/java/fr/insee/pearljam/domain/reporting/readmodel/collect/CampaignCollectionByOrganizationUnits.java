package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.OrganizationUnitDailyStats;

import java.util.List;

public record CampaignCollectionByOrganizationUnits(
        List<OrganizationUnit> organizationUnits,
        Campaign campaign) {

    public static CampaignCollectionByOrganizationUnits from(
            List<OrganizationUnitDailyStats> organizationUnitDailyStats,
            CampaignDailyStats campaignDailyStats) {

        List<OrganizationUnit> ous = organizationUnitDailyStats.stream()
                .map(ouStats -> new OrganizationUnit(
                        ouStats.getOuLabel(),
                        ouStats.getAllocatedStateCount(),
                        CollectionRates.from(ouStats),
                        ContactOutcomesProgress.from(ouStats),
                        ClosingCausesProgress.from(ouStats)
                ))
                .toList();

        Campaign campaign = new Campaign(
                campaignDailyStats.getAllocatedStateCount(),
                CollectionRates.from(campaignDailyStats),
                ContactOutcomesProgress.from(campaignDailyStats),
                ClosingCausesProgress.from(campaignDailyStats));
        return new CampaignCollectionByOrganizationUnits(ous, campaign);
    }

    public record OrganizationUnit(
            String organizationUnitLabel,
            long allocated,
            CollectionRates rates,
            ContactOutcomesProgress outcomes,
            ClosingCausesProgress closingCauses
    ) {}

    public record Campaign(
            long allocated,
            CollectionRates rates,
            ContactOutcomesProgress outcomes,
            ClosingCausesProgress closingCauses
    ) {}
}
