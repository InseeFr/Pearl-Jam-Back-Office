package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountDto;

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
}
