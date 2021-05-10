package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface ContactOutcomeService {
	List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date);
	
	ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId, Long date);

	ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String id, Long date);
	
	ContactOutcomeTypeCountDto getContactOutcomeByInterviewerAndCampaign(String userId, String campaignId,
			String interviewerId, Long date);
}
