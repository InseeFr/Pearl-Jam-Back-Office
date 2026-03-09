package fr.insee.pearljam.domain.contactoutcome.port.userside;

import java.util.List;

import fr.insee.pearljam.api.contactoutcome.dto.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.api.contactoutcome.dto.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface ContactOutcomeService {
	List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date);
	
	ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId, Long date) throws NotFoundException, CampaignNotFoundException;

	ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String id, Long date) throws CampaignNotFoundException;
	
	ContactOutcomeTypeCountDto getContactOutcomeByInterviewerAndCampaign(String userId, String campaignId,
			String interviewerId, Long date) throws NotFoundException, CampaignNotFoundException;
}
