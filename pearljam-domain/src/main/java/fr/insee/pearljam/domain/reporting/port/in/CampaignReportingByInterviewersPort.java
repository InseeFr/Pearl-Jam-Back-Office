package fr.insee.pearljam.domain.reporting.port.in;

import java.time.LocalDate;

public interface CampaignReportingByInterviewersPort {
    <T> T getProgressForDay(String userId,
                           String campaignId,
                           LocalDate day,
                           CampaignStatsByInterviewersPresenter<T> presenter);
}
