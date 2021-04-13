package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface InterviewerService {
	
	List<CampaignDto> findCampaignsOfInterviewer(String interviewerId);
	ContactOutcomeTypeCountDto getContactOutcomeByInterviewerAndCampaign(String userId, String campaignId,
			String interviewerId, Long date);

}
