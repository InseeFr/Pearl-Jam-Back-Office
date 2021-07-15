package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.configuration.ApplicationProperties;
import fr.insee.pearljam.api.configuration.ApplicationProperties.Mode;
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
import fr.insee.pearljam.api.service.MessageService;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.service.UserService;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	OrganizationUnitRepository organizationUnitRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CampaignRepository campaignRepository;
	
	@Autowired
	OrganizationUnitRepository ouRepository;
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PreferenceService preferenceService;
	

	@Autowired
	ApplicationProperties applicationProperties;

	public UserDto getUser(String userId) throws NotFoundException {
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		if (applicationProperties.getMode() != Mode.noauth) {
			Optional<User> user = userRepository.findByIdIgnoreCase(userId);
			
			OrganizationUnitDto organizationUnitsParent = new OrganizationUnitDto();
			if (user.isPresent()) {
				organizationUnitsParent.setId(user.get().getOrganizationUnit().getId());
				organizationUnitsParent.setLabel(user.get().getOrganizationUnit().getLabel());
				getOrganizationUnits(organizationUnits, user.get().getOrganizationUnit(), false);
					return new UserDto(user.get().getId(), user.get().getFirstName(), user.get().getLastName(),
							organizationUnitsParent, organizationUnits);
			} else {
				throw new NotFoundException(String.format("User %s does not exist in database", userId));
			}
		} else {
			Optional<OrganizationUnit> ouNat = ouRepository.findByIdIgnoreCase(applicationProperties.getGuestOU());
			if(ouNat.isPresent()) {
				getOrganizationUnits(organizationUnits, ouNat.get(), false);
				Optional<OrganizationUnitDto> ou =  ouRepository.findDtoByIdIgnoreCase(applicationProperties.getGuestOU());
				if(ou.isPresent()) {
					return new UserDto("", "Guest", "",  ou.get(), organizationUnits);
				}
			}
			return new UserDto("", "Guest", "",  new OrganizationUnitDto("OU-NORTH","Guest organizational unit"), List.of());
		}
	}

	public void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu, boolean saveAllLevels) {
		List<OrganizationUnit> lstOu = organizationUnitRepository.findChildren(currentOu.getId());
		if(lstOu.isEmpty()) {
			organizationUnits.add(new OrganizationUnitDto(currentOu.getId(), currentOu.getLabel()));
		}else {
			if(saveAllLevels) {
				organizationUnits.add(new OrganizationUnitDto(currentOu.getId(), currentOu.getLabel()));
			}
			for (OrganizationUnit ou : lstOu) {
				getOrganizationUnits(organizationUnits, ou, saveAllLevels);
			}
		}
	}
	
	public List<OrganizationUnitDto> getUserOUs(String userId, boolean saveAllLevels){
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		if (!userId.equals(Constants.GUEST)) {
			Optional<User> user = userRepository.findByIdIgnoreCase(userId);
			if (user.isPresent()) {
				userService.getOrganizationUnits(organizationUnits, user.get().getOrganizationUnit(), saveAllLevels);
			}
	  }
		else {
			Optional<OrganizationUnit> ouNat = ouRepository.findByIdIgnoreCase(applicationProperties.getGuestOU());
			if(ouNat.isPresent()) {
				userService.getOrganizationUnits(organizationUnits, ouNat.get(), saveAllLevels);
			} else {
				List<String> natOus = ouRepository.findNationalOUs();
				if (!natOus.isEmpty()) {
					Optional<OrganizationUnit> ou = ouRepository.findByIdIgnoreCase(natOus.get(0));
					if(ou.isPresent()) {
						userService.getOrganizationUnits(organizationUnits, ou.get(), saveAllLevels);
					}
				}
			}
		}
		
		return organizationUnits;
	}
	
	public boolean isUserAssocitedToCampaign(String campaignId, String userId) {
		List<OrganizationUnitDto> lstUserOU = new ArrayList<>();
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		if(!user.isPresent()) {
			return false;
		}
		getOrganizationUnits(lstUserOU, user.get().getOrganizationUnit(), true);
		List<String> lstIdOUUser = lstUserOU.stream().map(OrganizationUnitDto::getId).collect(Collectors.toList());
		List<String> lstIdOUCampaign = campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId);		
		return !Collections.disjoint(lstIdOUUser, lstIdOUCampaign);
	}

	@Override
	public Response createUsersByOrganizationUnit(List<UserContextDto> users, String organisationUnitId) throws UserAlreadyExistsException, NoOrganizationUnitException {
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
		if(!user.isPresent()) {
			return HttpStatus.NOT_FOUND;
		}
		// delete preference
		preferenceService.setPreferences(new ArrayList<>(), user.get().getId());
		messageService.deleteMessageByUserId(user.get().getId());
		userRepository.delete(user.get());
		return HttpStatus.OK;
	}
}
