package fr.insee.pearljam.organization.domain.port.userside;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.OrganizationUnit;
import fr.insee.pearljam.shared.Response;
import fr.insee.pearljam.organization.infrastructure.rest.dto.OrganizationUnitDto;
import fr.insee.pearljam.organization.infrastructure.rest.dto.user.UserContextDto;
import fr.insee.pearljam.organization.infrastructure.rest.dto.user.UserDto;
import fr.insee.pearljam.organization.domain.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.configuration.web.exception.NotFoundException;
import fr.insee.pearljam.organization.domain.service.exception.UserAlreadyExistsException;

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
	Optional<UserDto> getUser(String userId);

	/**
	 * @param userId
	 * @return true if user is present
	 */
	boolean userIsPresent(String userId);

	/**
	 * @param organizationUnits
	 * @param currentOu
	 * @param saveAllLevels
	 */
	void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu,
			boolean saveAllLevels);

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

	Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId)
			throws UserAlreadyExistsException, NoOrganizationUnitException;

	void delete(String id) throws NotFoundException;

	UserDto createUser(UserDto user) throws NotFoundException;

	UserDto updateUser(UserDto user) throws NotFoundException;
}
