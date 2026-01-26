package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitTreeDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.OrganisationUnitAlreadyExistsException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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
			OrganizationUnit ouEntity = organizationUnitRepository
					.findById(ouDto.getId())
					.orElseThrow(OrganizationalUnitNotFoundException::new);

			if (ouDto.getUsers() != null) {
				for (UserContextDto user : ouDto.getUsers()) {
					insertUserForOrganisationUnitCreation(user, ouEntity);
				}
			}
		}
	}


	private void insertUserForOrganisationUnitCreation(UserContextDto user, OrganizationUnit ouEntity) throws UserAlreadyExistsException {

		Optional<User> userOpt = userRepository.findById(user.getId());
		if (userOpt.isPresent()) {
			throw new UserAlreadyExistsException("Found duplicate user with id: " + user.getId());
		}
		userRepository.save(new User(user.getId(), user.getFirstName(), user.getLastName(), ouEntity));


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
					OrganizationUnit orgUnit = new OrganizationUnit(ouDto.getId(), ouDto.getLabel(), ouDto.getType());
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

	private void setParentInChildOU(OrganizationUnitContextDto ouDto, OrganizationUnit orgUnit) {
		if (ouDto.getOrganisationUnitRef() != null) {
			for (String ouId : ouDto.getOrganisationUnitRef()) {
				Optional<OrganizationUnit> childOuOpt = organizationUnitRepository.findById(ouId);
				if (childOuOpt.isPresent()) {
					OrganizationUnit childOu = childOuOpt.get();
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
		Optional<OrganizationUnit> ou = organizationUnitRepository.findById(id);
		if (ou.isEmpty()) {
			return HttpStatus.NOT_FOUND;
		}
		List<SurveyUnit> lstSu = surveyUnitRepository.findByOrganizationUnitIdIn(List.of(id));
		List<User> lstUser = userRepository.findAllByOrganizationUnitId(id);
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

		List<OrganizationUnit> subtree = organizationUnitRepository.findSubtree(rootId);

		if (subtree.isEmpty()) {
			throw new IllegalStateException("Root OU not found: " + rootId);
		}

		// id → entity
		Map<String, OrganizationUnit> byId = subtree.stream()
				.collect(Collectors.toMap(OrganizationUnit::getId, Function.identity()));

		// parentId → children
		Map<String, List<OrganizationUnit>> childrenByParent = subtree.stream()
				.filter(ou -> ou.getOrganizationUnitParent() != null)
				.collect(Collectors.groupingBy(ou -> ou.getOrganizationUnitParent().getId()));

		OrganizationUnit root = byId.get(rootId);

		OrganizationUnitDto rootDto =
				new OrganizationUnitDto(root.getId(), root.getLabel());

		List<OrganizationUnitDto> locals = new ArrayList<>();

		Deque<OrganizationUnit> stack = new ArrayDeque<>();
		stack.push(root);

		while (!stack.isEmpty()) {
			OrganizationUnit current = stack.pop();
			List<OrganizationUnit> children =
					childrenByParent.getOrDefault(current.getId(), List.of());

			boolean isLeaf = children.isEmpty();

			if (isLeaf || saveAllLevels) {
				locals.add(new OrganizationUnitDto(
						current.getId(),
						current.getLabel()
				));
			}

			children.forEach(stack::push);
		}

		return new OrganizationUnitTreeDto(rootDto, locals);
	}


}
