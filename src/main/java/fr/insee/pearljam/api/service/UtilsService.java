package fr.insee.pearljam.api.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitOkNokDto;

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

	boolean isDevProfile();

	boolean isTestProfile();

	ResponseEntity<SurveyUnitOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request, List<String> id);
}
