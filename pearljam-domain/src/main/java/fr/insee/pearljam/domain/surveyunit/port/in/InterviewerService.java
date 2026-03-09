package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.api.campaign.dto.output.CampaignVisibilityPeriodDto;
import fr.insee.pearljam.domain.shared.model.Response;
import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.surveyunit.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.surveyunit.service.exception.InterviewerNotFoundException;

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
