package fr.insee.pearljam.api.service;

import fr.insee.pearljam.api.dto.surveyunit.InterrogationOkNokDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UtilsService {

	/**
	 * This method retrieves the organizationUnit of the user as well as all of its children units as a list of String
	 * @param userId user id
	 * @return {@link List} of {@link String}
	 */
	List<String> getRelatedOrganizationUnits(String userId);

	ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request, List<String> id);
}
