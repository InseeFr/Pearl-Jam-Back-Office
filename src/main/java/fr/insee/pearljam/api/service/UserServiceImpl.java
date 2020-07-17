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
			List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
			if (user.isPresent()) {
				for (OrganizationUnit ou : organizationUnitRepository
						.findChildren(user.get().getOrganizationUnit().getId())) {
					organizationUnits.add(new OrganizationUnitDto(ou.getId(), ou.getLabel()));
				}
				return new UserDto(user.get().getId(), user.get().getFirstName(), user.get().getLastName(),
						organizationUnits);
			} else {
				return null;
			}
		} else {
			return new UserDto("", "Guest", "", List.of());
		}
	}
}
