package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pearljam.api.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.state.SurveyUnitStatesDto;
import fr.insee.pearljam.api.dto.surveyunit.HabilitationDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitCampaignDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitInterviewerLinkDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.SurveyUnitException;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UtilsService;
import fr.insee.pearljam.api.web.authentication.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.insee.pearljam.api.configuration.auth.AuthorityRole.autorityContainsRole;

/**
 * SurveyUnitController is the Controller managing {@link SurveyUnit}
 * entity
 * 
 * @author Claudel Benjamin
 * 
 */
@RestController
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitController {

	private static final String GUEST = "GUEST";

	private final SurveyUnitService surveyUnitService;

	private final UtilsService utilsService;
	private final AuthenticationHelper authHelper;

	public static final String GET_SURVEY_UNIT_WITH_ID = "{} : GET SurveyUnit with id {} resulting in {}";

	/**
	 * This method is used to post the list of SurveyUnit defined in request body
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "POST SurveyUnit assignations to interviewer")
	@PostMapping(path = "/survey-units")
	public ResponseEntity<Object> postSurveyUnits(Authentication auth,
			@RequestBody List<SurveyUnitContextDto> surveyUnits) {

		Response response = surveyUnitService.createSurveyUnits(surveyUnits);
		log.info("POST /survey-units resulting in {} with response [{}]", response.getHttpStatus(),
				response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}

	/**
	 * This method is used to post the list of links between suvey-unit and
	 * intervewer defined in request body
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Post SurveyUnits")
	@PostMapping(path = "/survey-units/interviewers")
	public ResponseEntity<Object> postSurveyUnitInterviewerLinks(Authentication auth,
			@RequestBody List<SurveyUnitInterviewerLinkDto> surveyUnits) {

		Response response = surveyUnitService.createSurveyUnitInterviewerLinks(surveyUnits);
		log.info("POST /survey-units/interviewers resulting in {} with response [{}]", response.getHttpStatus(),
				response.getMessage());

		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}

	/**
	 * This method is used to get the list of SurveyUnit for current interviewer
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get SurveyUnits")
	@GetMapping(path = "/survey-units")
	public ResponseEntity<List<SurveyUnitDto>> getListSurveyUnit(Authentication auth,
			@RequestParam(value = "extended", defaultValue = "false", required = false) Boolean extended) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		List<SurveyUnitDto> lstSurveyUnit = surveyUnitService.getSurveyUnitDto(userId, extended);
		if (lstSurveyUnit == null) {
			log.info("{} GET SurveyUnits resulting in 404", userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("{} GET SurveyUnits resulting in 200", userId);
		return new ResponseEntity<>(lstSurveyUnit, HttpStatus.OK);
	}

	/**
	 * This method is used to get the detail of surveyUnit for current interviewer
	 * 
	 * @param id the id of reporting unit
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get detail of specific survey unit ")
	@GetMapping(path = "/survey-unit/{id}")
	public ResponseEntity<SurveyUnitDetailDto> getSurveyUnitById(Authentication auth,
			@PathVariable(value = "id") String id) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Optional<SurveyUnit> su = surveyUnitService.findById(id);
		if (!su.isPresent()) {
			log.error("{} : Survey unit with id {} was not found in database", userId, id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (!userId.equals(GUEST) && !surveyUnitService.findByIdAndInterviewerIdIgnoreCase(id, userId).isPresent()) {
			log.error("Survey unit with id {} is not associated to the interviewer {}", id, userId);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		SurveyUnitDetailDto surveyUnit;
		try {
			surveyUnit = surveyUnitService.getSurveyUnitDetail(userId, id);
		} catch (NotFoundException | SurveyUnitException e) {
			log.error(e.getMessage());
			log.info(GET_SURVEY_UNIT_WITH_ID, userId, id, HttpStatus.NOT_FOUND);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error(e.getMessage());
			log.info(GET_SURVEY_UNIT_WITH_ID, userId, id, HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		log.info(GET_SURVEY_UNIT_WITH_ID, userId, id, HttpStatus.OK);

		return new ResponseEntity<>(surveyUnit, HttpStatus.OK);

	}

	/**
	 * This method is used to update a specific survey unit
	 * 
	 * @param request
	 * @param surveyUnitUpdated
	 * @param id
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Update the Survey Unit")
	@PutMapping(path = "/survey-unit/{id}")
	public ResponseEntity<SurveyUnitDetailDto> updateSurveyUnit(Authentication auth,
			@RequestBody SurveyUnitDetailDto surveyUnitUpdated, @PathVariable(value = "id") String id) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		ResponseEntity<SurveyUnitDetailDto> updatedSurveyUnitResponse = surveyUnitService.updateSurveyUnitDetail(userId,
				id, surveyUnitUpdated);
		HttpStatusCode returnCode = updatedSurveyUnitResponse.getStatusCode();
		log.info("{} : PUT SurveyUnit with id {} resulting in {}", userId, id, returnCode.value());
		return updatedSurveyUnitResponse;
	}

	/**
	 * This method is used to post a survey-unit by id to a temp-zone
	 */
	@Operation(summary = "Post survey-unit to temp-zone")
	@PostMapping(path = "/survey-unit/{id}/temp-zone")
	public ResponseEntity<Object> postSurveyUnitByIdInTempZone(@RequestBody JsonNode surveyUnit,
			Authentication auth, @PathVariable(value = "id") String id) {
		String userId = authHelper.getUserId(auth);
		surveyUnitService.saveSurveyUnitToTempZone(id, userId, surveyUnit);
		log.info("{} : POST survey-unit {} to temp-zone resulting in 201", userId, id);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * This method is used to retrieve survey-units in temp-zone
	 */
	@Operation(summary = "GET all survey-units in temp-zone")
	@GetMapping(path = "/survey-units/temp-zone")
	public ResponseEntity<Object> getSurveyUnitsInTempZone() {
		List<SurveyUnitTempZone> surveyUnitTempZones = surveyUnitService.getAllSurveyUnitTempZone();
		log.info("GET survey-units in temp-zone resulting in 200");
		return new ResponseEntity<>(surveyUnitTempZones, HttpStatus.OK);
	}

	/**
	 * This method is used to update the state of Survey Units listed in request
	 * body
	 * 
	 * @param request
	 * @param listSU
	 * @param state
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Update the state of Survey Units listed in request body")
	@PutMapping(path = "/survey-unit/{id}/state/{state}")
	public ResponseEntity<Object> updateSurveyUnitState(Authentication auth,
			@PathVariable(value = "id") String surveyUnitId, @PathVariable(value = "state") StateType state) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		HttpStatus returnCode = surveyUnitService.addStateToSurveyUnit(surveyUnitId, state);
		log.info("PUT state '{}' on survey unit {} resulting in {}", state.getLabel(), surveyUnitId,
				returnCode.value());
		return new ResponseEntity<>(returnCode);
	}

	/**
	 * This method closes the survey unit {id} with the closing cause {closingCause}
	 * Updates the closing cause if the SU is already closed
	 * 
	 * @param request
	 * @param id
	 * @param closingCause
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Closes a survey unit")
	@PutMapping(path = "/survey-unit/{id}/close/{closingCause}")
	public ResponseEntity<Object> closeSurveyUnit(Authentication auth,
			@PathVariable(value = "id") String surveyUnitId,
			@PathVariable(value = "closingCause") ClosingCauseType closingCause) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		log.info("{} : PUT close with cause '{}' on su {}", userId, closingCause, surveyUnitId);
		HttpStatus returnCode = surveyUnitService.closeSurveyUnit(surveyUnitId, closingCause);
		log.info("PUT close with cause '{}' on su {} resulting in {}", closingCause, surveyUnitId,
				returnCode.value());
		return new ResponseEntity<>(returnCode);
	}

	/**
	 * This method adds or updates the closing cause of the survey unit {id}
	 * but does not modify its state
	 * 
	 * @param request
	 * @param id
	 * @param closingCause
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Add Closing cause")
	@PutMapping(path = "/survey-unit/{id}/closing-cause/{closingCause}")
	public ResponseEntity<Object> updateClosingCause(Authentication auth,
			@PathVariable(value = "id") String surveyUnitId,
			@PathVariable(value = "closingCause") ClosingCauseType closingCause) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		HttpStatus returnCode = surveyUnitService.updateClosingCause(surveyUnitId, closingCause);
		log.info("PUT close with cause '{}' on su {} resulting in {}", closingCause, surveyUnitId,
				returnCode.value());
		return new ResponseEntity<>(returnCode);
	}

	/**
	 * This method is used to update the comment of a Survey Unit
	 * 
	 * @param request
	 * @param listSU
	 * @param state
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Update the state of Survey Units listed in request body")
	@PutMapping(path = "/survey-unit/{id}/comment")
	public ResponseEntity<Object> updateSurveyUnitComment(Authentication auth,
			@RequestBody CommentDto comment, @PathVariable(value = "id") String surveyUnitId) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		HttpStatus returnCode = surveyUnitService.updateSurveyUnitComment(userId, surveyUnitId, comment);
		log.info("PUT comment on su {} resulting in {}", surveyUnitId, returnCode.value());
		return new ResponseEntity<>(returnCode);
	}

	@Operation(summary = "Update the state of Survey Units listed in request body")
	@PutMapping(path = "/survey-unit/{id}/viewed")
	public ResponseEntity<Object> updateSurveyUnitViewed(Authentication auth,
			@PathVariable(value = "id") String surveyUnitId) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		HttpStatus returnCode = surveyUnitService.updateSurveyUnitViewed(userId, surveyUnitId);
		log.info("PUT viewed on su {} resulting in {}", surveyUnitId, returnCode.value());
		return new ResponseEntity<>(returnCode);
	}

	/**
	 * This method is used to get survey units of a specific campaign
	 * 
	 * @param request
	 * @param id
	 * @param state
	 * @return list of {@link SurveyUnitCampaignDto} if exists, else
	 *         {@link HttpStatus} FORBIDDEN or NOT_FOUND
	 */
	@Operation(summary = "Get Survey Units in target campaign")
	@GetMapping(path = "/campaign/{id}/survey-units")
	public ResponseEntity<Set<SurveyUnitCampaignDto>> getSurveyUnitByCampaignId(Authentication auth,
			@PathVariable(value = "id") String id, @RequestParam(value = "state", required = false) String state) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Set<SurveyUnitCampaignDto> surveyUnit = surveyUnitService.getSurveyUnitByCampaign(id, userId, state);
		if (surveyUnit == null) {
			log.info("{} : GET SurveyUnit with id {} resulting in 404", userId, id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("{} : GET SurveyUnit with id {} resulting in 200", userId, id);
		return new ResponseEntity<>(surveyUnit, HttpStatus.OK);
	}

	/**
	 * This method is used to check if a user has access to an SU
	 */
	@Operation(summary = "Check habilitation")
	@GetMapping(path = "/check-habilitation")
	public ResponseEntity<HabilitationDto> checkHabilitation(Authentication auth,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "role", required = false) String role) {
		String userId = authHelper.getUserId(auth);
		HabilitationDto resp = new HabilitationDto();
		resp.setHabilitated(false);

		if (role == null) {
			log.info(
					"Check habilitation of {} without role for accessing survey-unit {} is denied. Please provide a role in request.",
					userId, id);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		}
		if (autorityContainsRole(auth, AuthorityRoleEnum.ADMIN)) {
			resp.setHabilitated(true);
			log.info(
					"Check habilitation of {} as {} for accessing survey-unit {} resulted in {} : Admin habilitation override",
					userId,
					role.isBlank() ? "interviewer" : role, id, resp.isHabilitated());
			return new ResponseEntity<>(resp, HttpStatus.OK);
		}
		if (role.isBlank()) {
			// interviewer
			boolean checkdataBase = surveyUnitService.checkHabilitationInterviewer(userId, id);
			boolean checkToken = autorityContainsRole(auth, AuthorityRoleEnum.INTERVIEWER);
			resp.setHabilitated(checkdataBase && checkToken);
		} else if (role.equals(Constants.REVIEWER)) {
			// local or national user
			boolean checkdataBase = surveyUnitService.checkHabilitationReviewer(userId, id);
			boolean checkToken = autorityContainsRole(auth, AuthorityRoleEnum.LOCAL_USER)
					|| autorityContainsRole(auth, AuthorityRoleEnum.NATIONAL_USER);
			resp.setHabilitated(checkdataBase && checkToken);
		}
		log.info("Check habilitation of {} as {} for accessing survey-unit {} resulted in {}", userId,
				role.isBlank() ? "interviewer" : role, id, resp.isHabilitated());
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	/**
	 * This method is used to get the list of states for a specific survey unit
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link StateDto} if exists, else {@link HttpStatus} FORBIDDEN
	 *         or NOT_FOUND
	 */
	@Operation(summary = "Get states of given survey unit")
	@GetMapping(path = "/survey-unit/{id}/states")
	public ResponseEntity<SurveyUnitStatesDto> getStatesBySurveyUnitId(Authentication auth,
			@PathVariable(value = "id") String id) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			log.info("GET states of surveyUnit {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		log.info("GET states of surveyUnit {} resulting in 403", id);
		List<StateDto> lstState = surveyUnitService.getListStatesBySurveyUnitId(id);
		if (lstState.isEmpty()) {
			log.info("GET states of surveyUnit {} resulting in 404", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new SurveyUnitStatesDto(id, lstState), HttpStatus.OK);
	}

	/**
	 * This method returns the list of states for a specific survey unit
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link StateDto} if exists, else {@link HttpStatus} FORBIDDEN
	 *         or NOT_FOUND
	 */
	@Operation(summary = "Get closable survey units")
	@GetMapping(path = "/survey-units/closable")
	public ResponseEntity<List<SurveyUnitCampaignDto>> getClosableSurveyUnits(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);

		log.info("{} try to GET closable units", userId);

		if (StringUtils.isBlank(userId)) {
			log.info("GET closable survey units resulting in 401");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		List<SurveyUnitCampaignDto> lstSu = surveyUnitService.getClosableSurveyUnits(request, userId);
		log.info("GET closable survey units resulting in 200");
		return new ResponseEntity<>(lstSu, HttpStatus.OK);
	}

	/**
	 * This method is used to delete a survey-unit
	 * 
	 * @param id the id of survey-unit
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Delete survey-unit")
	@DeleteMapping(path = "/survey-unit/{id}")

	public ResponseEntity<Object> deleteSurveyUnit(Authentication auth, @PathVariable(value = "id") String id) {
		String userId = authHelper.getUserId(auth);
		log.info("{} try to DELETE survey-unit {}", userId, id);

		Optional<SurveyUnit> surveyUnitOptional = surveyUnitService.findById(id);
		if (!surveyUnitOptional.isPresent()) {
			log.error("DELETE survey-unit with id {} resulting in 404 because it does not exists", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		surveyUnitService.delete(surveyUnitOptional.get());
		log.info("DELETE survey-unit with id {} resulting in 200", id);
		return ResponseEntity.ok().build();
	}

	/**
	 * This method returns the list of all survey-unit ids
	 * 
	 * @param request
	 * @return List of {@link String}
	 */
	@Operation(summary = "Get survey units id")
	@GetMapping(path = "/admin/survey-units")
	public ResponseEntity<List<String>> getAllSurveyUnitsId(Authentication auth) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			log.info("GET admin survey units resulting in 401");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		List<String> suIds = surveyUnitService.getAllIds();
		log.info("{} : GET admin survey units resulting in 200", userId);
		return new ResponseEntity<>(suIds, HttpStatus.OK);
	}

	/**
	 * This method returns the list of all survey-unit ids for specified campaign
	 * 
	 * @param request
	 * @param id      the id of campaign
	 * @return List of {@link String}
	 */
	@Operation(summary = "Get survey units id by campaign")
	@GetMapping(path = "/admin/campaign/{id}/survey-units")
	public ResponseEntity<List<String>> getAllSurveyUnitsIdByCampaignId(Authentication auth,
			@PathVariable(value = "id") String id) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			log.info("GET admin survey units for campaign {} resulting in 401", id);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		List<String> suIds = surveyUnitService.getAllIdsByCampaignId(id);
		log.info("GET admin survey units for campaign {} resulting in 200", id);
		return new ResponseEntity<>(suIds, HttpStatus.OK);
	}
}
