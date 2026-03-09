package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.surveyunit.model.Response;
import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.api.user.dto.UserContextDto;
import fr.insee.pearljam.api.user.dto.UserDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.domain.exception.UserNotAssociatedToCampaignException;
import fr.insee.pearljam.domain.user.port.userside.UserService;

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
    public List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels) {
        return List.of();
    }

    @Override
    public void checkUserAssociationToCampaign(String campaignId, String userId) {
        throw new UserNotAssociatedToCampaignException(campaignId,userId);
    }

    @Override
    public Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId) {
        return null;
    }

    @Override
    public void delete(String id) {
        // no-imp;
    }

    @Override
    public UserDto createUser(UserDto user) {
        return null;
    }

    @Override
    public UserDto updateUser(UserDto user) {
        return null;
    }
}
