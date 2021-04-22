package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CollectionDatesDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface CampaignService {

	/**
	 * @param userId
	 * @return {@link List} of {@link CampaignDto} 
	 */
	List<CampaignDto> getListCampaign(String userId);

	/**
	 * @param userId
	 * @param campaignId
	 * @return {@link List} of {@link InterviewerDto}
	 */
	List<InterviewerDto> getListInterviewers(String userId, String campaignId);

	boolean isUserPreference(String userId, String campaignId);

	/**
	 * @param userId
	 * @param campaignId
	 * @param interviewerId
	 * @param date
	 * @param associatedOrgUnits
	 * @return {@link StateCountDto}
	 */
	StateCountDto getStateCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits);
	
	/**
	 * @param userId
	 * @param campaignId
	 * @param date
	 * @return {@link StateCountCampaignDto}
	 */
	StateCountCampaignDto getStateCountByCampaign(String userId, String campaignId, Long date);

	CountDto getNbSUAbandonedByCampaign(String userId, String campaignId);

	CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId);

	
	/**
	 * @param userId
	 * @param date
	 * @return {@link List<StateCountDto>}
	 */
	List<StateCountDto> getStateCountByCampaigns(String userId, Long date);
	
	/**
	 * @param userId
	 * @param date
	 * @return {@link List<StateCountDto>}
	 */
	List<StateCountDto> getStateCountByInterviewer(String userId, Long date);

	HttpStatus updateDates(String userId, String id, CollectionDatesDto campaign);
	
	/**
	 * Update the visibility for a given campaign and a Organizational Unit
	 * @param idCampaign
	 * @param idOu
	 * @param updatedVisibility
	 * @return
	 */
	HttpStatus updateVisibility(String idCampaign, String idOu, VisibilityDto updatedVisibility);
	
	List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date);
	
	ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId, Long date);

	StateCountDto getNbSUNotAttributedStateCount(String userId, String id, Long date);

	ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String id, Long date);
}
