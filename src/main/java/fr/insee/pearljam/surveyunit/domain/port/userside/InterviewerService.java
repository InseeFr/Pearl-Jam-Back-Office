package fr.insee.pearljam.surveyunit.domain.port.userside;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.insee.pearljam.shared.Response;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerDto;

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

	boolean delete(String id);

	Optional<InterviewerContextDto> update(String id, InterviewerContextDto interviewer);

	Optional<InterviewerContextDto> findDtoById(String id);

	List<InterviewerContextDto> getCompleteListInterviewers();

}
