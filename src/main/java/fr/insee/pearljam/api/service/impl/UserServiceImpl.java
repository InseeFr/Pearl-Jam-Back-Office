package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.UserNotAssociatedToCampaignException;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final OrganizationUnitRepository organizationUnitRepository;
	private final UserRepository userRepository;
	private final CampaignRepository campaignRepository;

	public Optional<UserDto> getUser(String userId) {
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);

		if (user.isEmpty()) {
			return Optional.empty();
		}

		OrganizationUnitDto organizationUnitsParent = new OrganizationUnitDto();
		organizationUnitsParent.setId(user.get().getOrganizationUnit().getId());
		organizationUnitsParent.setLabel(user.get().getOrganizationUnit().getLabel());
		getOrganizationUnits(organizationUnits, user.get().getOrganizationUnit(), false);

		return Optional.of(new UserDto(user.get().getId(), user.get().getFirstName(), user.get().getLastName(),
				organizationUnitsParent, organizationUnits));
	}

	public boolean userIsPresent(String userId) {
		return userRepository.findByIdIgnoreCase(userId).isPresent();
	}

	public void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu,
			boolean saveAllLevels) {
		List<OrganizationUnit> lstOu = organizationUnitRepository.findChildren(currentOu.getId());
		if (lstOu.isEmpty()) {
			organizationUnits.add(new OrganizationUnitDto(currentOu.getId(), currentOu.getLabel()));
			return;
		}
		if (saveAllLevels) {
			organizationUnits.add(new OrganizationUnitDto(currentOu.getId(), currentOu.getLabel()));
		}
		for (OrganizationUnit ou : lstOu) {
			getOrganizationUnits(organizationUnits, ou, saveAllLevels);
		}
	}

	public List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels) {
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		user.ifPresent(value -> getOrganizationUnits(organizationUnits, value.getOrganizationUnit(), saveAllLevels));
		return organizationUnits;
	}

	public void checkUserAssociationToCampaign(String campaignId, String userId) throws UserNotAssociatedToCampaignException, CampaignNotFoundException {
		List<OrganizationUnitDto> lstUserOU = new ArrayList<>();
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		if (user.isEmpty()) {
			throw new UserNotAssociatedToCampaignException(campaignId, userId);
		}
		List<String> lstIdOUCampaign = campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId);
		if(lstIdOUCampaign.isEmpty()){
			throw new CampaignNotFoundException();
		}

		getOrganizationUnits(lstUserOU, user.get().getOrganizationUnit(), true);
		List<String> lstIdOUUser = lstUserOU.stream().map(OrganizationUnitDto::getId).toList();
		boolean notAssociated = Collections.disjoint(lstIdOUUser, lstIdOUCampaign);

		if (notAssociated){
			throw new UserNotAssociatedToCampaignException(campaignId, userId);
		}

	}

	@Override
	public Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId)
			throws UserAlreadyExistsException, NoOrganizationUnitException {
		for (UserContextDto user : users) {
			Optional<User> userOpt = userRepository.findById(user.getId());
			if (userOpt.isPresent()) {
				throw new UserAlreadyExistsException("Found duplicate user with id: " + user.getId());
			}
			Optional<OrganizationUnit> ouOpt = organizationUnitRepository.findById(organisationUnitId);
			if (ouOpt.isEmpty()) {
				throw new NoOrganizationUnitException("Organization Unit does not exist : " + organisationUnitId);
			}
			userRepository.save(new User(user.getId(), user.getFirstName(), user.getLastName(), ouOpt.get()));

		}
		return new Response("", HttpStatus.OK);
	}

	@Override
	@Transactional
	public HttpStatus delete(String id) {
		Optional<User> user = userRepository.findById(id);
		if (user.isEmpty()) {
			return HttpStatus.NOT_FOUND;
		}
		userRepository.delete(user.get());
		return HttpStatus.OK;
	}

	@Override
	public boolean checkValidity(UserDto user) {
		if (user == null || user.getOrganizationUnit() == null)
			return false;
		String ouId = user.getOrganizationUnit().getId();
		boolean attributesValidity = user.getFirstName() != null && user.getLastName() != null && user.getId() != null;
		boolean ouValidity = ouId != null && organizationUnitRepository.findById(ouId).isPresent();

		return ouValidity && attributesValidity;
	}

	@Override
	public UserDto createUser(UserDto userToCreate) throws NotFoundException {
		Optional<OrganizationUnit> ouOpt = organizationUnitRepository
				.findById(userToCreate.getOrganizationUnit().getId());

		OrganizationUnit ou = ouOpt
				.orElseThrow(() -> new NotFoundException(String.format("Organization Unit with id %s not found",
						userToCreate.getOrganizationUnit().getId())));
		User user = new User(userToCreate.getId(), userToCreate.getFirstName(), userToCreate.getLastName(), ou);
		userRepository.save(user);
		return getUser(userToCreate.getId()).orElse(null);
	}

	@Override
	public UserDto updateUser(UserDto user) throws NotFoundException {
		Optional<User> optDbUser = userRepository.findByIdIgnoreCase(user.getId());
		if (optDbUser.isEmpty()) {
			throw new NotFoundException(String.format("User with id %s not found", user.getId()));
		}
		User dbUser = optDbUser.get();
		dbUser.setFirstName(user.getFirstName());
		dbUser.setLastName(user.getLastName());
		OrganizationUnit dbOu = organizationUnitRepository.findByIdIgnoreCase(user.getOrganizationUnit().getId())
				.orElse(null);

		dbUser.setOrganizationUnit(dbOu);
		User updatedUser = userRepository.save(dbUser);
		OrganizationUnitDto ou = organizationUnitRepository
				.findDtoByIdIgnoreCase(updatedUser.getOrganizationUnit().getId()).orElse(null);
		return new UserDto(updatedUser.getId(), updatedUser.getFirstName(), updatedUser.getLastName(), ou,
				null);

	}
}
