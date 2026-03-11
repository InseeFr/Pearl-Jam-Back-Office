package fr.insee.pearljam.domain.organizationunit.port.in;

import fr.insee.pearljam.domain.shared.model.Response;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.api.organizationunit.dto.user.UserContextDto;
import fr.insee.pearljam.api.organizationunit.dto.user.UserDto;
import fr.insee.pearljam.domain.organizationunit.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserAlreadyExistsException;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserNotAssociatedToCampaignException;

import java.util.List;
import java.util.Optional;

/**
 * Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
public interface UserService {

	/**
	 * @param userId
	 * @return {@link Optional<UserDto>}
	 */
	UserDto getUser(String userId) throws EntityNotFoundException;

	/**
	 * @param userId
	 * @return true if user is present
	 */
	boolean userIsPresent(String userId);

	/**
	 * @param userId
	 * @param saveAllLevels
	 */
	List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels);

	/**
	 * @param campaignId campaign to check
	 * @param userId user to check
	 */
	void checkUserAssociationToCampaign(String campaignId, String userId) throws UserNotAssociatedToCampaignException, CampaignNotFoundException;

	Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId)
			throws UserAlreadyExistsException, NoOrganizationUnitException;

	void delete(String id) throws EntityNotFoundException;

	UserDto createUser(UserDto user) throws EntityNotFoundException, UserAlreadyExistsException;

	UserDto updateUser(UserDto user) throws EntityNotFoundException;
}
