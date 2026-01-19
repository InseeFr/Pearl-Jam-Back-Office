package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;
import fr.insee.pearljam.api.service.UserService;

import java.util.List;

public class UserFakeService implements UserService {
    @Override
    public UserDto getUser(String userId) throws NotFoundException {
        throw new NotFoundException("User not found");
    }

    @Override
    public boolean userIsPresent(String userId) {
        return false;
    }

    @Override
    public void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu, boolean saveAllLevels) {
        // not used at this moment
    }

    @Override
    public List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels) {
        return List.of();
    }

    @Override
    public boolean isUserAssociatedToCampaign(String campaignId, String userId) {
        return false;
    }

    @Override
    public Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId) throws UserAlreadyExistsException, NoOrganizationUnitException {
        return null;
    }

    @Override
    public void delete(String id) {
        return;
    }

    @Override
    public UserDto createUser(UserDto user) throws NotFoundException {
        return null;
    }

    @Override
    public UserDto updateUser(UserDto user) throws NotFoundException {
        return null;
    }
}
