package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.configuration.properties.ApplicationProperties;
import fr.insee.pearljam.api.configuration.properties.AuthEnumProperties;
import fr.insee.pearljam.api.constants.Constants;
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
	private final OrganizationUnitRepository ouRepository;
	private final ApplicationProperties applicationProperties;

	public Optional<UserDto> getUser(String userId) {
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		if (applicationProperties.auth() != AuthEnumProperties.NOAUTH) {
			Optional<User> user = userRepository.findByIdIgnoreCase(userId);

			OrganizationUnitDto organizationUnitsParent = new OrganizationUnitDto();
			if (user.isPresent()) {
				organizationUnitsParent.setId(user.get().getOrganizationUnit().getId());
				organizationUnitsParent.setLabel(user.get().getOrganizationUnit().getLabel());
				getOrganizationUnits(organizationUnits, user.get().getOrganizationUnit(), false);

				return Optional.of(new UserDto(user.get().getId(), user.get().getFirstName(), user.get().getLastName(),
						organizationUnitsParent, organizationUnits));
			} else {
				return Optional.empty();
			}
		} else {
			Optional<OrganizationUnit> ouNat = ouRepository.findByIdIgnoreCase(applicationProperties.guestOU());
			if (ouNat.isPresent()) {
				getOrganizationUnits(organizationUnits, ouNat.get(), false);
				Optional<OrganizationUnitDto> ou = ouRepository
						.findDtoByIdIgnoreCase(applicationProperties.guestOU());
				if (ou.isPresent()) {
					return Optional.of(new UserDto("", "Guest", "", ou.get(), organizationUnits));
				}
			}
			return Optional.of(new UserDto("GUEST", "Guest", "",
					new OrganizationUnitDto("OU-NORTH", "Guest organizational unit"), List.of()));
		}
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
			if (user.isPresent()) {
				getOrganizationUnits(organizationUnits, user.get().getOrganizationUnit(), saveAllLevels);
			}
		} else {
			Optional<OrganizationUnit> ouNat = ouRepository.findByIdIgnoreCase(applicationProperties.guestOU());
			if (ouNat.isPresent()) {
				getOrganizationUnits(organizationUnits, ouNat.get(), saveAllLevels);
			} else {
				List<String> natOus = ouRepository.findNationalOUs();
				if (!natOus.isEmpty()) {
					Optional<OrganizationUnit> ou = ouRepository.findByIdIgnoreCase(natOus.get(0));
					if (ou.isPresent()) {
						getOrganizationUnits(organizationUnits, ou.get(), saveAllLevels);
					}
				}
			}
		}

		return organizationUnits;
	}

	public boolean isUserAssocitedToCampaign(String campaignId, String userId) {
		List<OrganizationUnitDto> lstUserOU = new ArrayList<>();
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		if (!user.isPresent()) {
			return false;
		}
		getOrganizationUnits(lstUserOU, user.get().getOrganizationUnit(), true);
		List<String> lstIdOUUser = lstUserOU.stream().map(OrganizationUnitDto::getId).collect(Collectors.toList());
		List<String> lstIdOUCampaign = campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId);
		return !Collections.disjoint(lstIdOUUser, lstIdOUCampaign);
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
			if (!ouOpt.isPresent()) {
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
		if (!user.isPresent()) {
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
		if (ouOpt.isEmpty()) {
			throw new NotFoundException(String.format("Organization Unit with id %s not found",
					userToCreate.getOrganizationUnit().getId()));
		}
		OrganizationUnit ou = ouOpt.get();
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
