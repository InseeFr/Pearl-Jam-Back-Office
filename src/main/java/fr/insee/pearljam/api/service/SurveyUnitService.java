package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitCampaignDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;

/**
 * Service for the SurveyUnit entity
 * @author scorcaud
 *
 */
public interface SurveyUnitService {

	/**
	 * Retrieve the SurveyUnitDetail entity by Id and UserId
	 * @param userId
	 * @param id
	 * @return SurveyUnitDetailDto
	 */
	SurveyUnitDetailDto getSurveyUnitDetail(String userId, String id);
	
	/**
	 * Retrieve all the SurveyUnit entity by userId
	 * @param userId
	 * @return List of SurveyUnitDto
	 */
	List<SurveyUnitDto> getSurveyUnitDto(String userId);

	/**
	 * Update the SurveyUnit by Id and UserId with the SurveyUnitDetailDto passed in parameter
	 * @param userId
	 * @param id
	 * @param surveyUnitDetailDto
	 * @return HttpStatus
	 */
	HttpStatus updateSurveyUnitDetail(String userId, String id, SurveyUnitDetailDto surveyUnitDetailDto);
	
	List<SurveyUnitCampaignDto> getSurveyUnitByCampaign(String userId, String id, String state);

}
