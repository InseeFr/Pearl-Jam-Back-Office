package fr.insee.pearljam.domain.reporting.port.in;

import java.time.LocalDate;

public interface CampaignReportingByOrganizationUnitsPort {
    <T> T getProgressForDay(String userId,
                           String campaignId,
                           LocalDate day,
                           CampaignStatsByOrganizationUnitsPresenter<T> presenter);
}
