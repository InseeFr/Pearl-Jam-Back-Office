package fr.insee.pearljam.domain.user.stub;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.organizationunit.dto.user.UserContextDto;
import fr.insee.pearljam.contracts.organizationunit.dto.user.UserDto;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserAlreadyExistsException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserNotAssociatedToCampaignException;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;
import fr.insee.pearljam.domain.shared.model.Response;

import java.util.List;

public class UserServiceStub implements UserService {

    private final List<OrganizationUnitSummary> orgUnits;

    public UserServiceStub(List<OrganizationUnitSummary> orgUnits) {
        this.orgUnits = orgUnits;
    }

    @Override
    public UserDto getUser(String userId) throws EntityNotFoundException {
        return null;
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
    public List<OrganizationUnitSummary> getUserOUsModel(String userId, boolean withChildren) {
        return orgUnits;
    }

    @Override
    public void checkUserAssociationToCampaign(String campaignId, String userId) throws UserNotAssociatedToCampaignException, CampaignNotFoundException {
        //not used
    }

    @Override
    public Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId) throws UserAlreadyExistsException, NoOrganizationUnitException {
        return null;
    }

    @Override
    public void delete(String id) throws EntityNotFoundException {
        //not used
    }

    @Override
    public UserDto createUser(UserDto user) throws EntityNotFoundException, UserAlreadyExistsException {
        return null;
    }

    @Override
    public UserDto updateUser(UserDto user) throws EntityNotFoundException {
        return null;
    }
}