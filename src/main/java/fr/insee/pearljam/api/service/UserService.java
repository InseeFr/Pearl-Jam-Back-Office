package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;

/**
 * Service for the Interviewer entity
 * @author scorcaud
 *
 */
public interface UserService {

	/**
	 * @param userId
	 * @return {@link UserDto}
	 * @throws NotFoundException 
	 */
	UserDto getUser(String userId) throws NotFoundException;
	
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

	Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId) throws UserAlreadyExistsException, NoOrganizationUnitException;

	HttpStatus delete(String id);
}
