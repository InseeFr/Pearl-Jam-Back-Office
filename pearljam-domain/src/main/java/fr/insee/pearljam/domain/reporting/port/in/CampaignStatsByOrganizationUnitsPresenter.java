package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;

import java.util.List;

public interface CampaignStatsByOrganizationUnitsPresenter<T> {
    T present(List<OrganizationUnitDailyStats> organizationUnitStats, CampaignDailyStats campaignStats);
}
