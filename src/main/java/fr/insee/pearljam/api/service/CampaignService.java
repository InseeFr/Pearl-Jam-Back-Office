package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;

/**
 * Service for the Campaign entity
 * @author scorcaud
 *
 */
public interface CampaignService {
	
	List<CampaignDto> getListCampaign(String userId);
  List<InterviewerDto> getListInterviewers(String userId, String campaignId);
  boolean isUserAssociatedToTheCampaign(String userId, String campaignId);
}
