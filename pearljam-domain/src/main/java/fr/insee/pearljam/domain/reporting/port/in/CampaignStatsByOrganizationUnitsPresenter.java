package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.OrganizationUnitDailyStats;

import java.util.List;

public interface CampaignStatsByOrganizationUnitsPresenter<T> {
    T present(List<OrganizationUnitDailyStats> organizationUnitStats, CampaignDailyStats campaignStats);
}
