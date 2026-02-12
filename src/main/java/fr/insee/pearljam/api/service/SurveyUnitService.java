package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Set;

import fr.insee.pearljam.api.dto.surveyunit.*;
import fr.insee.pearljam.api.surveyunit.dto.ClosableSurveyUnitDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitCreationDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitInterviewerResponseDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
import fr.insee.pearljam.domain.exception.PersonNotFoundException;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.SurveyUnitForInterviewer;
import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pearljam.api.domain.*;
import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.dto.state.StateDto;

/**
 * Service for the SurveyUnit entity
 * 
 * @author scorcaud
 *
 */
public interface SurveyUnitService {

	SurveyUnitInterviewerResponseDto buildSurveyUnitInterviewerResponse(SurveyUnit surveyUnit);

	/**
	 * Retrieve the SurveyUnitDetail entity by Id and UserId
	 *
	 * @param userId       user id
	 * @param surveyUnitId survey unit id
	 * @return {@link SurveyUnitForInterviewer}
	 */
	SurveyUnitInterviewerResponseDto getSurveyUnitInterviewerDetail(String userId, String surveyUnitId);

	SurveyUnitInterviewerResponseDto getSurveyUnitDetail(String surveyUnitId);

	/**
	 * Retrieve all the SurveyUnit entity by userId
	 * 
	 * @param userId
	 * @return {@link List} of {@link SurveyUnitDto}
	 */
	List<SurveyUnitDto> getSurveyUnitDto(String userId, Boolean extended);

	/**
	 * Update the SurveyUnit by Id and UserId with the SurveyUnitDetailDto passed in
	 * parameter
	 * 
	 * @param userId
	 * @param id
	 * @param surveyUnitUpdateDto
	 * @return {@link SurveyUnitDetailDto}
	 */
	SurveyUnitDetailDto updateSurveyUnit(String userId, String id,
										 SurveyUnitUpdateDto surveyUnitUpdateDto) throws SurveyUnitNotFoundException, PersonNotFoundException;

	/**
	 * @param userId
	 * @param id
	 * @param state
	 * @return {@link HttpStatus}
	 */
	Set<SurveyUnitCampaignDto> getSurveyUnitByCampaign(String userId, String id, String state);

	/**
	 * @param listSU
	 * @param state
	 * @return {@link HttpStatus}
	 */
	HttpStatus addStateToSurveyUnit(String listSU, StateType state);

	/**
	 * @param suId
	 * @return {@link List} of {@link StateDto}
	 */
	List<StateDto> getListStatesBySurveyUnitId(String suId);

	/**
	 *
	 * @param surveyUnitId survey unit id
	 * @return {@link SurveyUnit} the survey unit
	 */
	SurveyUnit getSurveyUnit(String surveyUnitId);

	List<ClosableSurveyUnitDto> getClosableSurveyUnits(HttpServletRequest request, String userId);

	HttpStatus updateSurveyUnitViewed(String userId, String surveyUnitId);

	HttpStatus closeSurveyUnit(String surveyUnitId, ClosingCauseType closingCause);

	HttpStatus updateClosingCause(String surveyUnitId, ClosingCauseType closingCause);

	List<SurveyUnit> getSurveyUnitIdByOrganizationUnits(List<String> lstOuId);

	Response createSurveyUnits(List<SurveyUnitCreationDto> surveyUnits);

	Response createSurveyUnitInterviewerLinks(List<SurveyUnitInterviewerLinkDto> surveyUnitInterviewerLink);

	boolean checkHabilitationInterviewer(String userId, String id);

	boolean checkHabilitationReviewer(String userId, String id);

	void delete(String surveyUnitId);

	void saveSurveyUnitToTempZone(String id, String userId, JsonNode surveyUnit);

	List<SurveyUnitTempZone> getAllSurveyUnitTempZone();

	boolean canBeSeenByInterviewer(String suId);

	List<String> getAllIds();

	List<String> getAllIdsByCampaignId(String campaignId);

	List<String> getAllIdsByInterviewerId(String interviewerId);

	void removeInterviewerLink(List<String> ids);
}
