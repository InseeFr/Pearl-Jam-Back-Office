package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.api.service.UserService;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
@Transactional
public class OrganizationUnitServiceImpl implements OrganizationUnitService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationUnitServiceImpl.class);

	@Autowired
	OrganizationUnitRepository organizationUnitRepository;

	@Autowired
	UserService userService;
	
	@Autowired
	SurveyUnitRepository surveyUnitRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MessageRepository messageRepository;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Response createOrganizationUnits(List<OrganizationUnitContextDto> organizationUnitDtos)
			throws NoOrganizationUnitException, UserAlreadyExistsException {
		// Verifying all attributes are set
		if (organizationUnitDtos.stream().anyMatch(dto -> !allAttributesHaveValue(dto))) {
			LOGGER.error("At least one organizational unit has an attribute missing");
			return new Response("At least one organizational unit has an attribute missing", HttpStatus.BAD_REQUEST);
		}
		String alreadyPresentIds = organizationUnitDtos.stream().map(OrganizationUnitContextDto::getOrganisationUnit).filter(id -> organizationUnitRepository.findById(id).isPresent()).collect(Collectors.joining(", "));
		if (alreadyPresentIds != null && !alreadyPresentIds.isEmpty()) {
			LOGGER.error("The following Organizational units were already present: [{}]", alreadyPresentIds);
			return new Response("The following Organizational units were already present: " + alreadyPresentIds, HttpStatus.BAD_REQUEST);
		}
		// Adding organization units
		addOrganizationalUnits(organizationUnitDtos);
		// Adding users
		addUsers(organizationUnitDtos);
		return new Response("", HttpStatus.OK);
	}

	private boolean allAttributesHaveValue(OrganizationUnitContextDto dto) {
		return dto.getOrganisationUnit() != null && !dto.getOrganisationUnit().isBlank()
				&& dto.getOrganisationUnitLabel() != null && !dto.getOrganisationUnitLabel().isBlank()
				&& dto.getType() != null;
	}

	private void addUsers(List<OrganizationUnitContextDto> organizationUnitDtos) throws UserAlreadyExistsException, NoOrganizationUnitException {
		for (OrganizationUnitContextDto ouDto : organizationUnitDtos) {
			if (ouDto.getUsers() != null) {
				userService.createUsersByOrganizationUnit(ouDto.getUsers(), ouDto.getOrganisationUnit());	
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
				if (ouDto.getOrganisationUnitRef() == null || ouDto.getOrganisationUnitRef().stream().noneMatch(ouId -> !organizationUnitRepository.findById(ouId).isPresent())) {
					OrganizationUnit orgUnit = new OrganizationUnit(ouDto.getOrganisationUnit(), ouDto.getOrganisationUnitLabel(), ouDto.getType());
					organizationUnitRepository.save(orgUnit);
					added.add(ouDto);
					setParentInChildOU(ouDto, orgUnit);
				}
			}
			remainingToAdd.removeAll(added);

		}
		if (!remainingToAdd.isEmpty()) {
			String remainingIds = remainingToAdd.stream().map(OrganizationUnitContextDto::getOrganisationUnit).collect(Collectors.joining(", "));
			throw new NoOrganizationUnitException("One of the organizationUnitRef of the following organizational units could not be found: " + remainingIds);
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
				.map(ou -> new OrganizationUnitContextDto(ou, userRepository.findAllByOrganizationUnitId(ou.getId()), organizationUnitRepository.findChildrenId(ou.getId())))
				.collect(Collectors.toList());
	}

	@Override
	public HttpStatus delete(String id) {
		Optional<OrganizationUnit> ou = organizationUnitRepository.findById(id);
		if(!ou.isPresent()) {
			return HttpStatus.NOT_FOUND;
		}
		List<SurveyUnit> lstSu = surveyUnitRepository.findByOrganizationUnitIdIn(List.of(id));
		List<User> lstUser = userRepository.findAllByOrganizationUnitId(id);
		if(!lstSu.isEmpty() || !lstUser.isEmpty()) {
			return HttpStatus.BAD_REQUEST;
		}
		messageRepository.deleteOUMessageRecipientByOrganizationUnitId(ou.get().getId());
		if(ou.get().getOrganizationUnitParent()!=null) {
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
