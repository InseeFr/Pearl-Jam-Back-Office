package fr.insee.pearljam.surveyunit.domain.port.userside;

import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.state.StateCountDto;
import fr.insee.pearljam.configuration.web.exception.NotFoundException;
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
	 * @throws NotFoundException 
	 */
	StateCountDto getStateCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits) throws NotFoundException;
	
	/**
	 * @param userId
	 * @param campaignId
	 * @param date
	 * @return {@link StateCountCampaignDto}
	 * @throws NotFoundException 
	 */
	StateCountCampaignDto getStateCountByCampaign(String userId, String campaignId, Long date) throws NotFoundException;

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
	StateCountDto getNbSUNotAttributedStateCount(String userId, String id, Long date) throws NotFoundException;
}
