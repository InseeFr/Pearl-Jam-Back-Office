package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignProgressByOrganizationUnits;

import java.time.LocalDate;

public interface CampaignProgressByOrganizationUnitsPort {
    CampaignProgressByOrganizationUnits getProgressForDay(String userId,
                                                          String campaignId,
                                                          LocalDate day) throws CampaignNotFoundException;
}