package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.InterviewerNotFoundException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface InterviewerService {

	Optional<List<CampaignDto>> findCampaignsOfInterviewer(String interviewerId);

	Response createInterviewers(List<InterviewerContextDto> interviewers);

	Set<InterviewerDto> getInterviewersByUserAndCampaign(String userId);

	boolean delete(String id);

	Optional<InterviewerContextDto> update(String id, InterviewerContextDto interviewer);

	Optional<InterviewerContextDto> findDtoById(String id);

	List<InterviewerContextDto> getCompleteListInterviewers();

	List<InterviewerDto> getInterviewersByUserAndCampaign(String userId, String campaignId) throws InterviewerNotFoundException, CampaignNotFoundException;

}
