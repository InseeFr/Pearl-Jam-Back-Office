package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;

import java.time.LocalDate;

public interface CampaignReportingByOrganizationUnitsPort {
    <T> T getProgressForDay(String userId,
                           String campaignId,
                           LocalDate day,
                           CampaignStatsByOrganizationUnitsPresenter<T> presenter) throws CampaignNotFoundException;
}
