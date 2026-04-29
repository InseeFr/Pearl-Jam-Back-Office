package fr.insee.pearljam.domain.reporting.port.in;

import java.time.LocalDate;

public interface InterviewerCampaignsReportingPort {
    <T> T getCampaignsStatsForInterviewer(String userId, LocalDate day, String interviewerId, InterviewerCampaignsStatsPresenter<T> presenter);
}
