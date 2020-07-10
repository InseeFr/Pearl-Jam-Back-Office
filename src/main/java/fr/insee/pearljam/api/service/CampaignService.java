package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;

/**
 * Service for the Campaign entity
 * @author scorcaud
 *
 */
public interface CampaignService {
	
	List<CampaignDto> getListCampaign(String userId);
  List<InterviewerDto> getListInterviewers(String userId, String campaignId, List<String> associatedOrgUnits);
  boolean isUserAssociatedToTheCampaign(String userId, String campaignId);
  StateCountDto getStateCount(String userId, String campaignId, String interviewerId, Long date, List<String> associatedOrgUnits);
}
