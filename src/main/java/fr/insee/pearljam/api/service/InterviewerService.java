package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;

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
	Response createInterviewers(List<InterviewerContextDto> interviewers);

}
