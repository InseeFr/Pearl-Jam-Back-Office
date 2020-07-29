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

	UserDto getUser(String userId);
	
	void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu, boolean saveAllLevels);

}
