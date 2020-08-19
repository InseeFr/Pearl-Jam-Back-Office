package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.configuration.ApplicationProperties;
import fr.insee.pearljam.api.configuration.ApplicationProperties.Mode;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;

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
	ApplicationProperties applicationProperties;

	public UserDto getUser(String userId) {
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		if (applicationProperties.getMode() != Mode.NoAuth) {
			Optional<User> user = userRepository.findByIdIgnoreCase(userId);
			
			OrganizationUnitDto organizationUnitsParent = new OrganizationUnitDto();
			if (user.isPresent()) {
				organizationUnitsParent.setId(user.get().getOrganizationUnit().getId());
				organizationUnitsParent.setLabel(user.get().getOrganizationUnit().getLabel());
				getOrganizationUnits(organizationUnits, user.get().getOrganizationUnit(), false);
					return new UserDto(user.get().getId(), user.get().getFirstName(), user.get().getLastName(),
							organizationUnitsParent, organizationUnits);
			} else {
				return null;
			}
		} else {
			Optional<OrganizationUnit> ouNat = ouRepository.findByIdIgnoreCase("OU-NATIONAL");
			if(ouNat.isPresent()) {
				getOrganizationUnits(organizationUnits, ouNat.get(), false);
				return new UserDto("", "Guest", "",  ouRepository.findDtoByIdIgnoreCase("OU-NATIONAL").get(), organizationUnits);
			} else {
				return new UserDto("", "Guest", "",  new OrganizationUnitDto("OU-NORTH","Guest organizational unit"), List.of());
			}
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
}
