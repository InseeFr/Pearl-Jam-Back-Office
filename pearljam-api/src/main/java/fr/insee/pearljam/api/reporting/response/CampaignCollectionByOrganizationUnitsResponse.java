package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignCollectionByOrganizationUnits")
public record CampaignCollectionByOrganizationUnitsResponse(
        List<OrganizationUnit> organizationUnits,
        Campaign campaign,
        long updatedAt) {



    @Schema(name = "CampaignCollectionByOrganizationUnitsOU")
    public record OrganizationUnit(
            String organizationUnitLabel,
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}

    @Schema(name = "CampaignCollectionByOrganizationUnitsCampaign")
    public record Campaign(
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}
}
