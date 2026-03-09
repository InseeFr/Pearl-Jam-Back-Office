package fr.insee.pearljam.domain.organizationunit.service;

import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitContextDto;
import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitTreeDto;
import fr.insee.pearljam.api.organizationunit.dto.user.UserContextDto;
import fr.insee.pearljam.domain.organizationunit.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.domain.organizationunit.service.exception.OrganisationUnitAlreadyExistsException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserAlreadyExistsException;
import fr.insee.pearljam.domain.campaign.service.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.domain.message.port.out.MessageRepository;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.organizationunit.port.out.UserRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.OrganizationUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrganizationUnitServiceImpl implements OrganizationUnitService {

	private final OrganizationUnitRepository organizationUnitRepository;
	private final SurveyUnitRepository surveyUnitRepository;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createOrganizationUnits(List<OrganizationUnitContextDto> organizationUnitDtos)
            throws NoOrganizationUnitException, OrganizationalUnitNotFoundException, OrganisationUnitAlreadyExistsException,UserAlreadyExistsException {

		String alreadyPresentIds = organizationUnitDtos.stream()
				.map(OrganizationUnitContextDto::getId)
				.filter(id -> organizationUnitRepository.findById(id).isPresent())
				.collect(Collectors.joining(", "));

		if (!alreadyPresentIds.isEmpty()) {
			throw new OrganisationUnitAlreadyExistsException(
					"The following Organizational units were already present: [" + alreadyPresentIds + "]"
			);
		}
		// Adding organization units
		addOrganizationalUnits(organizationUnitDtos);
		// Adding users
		addUsers(organizationUnitDtos);
	}

	private void addUsers(List<OrganizationUnitContextDto> organizationUnitDtos) throws OrganizationalUnitNotFoundException, UserAlreadyExistsException {

		for (OrganizationUnitContextDto ouDto : organizationUnitDtos) {
			OrganizationUnitDB ouEntity = organizationUnitRepository
					.findById(ouDto.getId())
					.orElseThrow(OrganizationalUnitNotFoundException::new);

			if (ouDto.getUsers() != null) {
				for (UserContextDto user : ouDto.getUsers()) {
					insertUserForOrganisationUnitCreation(user, ouEntity);
				}
			}
		}
	}


	private void insertUserForOrganisationUnitCreation(UserContextDto user, OrganizationUnitDB ouEntity) throws UserAlreadyExistsException {

		Optional<UserDB> userOpt = userRepository.findById(user.getId());
		if (userOpt.isPresent()) {
			throw new UserAlreadyExistsException("Found duplicate user with id: " + user.getId());
		}
		userRepository.save(new UserDB(user.getId(), user.getFirstName(), user.getLastName(), ouEntity));


	}

	private void addOrganizationalUnits(List<OrganizationUnitContextDto> organizationUnitDtos)
			throws NoOrganizationUnitException {
		// Adding OUs which children are already in db first, until all are added
        List<OrganizationUnitContextDto> remainingToAdd = new ArrayList<>(organizationUnitDtos);
		int remainingNb = -1;
		while (remainingNb > remainingToAdd.size() || remainingNb < 0) {
			remainingNb = remainingToAdd.size();
			List<OrganizationUnitContextDto> added = new ArrayList<>();
			for (OrganizationUnitContextDto ouDto : remainingToAdd) {
				if (ouDto.getOrganisationUnitRef() == null || ouDto.getOrganisationUnitRef().stream()
						.allMatch(ouId -> organizationUnitRepository.findById(ouId).isPresent())) {
					OrganizationUnitDB orgUnit = new OrganizationUnitDB(ouDto.getId(), ouDto.getLabel(), ouDto.getType());
					organizationUnitRepository.save(orgUnit);
					added.add(ouDto);
					setParentInChildOU(ouDto, orgUnit);
				}
			}
			remainingToAdd.removeAll(added);

		}
		if (!remainingToAdd.isEmpty()) {
			String remainingIds = remainingToAdd.stream().map(OrganizationUnitContextDto::getId)
					.collect(Collectors.joining(", "));
			throw new NoOrganizationUnitException(
					String.format(
							"One of the organizationUnitRef of the following organizational units could not be found: %s",
							remainingIds));
		}
	}

	private void setParentInChildOU(OrganizationUnitContextDto ouDto, OrganizationUnitDB orgUnit) {
		if (ouDto.getOrganisationUnitRef() != null) {
			for (String ouId : ouDto.getOrganisationUnitRef()) {
				Optional<OrganizationUnitDB> childOuOpt = organizationUnitRepository.findById(ouId);
				if (childOuOpt.isPresent()) {
					OrganizationUnitDB childOu = childOuOpt.get();
					childOu.setOrganizationUnitParent(orgUnit);
					organizationUnitRepository.save(childOu);
				}
			}
		}
	}

	@Override
	public List<OrganizationUnitContextDto> findAllOrganizationUnits() {
		return organizationUnitRepository.findAll().stream()
				.map(ou -> new OrganizationUnitContextDto(ou, userRepository.findAllByOrganizationUnitId(ou.getId()),
						organizationUnitRepository.findChildrenId(ou.getId())))
				.toList();
	}

	@Override
	public HttpStatus delete(String id) {
		Optional<OrganizationUnitDB> ou = organizationUnitRepository.findById(id);
		if (ou.isEmpty()) {
			return HttpStatus.NOT_FOUND;
		}
		List<SurveyUnitDB> lstSu = surveyUnitRepository.findByOrganizationUnitIdIn(List.of(id));
		List<UserDB> lstUser = userRepository.findAllByOrganizationUnitId(id);
		if (!lstSu.isEmpty() || !lstUser.isEmpty()) {
			return HttpStatus.BAD_REQUEST;
		}
		messageRepository.deleteOUMessageRecipientByOrganizationUnitId(ou.get().getId());
		if (ou.get().getOrganizationUnitParent() != null) {
			ou.get().setOrganizationUnitParent(null);
			organizationUnitRepository.save(ou.get());
		}
		organizationUnitRepository.delete(ou.get());
		return HttpStatus.OK;
	}

	@Override
	public Optional<OrganizationUnitDto> findById(String ouId) {
		return organizationUnitRepository.findDtoByIdIgnoreCase(ouId);
	}

	@Override
	public boolean isPresent(String ouId) {
		return organizationUnitRepository.findDtoByIdIgnoreCase(ouId).isPresent();
	}


	public OrganizationUnitTreeDto getOrganizationUnitTree(String rootId, boolean saveAllLevels) {

		List<OrganizationUnitDB> subtree = organizationUnitRepository.findSubtree(rootId);

		if (subtree.isEmpty()) {
			throw new IllegalStateException("Root OU not found: " + rootId);
		}

		OrganizationUnitDB root = subtree.stream()
				.filter(ou -> ou.getId().equals(rootId))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Root OU not found: " + rootId));

		OrganizationUnitDto rootDto = new OrganizationUnitDto(root.getId(), root.getLabel());

		// include root + children
		if (saveAllLevels) {
			List<OrganizationUnitDto> locals = subtree.stream()
					.map(ou -> new OrganizationUnitDto(ou.getId(), ou.getLabel()))
					.toList();
			return new OrganizationUnitTreeDto(rootDto, locals);
		}

		// saveAllLevels = false: expect "locals" = children if root is a parent, else [root]
		boolean rootHasChildren = subtree.stream()
				.anyMatch(ou -> ou.getOrganizationUnitParent() != null
						&& rootId.equals(ou.getOrganizationUnitParent().getId()));

		if (!rootHasChildren) {
			return new OrganizationUnitTreeDto(rootDto, List.of(rootDto));
		}

		List<OrganizationUnitDto> locals = subtree.stream()
				.filter(ou -> !ou.getId().equals(rootId))
				.map(ou -> new OrganizationUnitDto(ou.getId(), ou.getLabel()))
				.toList();

		return new OrganizationUnitTreeDto(rootDto, locals);
	}


}
