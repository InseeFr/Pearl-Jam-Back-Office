package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;

import java.util.List;

public interface CampaignStatsPresenter<T> {
    T present(List<CampaignDailyStats> stats);
}
