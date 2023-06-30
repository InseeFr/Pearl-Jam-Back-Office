package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface InterviewerService {

	Optional<List<CampaignDto>> findCampaignsOfInterviewer(String interviewerId);

	Response createInterviewers(List<InterviewerContextDto> interviewers);

	Set<InterviewerDto> getListInterviewers(String userId);

	boolean isPresent(String interviewerId);

	boolean delete(String id);

	Optional<InterviewerContextDto> update(String id, InterviewerContextDto interviewer);

	Optional<InterviewerContextDto> findDtoById(String id);

	List<InterviewerContextDto> getCompleteListInterviewers();

}
