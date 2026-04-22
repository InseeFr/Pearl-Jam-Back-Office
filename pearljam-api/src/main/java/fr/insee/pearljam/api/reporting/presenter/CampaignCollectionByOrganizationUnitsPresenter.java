package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionByOrganizationUnitsResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignCollectionByOrganizationUnitsPresenter implements
        CampaignStatsByOrganizationUnitsPresenter<CampaignCollectionByOrganizationUnitsResponse> {

    @Override
    public CampaignCollectionByOrganizationUnitsResponse present(List<OrganizationUnitDailyStats> organizationUnitStats,
                                                                 CampaignDailyStats campaignStats) {
        return new CampaignCollectionByOrganizationUnitsResponse(
                organizationUnitStats.stream()
                        .map(organizationUnit -> new CampaignCollectionByOrganizationUnitsResponse.OrganizationUnit(
                                organizationUnit.getOuLabel(),
                                organizationUnit.getAllocatedCount(),
                                CollectionRatesResponse.from(organizationUnit),
                                ContactOutcomesProgressResponse.from(organizationUnit),
                                ClosingCausesProgressResponse.from(organizationUnit)
                        ))
                        .toList(),
                new CampaignCollectionByOrganizationUnitsResponse.Campaign(
                        campaignStats.getAllocatedCount(),
                        CollectionRatesResponse.from(campaignStats),
                        ContactOutcomesProgressResponse.from(campaignStats),
                        ClosingCausesProgressResponse.from(campaignStats)
                )
        );
    }
}
