package fr.insee.pearljam.domain.organizationunit.port.in;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import fr.insee.pearljam.api.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UtilsService {

	/**
	 * This method retrieves the organizationUnit of the user as well as all of its children units as a list of String
	 * @param userId user id
	 * @return {@link List} of {@link String}
	 */
	List<String> getRelatedOrganizationUnits(String userId);

	ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request, Set<String> id);
}
