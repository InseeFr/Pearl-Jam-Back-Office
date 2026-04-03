package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressByOrganizationUnitsResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CommunicationsProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.OrganizationUnitDailyStats;
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
                                StatesProgress.from(organizationUnit),
                                CommunicationsProgress.from(organizationUnit)))
                        .toList(),
                new CampaignProgressByOrganizationUnitsResponse.Campaign(
                        campaignStats.getProgressStateRate(),
                        StatesProgress.from(campaignStats),
                        CommunicationsProgress.from(campaignStats))
        );
    }
}
