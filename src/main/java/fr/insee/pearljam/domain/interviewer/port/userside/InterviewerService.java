package fr.insee.pearljam.domain.interviewer.port.userside;

import fr.insee.pearljam.api.campaign.dto.output.CampaignVisibilityPeriodDto;
import fr.insee.pearljam.domain.surveyunit.model.Response;
import fr.insee.pearljam.api.interviewer.dto.InterviewerContextDto;
import fr.insee.pearljam.api.interviewer.dto.InterviewerDto;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.InterviewerNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface InterviewerService {

	List<CampaignVisibilityPeriodDto> findCampaignsOfInterviewer(String interviewerId);

	Response createInterviewers(List<InterviewerContextDto> interviewers);

	Set<InterviewerDto> getInterviewersForCurrentUser();

	void delete(String id);

	InterviewerContextDto update(String id, InterviewerContextDto interviewer);

	Optional<InterviewerContextDto> findDtoById(String id);

	List<InterviewerContextDto> getCompleteListInterviewers();

	List<InterviewerDto> getInterviewersByUserAndCampaign(String campaignId) throws InterviewerNotFoundException, CampaignNotFoundException;

}
