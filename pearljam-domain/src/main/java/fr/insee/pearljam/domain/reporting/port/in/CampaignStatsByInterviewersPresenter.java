package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;

import java.util.List;

public interface CampaignStatsByInterviewersPresenter<T> {
    T present(List<InterviewerDailyStats> interviewerStats, CampaignDailyStats siteStats, CampaignDailyStats campaignStats);
}
