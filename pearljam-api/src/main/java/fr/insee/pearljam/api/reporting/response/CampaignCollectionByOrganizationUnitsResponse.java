package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignCollectionByOrganizationUnits")
public record CampaignCollectionByOrganizationUnitsResponse(
        List<OrganizationUnit> organizationUnits,
        Campaign campaign) {

    public static CampaignCollectionByOrganizationUnitsResponse from(
            List<OrganizationUnitDailyStats> organizationUnitDailyStats,
            CampaignDailyStats campaignDailyStats) {

        List<OrganizationUnit> ous = organizationUnitDailyStats.stream()
                .map(ouStats -> new OrganizationUnit(
                        ouStats.getOuLabel(),
                        ouStats.getAllocatedStateCount(),
                        CollectionRatesResponse.from(ouStats),
                        ContactOutcomesProgressResponse.from(ouStats),
                        ClosingCausesProgressResponse.from(ouStats)
                ))
                .toList();

        Campaign campaign = new Campaign(
                campaignDailyStats.getAllocatedStateCount(),
                CollectionRatesResponse.from(campaignDailyStats),
                ContactOutcomesProgressResponse.from(campaignDailyStats),
                ClosingCausesProgressResponse.from(campaignDailyStats));
        return new CampaignCollectionByOrganizationUnitsResponse(ous, campaign);
    }

    public record OrganizationUnit(
            String organizationUnitLabel,
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}

    public record Campaign(
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}
}
