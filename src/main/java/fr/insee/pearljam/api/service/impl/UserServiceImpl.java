package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.exception.*;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.dto.user.UserDto;
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
	private final OrganizationUnitRepository ouRepository;

	public UserDto getUser(String userId) throws NotFoundException {
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		User user = userRepository
				.findByIdIgnoreCase(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		OrganizationUnitDto organizationUnitsParent = new OrganizationUnitDto();
		organizationUnitsParent.setId(user.getOrganizationUnit().getId());
		organizationUnitsParent.setLabel(user.getOrganizationUnit().getLabel());
		getOrganizationUnits(organizationUnits, user.getOrganizationUnit(), false);

		return new UserDto(user.getId(), user.getFirstName(), user.getLastName(),
				organizationUnitsParent, organizationUnits);
	}

	public boolean userIsPresent(String userId) {
		return userRepository.findByIdIgnoreCase(userId).isPresent();
	}

	public void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu,
			boolean saveAllLevels) {
		List<OrganizationUnit> lstOu = organizationUnitRepository.findChildren(currentOu.getId());
		if (lstOu.isEmpty()) {
			organizationUnits.add(new OrganizationUnitDto(currentOu.getId(), currentOu.getLabel()));
		} else {
			if (saveAllLevels) {
				organizationUnits.add(new OrganizationUnitDto(currentOu.getId(), currentOu.getLabel()));
			}
			for (OrganizationUnit ou : lstOu) {
				getOrganizationUnits(organizationUnits, ou, saveAllLevels);
			}
		}
	}

	public List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels) {
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		if (!userId.equals(Constants.GUEST)) {
			Optional<User> user = userRepository.findByIdIgnoreCase(userId);
            user.ifPresent(value -> getOrganizationUnits(organizationUnits, value.getOrganizationUnit(), saveAllLevels));
		} else {
			Optional<OrganizationUnit> ouNat = ouRepository.findByIdIgnoreCase("OU-NORTH");
			if (ouNat.isPresent()) {
				getOrganizationUnits(organizationUnits, ouNat.get(), saveAllLevels);
			} else {
				List<String> natOus = ouRepository.findNationalOUs();
				if (!natOus.isEmpty()) {
					Optional<OrganizationUnit> ou = ouRepository.findByIdIgnoreCase(natOus.getFirst());
                    ou.ifPresent(organizationUnit -> getOrganizationUnits(organizationUnits, organizationUnit, saveAllLevels));
				}
			}
		}

		return organizationUnits;
	}

	public boolean isUserAssociatedToCampaign(String campaignId, String userId) {
		List<OrganizationUnitDto> lstUserOU = new ArrayList<>();
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		if (user.isEmpty()) {
			return false;
		}
		getOrganizationUnits(lstUserOU, user.get().getOrganizationUnit(), true);
		List<String> lstIdOUUser = lstUserOU.stream().map(OrganizationUnitDto::getId).toList();
		List<String> lstIdOUCampaign = campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId);
		return !Collections.disjoint(lstIdOUUser, lstIdOUCampaign);
	}

	@Override
	public Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId)
			throws UserAlreadyExistsException, NoOrganizationUnitException {
		for (UserContextDto user : users) {
			Optional<User> userOpt = userRepository.findById(user.getId());
			if (userOpt.isPresent()) {
				throw new UserAlreadyExistsException("User already exists");
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
	public void delete(String id) throws NotFoundException {
		Optional<User> user = userRepository.findById(id);
		if (user.isEmpty()) {
			throw new NotFoundException("User does not exist");
		}
		userRepository.delete(user.get());
	}

	@Override
	public UserDto createUser(UserDto userToCreate) throws NotFoundException, UserAlreadyExistsException {
		Optional<OrganizationUnit> ouOpt = organizationUnitRepository
				.findById(userToCreate.getOrganizationUnit().getId());

		String ouId = userToCreate.getOrganizationUnit().getId();
		if (!organizationUnitRepository.existsById(ouId)) {
			throw new NotFoundException("No organizational unit found with this id",
					String.format("Invalid organizational unit %s", ouId));
		}

		String userId = userToCreate.getId();
		if (userIsPresent(userId)) {
			throw new UserAlreadyExistsException("User already exists");
		}

		OrganizationUnit ou = ouOpt
				.orElseThrow(() -> new NotFoundException(String.format("Organization Unit with id %s not found",
						userToCreate.getOrganizationUnit().getId())));
		User user = new User(userToCreate.getId(), userToCreate.getFirstName(), userToCreate.getLastName(), ou);
		userRepository.save(user);
		return getUser(userToCreate.getId());
	}

	@Override
	public UserDto updateUser(UserDto user) throws NotFoundException {
		User dbUser = userRepository
				.findByIdIgnoreCase(user.getId())
				.orElseThrow(() -> new NotFoundException(
						"User not found",
						String.format("User with id %s not found", user.getId())));
		dbUser.setFirstName(user.getFirstName());
		dbUser.setLastName(user.getLastName());

		String ouId = user.getOrganizationUnit().getId();

		OrganizationUnit dbOu = organizationUnitRepository
				.findByIdIgnoreCase(user.getOrganizationUnit().getId())
				.orElseThrow(() -> new NotFoundException(
						"No organizational unit found with this id",
						String.format("Invalid organizational unit %s", ouId)));

		dbUser.setOrganizationUnit(dbOu);
		User updatedUser = userRepository.save(dbUser);
		OrganizationUnitDto ou = organizationUnitRepository
				.findDtoByIdIgnoreCase(updatedUser.getOrganizationUnit().getId()).orElse(null);
		return new UserDto(updatedUser.getId(), updatedUser.getFirstName(), updatedUser.getLastName(), ou,
				null);

	}
}
