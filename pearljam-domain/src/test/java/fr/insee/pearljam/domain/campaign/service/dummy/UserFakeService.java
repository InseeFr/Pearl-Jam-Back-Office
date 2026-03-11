package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.shared.model.Response;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.organizationunit.dto.user.UserContextDto;
import fr.insee.pearljam.contracts.organizationunit.dto.user.UserDto;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserNotAssociatedToCampaignException;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;

import java.util.List;

public class UserFakeService implements UserService {
    @Override
    public UserDto getUser(String userId) throws EntityNotFoundException {
        throw new EntityNotFoundException("User not found");
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
        // not used ath this moment
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
