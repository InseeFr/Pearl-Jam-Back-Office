package fr.insee.pearljam.organization.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.insee.pearljam.organization.domain.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.organization.domain.service.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.OrganizationUnit;
import fr.insee.pearljam.shared.Response;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.User;
import fr.insee.pearljam.organization.infrastructure.rest.dto.OrganizationUnitContextDto;
import fr.insee.pearljam.organization.infrastructure.rest.dto.OrganizationUnitDto;
import fr.insee.pearljam.message.infrastructure.persistence.jpa.repository.MessageRepository;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.repository.OrganizationUnitRepository;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.repository.SurveyUnitRepository;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.repository.UserRepository;
import fr.insee.pearljam.organization.domain.port.userside.OrganizationUnitService;
import fr.insee.pearljam.organization.domain.port.userside.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
	private final UserService userService;
	private final SurveyUnitRepository surveyUnitRepository;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Response createOrganizationUnits(List<OrganizationUnitContextDto> organizationUnitDtos)
			throws NoOrganizationUnitException, UserAlreadyExistsException {
		// Verifying all attributes are set
		if (organizationUnitDtos.stream().anyMatch(dto -> !allAttributesHaveValue(dto))) {
			log.error("At least one organizational unit has an attribute missing");
			return new Response("At least one organizational unit has an attribute missing", HttpStatus.BAD_REQUEST);
		}
		String alreadyPresentIds = organizationUnitDtos.stream().map(OrganizationUnitContextDto::getId)
				.filter(id -> organizationUnitRepository.findById(id).isPresent()).collect(Collectors.joining(", "));
		if (alreadyPresentIds != null && !alreadyPresentIds.isEmpty()) {
			String errorMessage = String.format("The following Organizational units were already present: [%s]",
					alreadyPresentIds);
			log.error(errorMessage);
			return new Response(errorMessage,
					HttpStatus.BAD_REQUEST);
		}
		// Adding organization units
		addOrganizationalUnits(organizationUnitDtos);
		// Adding users
		addUsers(organizationUnitDtos);
		return new Response("", HttpStatus.OK);
	}

	private boolean allAttributesHaveValue(OrganizationUnitContextDto dto) {
		return dto.getId() != null && !dto.getId().isBlank()
				&& dto.getLabel() != null && !dto.getLabel().isBlank()
				&& dto.getType() != null;
	}

	private void addUsers(List<OrganizationUnitContextDto> organizationUnitDtos)
			throws UserAlreadyExistsException, NoOrganizationUnitException {
		for (OrganizationUnitContextDto ouDto : organizationUnitDtos) {
			if (ouDto.getUsers() != null) {
				userService.createUsersByOrganizationUnit(ouDto.getUsers(), ouDto.getId());
			}
		}
	}

	private void addOrganizationalUnits(List<OrganizationUnitContextDto> organizationUnitDtos)
			throws NoOrganizationUnitException {
		// Adding OUs which children are already in db first, until all are added
		List<OrganizationUnitContextDto> remainingToAdd = new ArrayList<>();
		remainingToAdd.addAll(organizationUnitDtos);
		Integer remainingNb = -1;
		while (remainingNb > remainingToAdd.size() || remainingNb < 0) {
			remainingNb = remainingToAdd.size();
			List<OrganizationUnitContextDto> added = new ArrayList<>();
			for (OrganizationUnitContextDto ouDto : remainingToAdd) {
				if (ouDto.getOrganisationUnitRef() == null || ouDto.getOrganisationUnitRef().stream()
						.noneMatch(ouId -> !organizationUnitRepository.findById(ouId).isPresent())) {
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

}
