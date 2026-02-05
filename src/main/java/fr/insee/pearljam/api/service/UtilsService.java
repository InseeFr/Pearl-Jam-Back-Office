package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import fr.insee.pearljam.api.dto.surveyunit.InterrogationOkNokDto;

public interface UtilsService {

	/**
	 * @param userId
	 * @param campaignId
	 * @return {@link Boolean}
	 */
	boolean checkUserCampaignOUConstraints(String userId, String campaignId);

	/**
	 * This method retreives the organizationUnit of the user as well as all of its children units as a list of String
	 * @param userId
	 * @return {@link List} of {@link String}
	 */
	List<String> getRelatedOrganizationUnits(String userId);

	ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request, Set<String> id);
}
