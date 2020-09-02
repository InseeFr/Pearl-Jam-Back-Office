package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserDto;

/**
 * Service for the Interviewer entity
 * @author scorcaud
 *
 */
public interface UserService {

	/**
	 * @param userId
	 * @return {@link UserDto}
	 */
	UserDto getUser(String userId);
	
	/**
	 * @param organizationUnits
	 * @param currentOu
	 * @param saveAllLevels
	 */
	void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu, boolean saveAllLevels);

	/**
	 * @param userId
	 * @param saveAllLevels
	 */
	List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels);
	
	/**
	 * @param campaignId
	 * @param userId
	 * @return {@link Boolean}
	 */
	public boolean isUserAssocitedToCampaign(String campaignId, String userId);
}
