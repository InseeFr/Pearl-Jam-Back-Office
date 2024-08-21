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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public class UserFakeService implements UserService {
    @Override
    public Optional<UserDto> getUser(String userId) {
        return Optional.empty();
    }

    @Override
    public boolean userIsPresent(String userId) {
        return false;
    }

    @Override
    public void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu, boolean saveAllLevels) {

    }

    @Override
    public List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels) {
        return List.of();
    }

    @Override
    public boolean isUserAssocitedToCampaign(String campaignId, String userId) {
        return false;
    }

    @Override
    public Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId) throws UserAlreadyExistsException, NoOrganizationUnitException {
        return null;
    }

    @Override
    public HttpStatus delete(String id) {
        return null;
    }

    @Override
    public boolean checkValidity(UserDto user) {
        return false;
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
