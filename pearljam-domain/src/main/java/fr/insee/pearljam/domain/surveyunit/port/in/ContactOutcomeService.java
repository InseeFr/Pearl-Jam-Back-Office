package fr.insee.pearljam.domain.surveyunit.port.in;

import java.util.List;

import fr.insee.pearljam.contracts.surveyunit.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.contracts.surveyunit.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface ContactOutcomeService {
	List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date);
	
	ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId, Long date) throws EntityNotFoundException;

	ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String id, Long date) throws CampaignNotFoundException;
	
	ContactOutcomeTypeCountDto getContactOutcomeByInterviewerAndCampaign(String userId, String campaignId,
			String interviewerId, Long date) throws EntityNotFoundException;
}
