package fr.insee.pearljam.surveyunit.domain.port.userside;

import java.util.List;

import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.configuration.web.exception.NotFoundException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface ContactOutcomeService {
	List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date);
	
	ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId, Long date) throws NotFoundException;

	ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String id, Long date) throws NotFoundException;
	
	ContactOutcomeTypeCountDto getContactOutcomeByInterviewerAndCampaign(String userId, String campaignId,
			String interviewerId, Long date) throws NotFoundException;
}
