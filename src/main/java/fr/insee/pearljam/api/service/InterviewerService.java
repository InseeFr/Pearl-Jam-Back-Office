package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Set;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.exception.NotFoundException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface InterviewerService {
	
	List<CampaignDto> findCampaignsOfInterviewer(String interviewerId) throws NotFoundException;
	
	Response createInterviewers(List<InterviewerContextDto> interviewers);

	Set<InterviewerDto> getListInterviewers(String userId);

}
