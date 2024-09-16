package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.Set;

import fr.insee.pearljam.api.dto.surveyunit.*;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitInterviewerResponseDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
import fr.insee.pearljam.domain.exception.EntityNotFoundException;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pearljam.api.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.domain.security.model.AuthorityRole;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.state.SurveyUnitStatesDto;
import fr.insee.pearljam.api.service.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Validated
public class SurveyUnitController {

	private final SurveyUnitService surveyUnitService;
	private final AuthenticatedUserService authenticatedUserService;

	/**
	 * This method is used to post the list of SurveyUnit defined in request body
	 * @param surveyUnits survey units to create
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Create survey-units")
	@PostMapping(path = "/survey-units")
	public ResponseEntity<Object> postSurveyUnits(@RequestBody List<SurveyUnitContextDto> surveyUnits) {
		Response response = surveyUnitService.createSurveyUnits(surveyUnits);
		log.info("POST /survey-units resulting in {} with response [{}]", response.getHttpStatus(),
				response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}

	/**
	 * This method is used to post the list of links between survey-unit and
	 * interviewer defined in request body
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Assign SurveyUnits to interviewers")
	@PostMapping(path = "/survey-units/interviewers")
	public ResponseEntity<Object> postSurveyUnitInterviewerLinks(@RequestBody List<SurveyUnitInterviewerLinkDto> surveyUnits) {
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
	public ResponseEntity<List<SurveyUnitDto>> getListSurveyUnit(
						@RequestParam(value = "extended", defaultValue = "false", required = false) Boolean extended) {
		String userId = authenticatedUserService.getCurrentUserId();
		List<SurveyUnitDto> lstSurveyUnit = surveyUnitService.getSurveyUnitDto(userId, extended);
		if (lstSurveyUnit == null) {
			log.info("{} GET SurveyUnits resulting in 404", userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("{} GET SurveyUnits resulting in 200", userId);
		return new ResponseEntity<>(lstSurveyUnit, HttpStatus.OK);
	}

	/**
	 * This method is used to get the detail of survey unit for current interviewer
	 * 
	 * @param surveyUnitId the id of reporting unit
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get detail of specific survey unit ")
	@GetMapping(path = {"/interviewer/survey-unit/{id}", "/survey-unit/{id}"})
	public SurveyUnitInterviewerResponseDto getSurveyUnitById(@PathVariable(value = "id") String surveyUnitId) {
		String userId = authenticatedUserService.getCurrentUserId();
		return surveyUnitService.getSurveyUnitInterviewerDetail(userId, surveyUnitId);
	}

	/**
	 * This method is used to update a specific survey unit
	 * @param surveyUnitUpdateDto survey unit informations to update
	 * @param id survey unit id
	 * @return {@link SurveyUnitDetailDto}
	 * @throws EntityNotFoundException exception thrown if entity not found
	 */
	@Operation(summary = "Update the Survey Unit")
	@PutMapping(path = "/survey-unit/{id}")
	public SurveyUnitDetailDto updateSurveyUnit(
			@Valid @NotNull @RequestBody SurveyUnitUpdateDto surveyUnitUpdateDto,
			@PathVariable(value = "id") String id) throws EntityNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		SurveyUnitDetailDto updatedSurveyUnit = surveyUnitService.updateSurveyUnit(userId,
				id, surveyUnitUpdateDto);
		log.info("SurveyUnit {} updated", id);
		return updatedSurveyUnit;
	}

	/**
	 * This method is used to post a survey-unit by id to a temp-zone
	 */
	@Operation(summary = "Post survey-unit to temp-zone")
	@PostMapping(path = "/survey-unit/{id}/temp-zone")
	public ResponseEntity<Object> postSurveyUnitByIdInTempZone(
			@RequestBody JsonNode surveyUnit,
			@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
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
	 * This method is used to update the state of a survey unit
	 *
	 * @param surveyUnitId survey unit id
	 * @param state state to set
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Update the state of Survey Units listed in request body")
	@PutMapping(path = "/survey-unit/{id}/state/{state}")
	public ResponseEntity<Object> updateSurveyUnitState(
			@PathVariable(value = "id") String surveyUnitId,
			@PathVariable(value = "state") StateType state) {
		HttpStatus returnCode = surveyUnitService.addStateToSurveyUnit(surveyUnitId, state);
		log.info("PUT state '{}' on survey unit {} resulting in {}", state.getLabel(), surveyUnitId,
				returnCode.value());
		return new ResponseEntity<>(returnCode);
	}

	/**
	 * This method closes the survey unit {id} with the closing cause {closingCause}
	 * Updates the closing cause if the SU is already closed
	 * 
	 * @param surveyUnitId survey unit id
	 * @param closingCause closing cause to set/update
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Closes a survey unit")
	@PutMapping(path = "/survey-unit/{id}/close/{closingCause}")
	public ResponseEntity<Object> closeSurveyUnit(
			@PathVariable(value = "id") String surveyUnitId,
			@PathVariable(value = "closingCause") ClosingCauseType closingCause) {
		String userId = authenticatedUserService.getCurrentUserId();
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
	 * @param surveyUnitId survey unit id
	 * @param closingCause closing cause to add
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Add Closing cause")
	@PutMapping(path = "/survey-unit/{id}/closing-cause/{closingCause}")
	public ResponseEntity<Object> updateClosingCause(
			@PathVariable(value = "id") String surveyUnitId,
			@PathVariable(value = "closingCause") ClosingCauseType closingCause) {
		HttpStatus returnCode = surveyUnitService.updateClosingCause(surveyUnitId, closingCause);
		log.info("PUT close with cause '{}' on su {} resulting in {}", closingCause, surveyUnitId,
				returnCode.value());
		return new ResponseEntity<>(returnCode);
	}

	@Operation(summary = "Update the state of Survey Units listed in request body")
	@PutMapping(path = "/survey-unit/{id}/viewed")
	public ResponseEntity<Object> updateSurveyUnitViewed(@PathVariable(value = "id") String surveyUnitId) {
		String userId = authenticatedUserService.getCurrentUserId();
		HttpStatus returnCode = surveyUnitService.updateSurveyUnitViewed(userId, surveyUnitId);
		log.info("PUT viewed on su {} resulting in {}", surveyUnitId, returnCode.value());
		return new ResponseEntity<>(returnCode);
	}

	/**
	 * This method is used to get survey units of a specific campaign
	 * 
	 * @param id campaign id
	 * @param state search survey unit with this state
	 * @return list of {@link SurveyUnitCampaignDto} if exists, else
	 *         {@link HttpStatus} FORBIDDEN or NOT_FOUND
	 */
	@Operation(summary = "Get Survey Units in target campaign")
	@GetMapping(path = "/campaign/{id}/survey-units")
	public ResponseEntity<Set<SurveyUnitCampaignDto>> getSurveyUnitByCampaignId(
			@PathVariable(value = "id") String id, 
			@RequestParam(value = "state", required = false) String state) {
		String userId = authenticatedUserService.getCurrentUserId();
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
	 * @param surveyUnitId survey unit id
	 * @param role role to check
	 * @return {@link HabilitationDto} the habilitation object
	 */
	@Operation(summary = "Check habilitation")
	@GetMapping(path = "/check-habilitation")
	public ResponseEntity<HabilitationDto> checkHabilitation(
			@RequestParam(value = "id") String surveyUnitId,
			@RequestParam(value = "role", required = false) String role) {

		String userId = authenticatedUserService.getCurrentUserId();
		HabilitationDto resp = new HabilitationDto();
		resp.setHabilitated(false);

		if (role == null) {
			log.info(
					"Check habilitation of {} without role for accessing survey-unit {} is denied. Please provide a role in request.",
					userId, surveyUnitId);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		}
		if (authenticatedUserService.hasRole(AuthorityRole.ADMIN)) {
			resp.setHabilitated(true);
			log.info(
					"Check habilitation of {} as {} for accessing survey-unit {} resulted in {} : Admin habilitation override",
					userId,
					role.isBlank() ? "interviewer" : role, surveyUnitId, resp.isHabilitated());
			return new ResponseEntity<>(resp, HttpStatus.OK);
		}
		if (role.isBlank()) {
			// interviewer
			boolean checkdataBase = surveyUnitService.checkHabilitationInterviewer(userId, surveyUnitId);
			boolean checkToken = authenticatedUserService.hasRole(AuthorityRole.INTERVIEWER);
			resp.setHabilitated(checkdataBase && checkToken);
		} else if (role.equals(Constants.REVIEWER)) {
			// local or national user
			boolean checkdataBase = surveyUnitService.checkHabilitationReviewer(userId, surveyUnitId);
			boolean checkToken = authenticatedUserService.hasAnyRole(AuthorityRole.LOCAL_USER, AuthorityRole.NATIONAL_USER);
			resp.setHabilitated(checkdataBase && checkToken);
		}
		log.info("Check habilitation of {} as {} for accessing survey-unit {} resulted in {}", userId,
				role.isBlank() ? "interviewer" : role, surveyUnitId, resp.isHabilitated());
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	/**
	 * This method is used to get the list of states for a specific survey unit
	 * 
	 * @param id survey unit it
	 * @return List of {@link StateDto} if exists, else {@link HttpStatus} FORBIDDEN
	 *         or NOT_FOUND
	 */
	@Operation(summary = "Get states of given survey unit")
	@GetMapping(path = "/survey-unit/{id}/states")
	public ResponseEntity<SurveyUnitStatesDto> getStatesBySurveyUnitId(
			@PathVariable(value = "id") String id) {

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
	 * @param request http servlet request
	 * @return List of {@link StateDto} if exists, else {@link HttpStatus} FORBIDDEN
	 *         or NOT_FOUND
	 */
	@Operation(summary = "Get closable survey units")
	@GetMapping(path = "/survey-units/closable")
	public ResponseEntity<List<SurveyUnitCampaignDto>> getClosableSurveyUnits(HttpServletRequest request) {

		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to GET closable units", userId);
		List<SurveyUnitCampaignDto> lstSu = surveyUnitService.getClosableSurveyUnits(request, userId);
		log.info("GET closable survey units resulting in 200");
		return new ResponseEntity<>(lstSu, HttpStatus.OK);
	}

	/**
	 * This method is used to delete a survey-unit
	 * 
	 * @param surveyUnitId the id of survey-unit
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Delete survey-unit")
	@DeleteMapping(path = "/survey-unit/{id}")
	public void deleteSurveyUnit(@PathVariable(value = "id") String surveyUnitId) {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to DELETE survey-unit {}", userId, surveyUnitId);
		surveyUnitService.delete(surveyUnitId);
		log.info("DELETE survey-unit with id {} resulting in 200", surveyUnitId);
	}

	/**
	 * This method returns the list of all survey-unit ids
	 * 
	 * @return List of {@link String}
	 */
	@Operation(summary = "Get survey units id")
	@GetMapping(path = "/admin/survey-units")
	public ResponseEntity<List<String>> getAllSurveyUnitsId() {
		String userId = authenticatedUserService.getCurrentUserId();
		List<String> suIds = surveyUnitService.getAllIds();
		log.info("{} : GET admin survey units resulting in 200", userId);
		return new ResponseEntity<>(suIds, HttpStatus.OK);
	}

	/**
	 * This method returns the list of all survey-unit ids for specified campaign
	 * 
	 * @param id      the id of campaign
	 * @return List of {@link String}
	 */
	@Operation(summary = "Get survey units id by campaign")
	@GetMapping(path = "/admin/campaign/{id}/survey-units")
	public ResponseEntity<List<String>> getAllSurveyUnitsIdByCampaignId(@PathVariable(value = "id") String id) {
		List<String> suIds = surveyUnitService.getAllIdsByCampaignId(id);
		log.info("GET admin survey units for campaign {} resulting in 200", id);
		return new ResponseEntity<>(suIds, HttpStatus.OK);
	}
}
