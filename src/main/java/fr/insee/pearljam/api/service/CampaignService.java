package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;

/**
 * Service for the Campaign entity
 * @author scorcaud
 *
 */
public interface CampaignService {
	
	List<CampaignDto> getListCampaign(String userId);
}
