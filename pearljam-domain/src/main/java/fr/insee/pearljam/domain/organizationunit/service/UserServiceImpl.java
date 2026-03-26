package fr.insee.pearljam.domain.organizationunit.service;

import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.domain.shared.model.Response;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitTreeDto;
import fr.insee.pearljam.contracts.organizationunit.dto.user.UserContextDto;
import fr.insee.pearljam.contracts.organizationunit.dto.user.UserDto;
import fr.insee.pearljam.domain.organizationunit.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserAlreadyExistsException;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.OrganizationUnitService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserNotAssociatedToCampaignException;
import fr.insee.pearljam.domain.organizationunit.port.out.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
	private final OrganizationUnitService organizationUnitService;
	private final UserRepository userRepository;
	private final CampaignRepository campaignRepository;

	public UserDto getUser(String userId) throws EntityNotFoundException {
		UserDB user = userRepository.findByIdIgnoreCase(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		OrganizationUnitTreeDto ouTree = organizationUnitService.getOrganizationUnitTree(
				user.getOrganizationUnit().getId(),
				false);

		return new UserDto(
				user.getId(),
				user.getFirstName(),
				user.getLastName(),
				ouTree.root(),
				ouTree.childOrganizationUnits());
	}

	public List<OrganizationUnitSummary> getUserOUsModel(String userId, boolean saveAllLevels) {
		return getUserOUs(userId, saveAllLevels)
				.stream()
				.map(ouDto -> new OrganizationUnitSummary(ouDto.getId(), ouDto.getLabel()))
				.toList();
	}


	public boolean userIsPresent(String userId) {
		return userRepository.findByIdIgnoreCase(userId).isPresent();
	}

	@Deprecated(forRemoval = true)
	public List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels) {
		return userRepository.findByIdIgnoreCase(userId)
				.map(user -> organizationUnitService.getOrganizationUnitTree(
						user.getOrganizationUnit().getId(), saveAllLevels)
						.childOrganizationUnits())
				.orElse(List.of());
	}

	public void checkUserAssociationToCampaign(String campaignId, String userId)
			throws UserNotAssociatedToCampaignException, CampaignNotFoundException {

		UserDB user = userRepository.findByIdIgnoreCase(userId)
				.orElseThrow(() -> new UserNotAssociatedToCampaignException(campaignId, userId));

		List<String> lstIdOUCampaign = campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId);
		if (lstIdOUCampaign.isEmpty()) {
			throw new CampaignNotFoundException();
		}

		List<String> lstIdOUUser = organizationUnitService
				.getOrganizationUnitTree(user.getOrganizationUnit().getId(), true)
				.childOrganizationUnits()
				.stream()
				.map(OrganizationUnitDto::getId)
				.toList();

		boolean notAssociated = Collections.disjoint(lstIdOUUser, lstIdOUCampaign);
		if (notAssociated) {
			throw new UserNotAssociatedToCampaignException(campaignId, userId);
		}

	}

	@Override
	public Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId)
			throws UserAlreadyExistsException, NoOrganizationUnitException {
		for (UserContextDto user : users) {
			Optional<UserDB> userOpt = userRepository.findById(user.getId());
			if (userOpt.isPresent()) {
				throw new UserAlreadyExistsException("User already exists");
			}
			Optional<OrganizationUnitDB> ouOpt = organizationUnitRepository.findById(organisationUnitId);
			if (ouOpt.isEmpty()) {
				throw new NoOrganizationUnitException("Organization Unit does not exist : " + organisationUnitId);
			}
			userRepository.save(new UserDB(user.getId(), user.getFirstName(), user.getLastName(), ouOpt.get()));

		}
		return new Response("", HttpStatus.OK);
	}

	@Override
	@Transactional
	public void delete(String id) throws EntityNotFoundException {
		Optional<UserDB> user = userRepository.findById(id);
		if (user.isEmpty()) {
			throw new EntityNotFoundException("User does not exist");
		}
		userRepository.delete(user.get());
	}

	@Override
	public UserDto createUser(UserDto userToCreate) throws EntityNotFoundException, UserAlreadyExistsException {
		Optional<OrganizationUnitDB> ouOpt = organizationUnitRepository
				.findById(userToCreate.getOrganizationUnit().getId());

		String ouId = userToCreate.getOrganizationUnit().getId();
		if (!organizationUnitRepository.existsById(ouId)) {
			throw new OrganizationalUnitNotFoundException();
		}

		String userId = userToCreate.getId();
		if (userIsPresent(userId)) {
			throw new UserAlreadyExistsException("User already exists");
		}

		OrganizationUnitDB ou = ouOpt
				.orElseThrow(OrganizationalUnitNotFoundException::new);
		UserDB user = new UserDB(userToCreate.getId(), userToCreate.getFirstName(), userToCreate.getLastName(), ou);
		userRepository.save(user);
		return getUser(userToCreate.getId());
	}

	@Override
	public UserDto updateUser(UserDto user) throws EntityNotFoundException {
		UserDB dbUser = userRepository
				.findByIdIgnoreCase(user.getId())
				.orElseThrow(() -> new EntityNotFoundException(
						String.format("User with id %s not found", user.getId())));
		dbUser.setFirstName(user.getFirstName());
		dbUser.setLastName(user.getLastName());

		OrganizationUnitDB dbOu = organizationUnitRepository
				.findByIdIgnoreCase(user.getOrganizationUnit().getId())
				.orElseThrow(OrganizationalUnitNotFoundException::new);

		dbUser.setOrganizationUnit(dbOu);
		UserDB updatedUser = userRepository.save(dbUser);
		OrganizationUnitDto ou = organizationUnitRepository
				.findDtoByIdIgnoreCase(updatedUser.getOrganizationUnit().getId()).orElse(null);
		return new UserDto(updatedUser.getId(), updatedUser.getFirstName(), updatedUser.getLastName(), ou,
				null);

	}
}
