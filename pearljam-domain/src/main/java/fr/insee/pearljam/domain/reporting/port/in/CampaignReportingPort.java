package fr.insee.pearljam.domain.reporting.port.in;

import java.time.LocalDate;

public interface CampaignReportingPort {
    <T> T getCampaignsStats(String userId, LocalDate day, CampaignStatsPresenter<T> presenter);
}
