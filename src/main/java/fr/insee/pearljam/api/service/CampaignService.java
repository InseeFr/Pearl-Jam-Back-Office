package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;

/**
 * Service for the Campaign entity
 * @author scorcaud
 *
 */
public interface CampaignService {
	/**
	 * This method check if the Interviewer exist or not in database
	 * @param userId
	 * @return boolean
	 */
	boolean existUser(String userId);
	
	List<CampaignDto> getListCampaign(String userId);
}
