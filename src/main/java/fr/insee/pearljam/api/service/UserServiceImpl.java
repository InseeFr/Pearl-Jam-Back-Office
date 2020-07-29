package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.configuration.ApplicationProperties;
import fr.insee.pearljam.api.configuration.ApplicationProperties.Mode;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserDto;
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
	ApplicationProperties applicationProperties;

	@Override
	public UserDto getUser(String userId) {
		if (applicationProperties.getMode() != Mode.NoAuth) {
			Optional<User> user = userRepository.findByIdIgnoreCase(userId);
			List<OrganizationUnitDto> organizationUnitsLocals = new ArrayList<>();
			OrganizationUnitDto organizationUnitsParent = new OrganizationUnitDto();
			if (user.isPresent()) {
				 getOrganizationUnits(organizationUnits, user.get().getOrganizationUnit(), false);
				 getOrganizationUnitsParents(organizationUnitsParent, user.get().getOrganizationUnit());
				 return new UserDto(user.get().getId(), user.get().getFirstName(), user.get().getLastName(),
						 organizationUnitsParent, organizationUnitsLocals);
			} else {
				return null;
			}
		} else {
			return new UserDto("", "Guest", "",  new OrganizationUnitDto(), List.of());
		}
	}

	private void getOrganizationUnits(List<OrganizationUnitDto> organizationUnits, OrganizationUnit currentOu, boolean saveAllLevels) {
		List<OrganizationUnit> lstOu = organizationUnitRepository.findChildren(currentOu.getId());
		if(lstOu.isEmpty()) {
			organizationUnitsLocals.add(new OrganizationUnitDto(currentOu.getId(), currentOu.getLabel()));
		}else {
			if(saveAllLevels) {
				organizationUnits.add(new OrganizationUnitDto(currentOu.getId(), currentOu.getLabel()));
			}
			for (OrganizationUnit ou : lstOu) {
				getOrganizationUnits(organizationUnits, ou, saveAllLevels);
			}
		}
	}
	
	private void getOrganizationUnitsParents(OrganizationUnitDto organizationUnitsParent, OrganizationUnit currentOu) {
		String idOuParent = organizationUnitRepository.findIdParent(currentOu.getId());
		OrganizationUnit parentOu = organizationUnitRepository.findOuById(idOuParent);
		organizationUnitsParent.setId(parentOu.getId());
		organizationUnitsParent.setLabel(parentOu.getLabel());
	}
}
