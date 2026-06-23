package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;

import java.util.List;

public interface InterviewerCampaignsStatsPresenter<T> {
    T present(List<InterviewerCampaignDailyStats> stats);
}
