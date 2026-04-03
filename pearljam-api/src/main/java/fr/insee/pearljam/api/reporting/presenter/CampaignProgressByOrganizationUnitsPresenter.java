package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressByOrganizationUnitsResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignProgressByOrganizationUnitsPresenter implements
        CampaignStatsByOrganizationUnitsPresenter<CampaignProgressByOrganizationUnitsResponse> {

    @Override
    public CampaignProgressByOrganizationUnitsResponse present(List<OrganizationUnitDailyStats> organizationUnitStats,
                                                               CampaignDailyStats campaignStats) {
        return new CampaignProgressByOrganizationUnitsResponse(
                organizationUnitStats.stream()
                        .map(organizationUnit -> new CampaignProgressByOrganizationUnitsResponse.OrganizationUnit(
                                organizationUnit.getOuLabel(),
                                organizationUnit.getProgressStateRate(),
                                StatesProgressResponse.from(organizationUnit),
                                CommunicationsProgressResponse.from(organizationUnit)))
                        .toList(),
                new CampaignProgressByOrganizationUnitsResponse.Campaign(
                        campaignStats.getProgressStateRate(),
                        StatesProgressResponse.from(campaignStats),
                        CommunicationsProgressResponse.from(campaignStats))
        );
    }
}
