package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface InterviewerService {
	
	List<CampaignDto> findCampaignsOfInterviewer(String interviewerId);
	
	Response createInterviewers(List<InterviewerContextDto> interviewers);

}
