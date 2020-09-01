package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;

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
}
