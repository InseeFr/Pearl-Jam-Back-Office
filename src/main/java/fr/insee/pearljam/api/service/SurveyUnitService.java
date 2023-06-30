package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pearljam.api.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitCampaignDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitInterviewerLinkDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.SurveyUnitException;

/**
 * Service for the SurveyUnit entity
 * 
 * @author scorcaud
 *
 */
public interface SurveyUnitService {

	/**
	 * Retrieve the SurveyUnitDetail entity by Id and UserId
	 * 
	 * @param userId
	 * @param id
	 * @return {@link SurveyUnitDetailDto}
	 * @throws SurveyUnitException
	 * @throws NotFoundException
	 */
	SurveyUnitDetailDto getSurveyUnitDetail(String userId, String id) throws SurveyUnitException, NotFoundException;

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
	 * @param surveyUnitDetailDto
	 * @return {@link ResponseEntity}<{@link SurveyUnitDetailDto}>
	 */
	ResponseEntity<SurveyUnitDetailDto> updateSurveyUnitDetail(String userId, String id,
			SurveyUnitDetailDto surveyUnitDetailDto);

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

	public Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String userId, String id);

	public Optional<SurveyUnit> findById(String id);

	List<SurveyUnitCampaignDto> getClosableSurveyUnits(HttpServletRequest request, String userId);

	HttpStatus updateSurveyUnitComment(String userId, String surveyUnitId, CommentDto comment);

	HttpStatus updateSurveyUnitViewed(String userId, String surveyUnitId);

	HttpStatus closeSurveyUnit(String surveyUnitId, ClosingCauseType closingCause);

	HttpStatus updateClosingCause(String surveyUnitId, ClosingCauseType closingCause);

	List<SurveyUnit> getSurveyUnitIdByOrganizationUnits(List<String> lstOuId);

	Response createSurveyUnits(List<SurveyUnitContextDto> surveyUnits);

	Response createSurveyUnitInterviewerLinks(List<SurveyUnitInterviewerLinkDto> surveyUnitInterviewerLink);

	boolean checkHabilitationInterviewer(String userId, String id);

	boolean checkHabilitationReviewer(String userId, String id);

	void delete(SurveyUnit surveyUnit);

	void saveSurveyUnitToTempZone(String id, String userId, JsonNode surveyUnit);

	List<SurveyUnitTempZone> getAllSurveyUnitTempZone();

	boolean canBeSeenByInterviewer(String suId);

	List<String> getAllIds();

	List<String> getAllIdsByCampaignId(String campaignId);

	List<String> getAllIdsByInterviewerId(String interviewerId);

	void removeInterviewerLink(List<String> ids);
}
