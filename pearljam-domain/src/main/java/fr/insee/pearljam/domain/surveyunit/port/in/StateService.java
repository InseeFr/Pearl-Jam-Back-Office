package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.api.surveyunit.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.surveyunit.dto.state.StateCountDto;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;

import java.util.List;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface StateService {
	/**
	 * @param userId
	 * @param campaignId
	 * @param interviewerId
	 * @param date
	 * @param associatedOrgUnits
	 * @return {@link StateCountDto}
	 * @throws CampaignNotFoundException
	 */
	StateCountDto getStateCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits) throws CampaignNotFoundException;
	
	/**
	 * @param userId
	 * @param campaignId
	 * @param date
	 * @return {@link StateCountCampaignDto}
	 * @throws EntityNotFoundException
	 */
	StateCountCampaignDto getStateCountByCampaign(String userId, String campaignId, Long date) throws EntityNotFoundException;

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

	/**
	 * @param userId
	 * @param campaignId
	 * @param date
	 * @return {@link List<StateCountDto>}
	 */
	List<StateCountDto> getInterviewersStateCountByCampaign(String userId, String campaignId, Long date);

	/**
	 * @param userId
	 * @param id
	 * @param date
	 * @return {@link StateCountDto}
	 */
	StateCountDto getNbSUNotAttributedStateCount(String userId, String id, Long date) throws CampaignNotFoundException;
}
