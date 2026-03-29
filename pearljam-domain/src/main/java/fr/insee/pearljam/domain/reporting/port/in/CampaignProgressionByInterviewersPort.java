package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignProgressionByInterviewers;

import java.time.LocalDate;

public interface CampaignProgressionByInterviewersPort {
    CampaignProgressionByInterviewers getProgressionForDay(String userId,
                                                           String campaignId,
                                                           LocalDate day) throws CampaignNotFoundException;
}