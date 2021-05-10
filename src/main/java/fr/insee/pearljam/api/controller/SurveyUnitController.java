package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.state.SurveyUnitStatesDto;
import fr.insee.pearljam.api.dto.surveyunit.HabilitationDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitCampaignDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitInterviewerLinkDto;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
 * SurveyUnitController is the Controller using to manage {@link SurveyUnit}
 * entity
 * 
 * @author Claudel Benjamin
 * 
 */
@RestController
@RequestMapping(path = "/api")
public class SurveyUnitController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyUnitController.class);

	private static final String GUEST = "GUEST";

	@Autowired
	SurveyUnitService surveyUnitService;

	@Autowired
	UtilsService utilsService;

	/**
	 * This method is using to post the list of SurveyUnit defined in request body
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "POST SurveyUnit assignations to interviewer")
	@PostMapping(path = "/survey-units")
	public ResponseEntity<Object> postSurveyUnits(HttpServletRequest request, @RequestBody List<SurveyUnitContextDto> surveyUnits) {
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Response response = surveyUnitService.createSurveyUnits(surveyUnits);
		LOGGER.info("POST /survey-units resulting in {} with response [{}]", response.getHttpStatus(), response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}
	
	/**
	 * This method is using to post the list of links between suvey-unit and intervewer defined in request body
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Post SurveyUnits")
	@PostMapping(path = "/survey-units/interviewers")
	public ResponseEntity<Object> postSurveyUnitInterviewerLinks(HttpServletRequest request, @RequestBody List<SurveyUnitInterviewerLinkDto> surveyUnits) {
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Response response = surveyUnitService.createSurveyUnitInterviewerLinks(surveyUnits);
		LOGGER.info("POST /survey-units/interviewers resulting in {} with response [{}]", response.getHttpStatus(), response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}
	
	/**
	 * This method is using to get the list of SurveyUnit for current interviewer
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get SurveyUnits")
	@GetMapping(path = "/survey-units")
	public ResponseEntity<List<SurveyUnitDto>> getListSurveyUnit(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.INTERVIEWER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<SurveyUnitDto> lstSurveyUnit = surveyUnitService.getSurveyUnitDto(userId);
			if (lstSurveyUnit == null) {
				LOGGER.info("GET SurveyUnit resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET SurveyUnit resulting in 200");
			return new ResponseEntity<>(lstSurveyUnit, HttpStatus.OK);
		}
	}

	/**
	 * This method is using to get the detail of surveyUnit for current interviewer
	 * 
	 * @param id the id of reporting unit
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get detail of specific survey unit ")
	@GetMapping(path = "/survey-unit/{id}")
	public ResponseEntity<SurveyUnitDetailDto> getSurveyUnitById(HttpServletRequest request, @PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.INTERVIEWER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} 
		Optional<SurveyUnit> su = surveyUnitService.findById(id);
		if(su.isPresent()) {
			if (!userId.equals(GUEST) && !surveyUnitService.findByIdAndInterviewerIdIgnoreCase(id, userId).isPresent()) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			SurveyUnitDetailDto surveyUnit = surveyUnitService.getSurveyUnitDetail(userId, id);
			if (surveyUnit == null) {
				LOGGER.info("GET SurveyUnit with id {} resulting in 404", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				LOGGER.info("GET SurveyUnit with id {} resulting in 200", id);
				return new ResponseEntity<>(surveyUnit, HttpStatus.OK);
			}
			
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	}

	/**
	 * This method is using to update a specific survey unit
	 * 
	 * @param request
	 * @param surveyUnitUpdated
	 * @param id
	 * @return {@link HttpStatus}
	 */
	@ApiOperation(value = "Update the Survey Unit")
	@PutMapping(path = "/survey-unit/{id}")
	public ResponseEntity<Object> updateSurveyUnit(HttpServletRequest request,
			@RequestBody SurveyUnitDetailDto surveyUnitUpdated, @PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.INTERVIEWER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = surveyUnitService.updateSurveyUnitDetail(userId, id, surveyUnitUpdated);
			LOGGER.info("PUT SurveyUnit with id {} resulting in {}", id, returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}

	/**
	 * This method is using to update the state of Survey Units listed in request
	 * body
	 * 
	 * @param request
	 * @param listSU
	 * @param state
	 * @return {@link HttpStatus}
	 */
	@ApiOperation(value = "Update the state of Survey Units listed in request body")
	@PutMapping(path = "/survey-unit/{id}/state/{state}")
	public ResponseEntity<Object> updateSurveyUnitState(HttpServletRequest request,
			@PathVariable(value = "id") String surveyUnitId, @PathVariable(value = "state") StateType state) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = surveyUnitService.addStateToSurveyUnit(surveyUnitId, state);
			LOGGER.info("PUT state '{}' on following su {} resulting in {}", state.getLabel(), surveyUnitId,
					returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
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
	@ApiOperation(value = "Closes a survey unit")
	@PutMapping(path = "/survey-unit/{id}/close/{closingCause}")
	public ResponseEntity<Object> closeSurveyUnit(HttpServletRequest request,
			@PathVariable(value = "id") String surveyUnitId, @PathVariable(value = "closingCause") ClosingCauseType closingCause) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = surveyUnitService.closeSurveyUnit(surveyUnitId, closingCause);
			LOGGER.info("PUT close with cause '{}' on su {} resulting in {}", closingCause, surveyUnitId,
					returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
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
	@ApiOperation(value = "Add Closing cause")
	@PutMapping(path = "/survey-unit/{id}/closing-cause/{closingCause}")
	public ResponseEntity<Object> updateClosingCause(HttpServletRequest request,
			@PathVariable(value = "id") String surveyUnitId, @PathVariable(value = "closingCause") ClosingCauseType closingCause) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = surveyUnitService.updateClosingCause(surveyUnitId, closingCause);
			LOGGER.info("PUT close with cause '{}' on su {} resulting in {}", closingCause, surveyUnitId,
					returnCode.value());  
			return new ResponseEntity<>(returnCode);
		}
	}
	
	/**
	 * This method is used to update the comment of a Survey Unit
	 * 
	 * @param request
	 * @param listSU
	 * @param state
	 * @return {@link HttpStatus}
	 */
	@ApiOperation(value = "Update the state of Survey Units listed in request body")
	@PutMapping(path = "/survey-unit/{id}/comment")
	public ResponseEntity<Object> updateSurveyUnitComment(HttpServletRequest request,
			@RequestBody CommentDto comment, @PathVariable(value = "id") String surveyUnitId) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = surveyUnitService.updateSurveyUnitComment(userId, surveyUnitId, comment);
			LOGGER.info("PUT comment on su {} resulting in {}", surveyUnitId,
					returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}
	
	@ApiOperation(value = "Update the state of Survey Units listed in request body")
	@PutMapping(path = "/survey-unit/{id}/viewed")
	public ResponseEntity<Object> updateSurveyUnitViewed(HttpServletRequest request, @PathVariable(value = "id") String surveyUnitId) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = surveyUnitService.updateSurveyUnitViewed(userId, surveyUnitId);
			LOGGER.info("PUT viewed on su {} resulting in {}", surveyUnitId,
					returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}

	/**
	 * This method is using to get survey units of a specific campaign
	 * 
	 * @param request
	 * @param id
	 * @param state
	 * @return list of {@link SurveyUnitCampaignDto} if exists, else
	 *         {@link HttpStatus} FORBIDDEN or NOT_FOUND
	 */
	@ApiOperation(value = "Update the Survey Unit")
	@GetMapping(path = "/campaign/{id}/survey-units")
	public ResponseEntity<Set<SurveyUnitCampaignDto>> getSurveyUnitByCampaignId(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestParam(value = "state", required = false) String state) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			Set<SurveyUnitCampaignDto> surveyUnit = surveyUnitService.getSurveyUnitByCampaign(id, userId, state);
			if (surveyUnit == null) {
				LOGGER.info("GET SurveyUnit with id {} resulting in 404", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				LOGGER.info("GET SurveyUnit with id {} resulting in 200", id);
				return new ResponseEntity<>(surveyUnit, HttpStatus.OK);
			}
		}
  }
  
  /**
	 * This method is used to check if a user has access to an SU
	 */
	@ApiOperation(value = "Check habilitation")
	@GetMapping(path = "/check-habilitation")
	public ResponseEntity<HabilitationDto> getSurveyUnitByCampaignId(HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id) {
    String userId = utilsService.getUserId(request);
    HabilitationDto resp = new HabilitationDto();
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
      resp.setHabilitated(false);
			return new ResponseEntity<>(resp, HttpStatus.OK);
    } 
    
    boolean habilitated = surveyUnitService.checkHabilitation(userId, id);
    resp.setHabilitated(habilitated);
    return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	/**
	 * This method is using to get the list of states for a specific survey unit
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link StateDto} if exists, else {@link HttpStatus} FORBIDDEN
	 *         or NOT_FOUND
	 */
	@ApiOperation(value = "Get states of given survey unit")
	@GetMapping(path = "/survey-unit/{id}/states")
	public ResponseEntity<SurveyUnitStatesDto> getStatesBySurveyUnitId(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			LOGGER.info("GET states of surveyUnit {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			LOGGER.info("GET states of surveyUnit {} resulting in 403", id);
			List<StateDto> lstState = surveyUnitService.getListStatesBySurveyUnitId(id);
			if (lstState.isEmpty()) {
				LOGGER.info("GET states of surveyUnit {} resulting in 404", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(new SurveyUnitStatesDto(id, lstState), HttpStatus.OK);
		}
	}
	
	/**
	 * This method is using to get the list of states for a specific survey unit
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link StateDto} if exists, else {@link HttpStatus} FORBIDDEN
	 *         or NOT_FOUND
	 */
	@ApiOperation(value = "Get closable survey units")
	@GetMapping(path = "/survey-units/closable")
	public ResponseEntity<List<SurveyUnitCampaignDto>> getClosableSurveyUnits(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			LOGGER.info("GET closable survey units resulting in 401");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} else {
			List<SurveyUnitCampaignDto> lstSu = surveyUnitService.getClosableSurveyUnits();
			LOGGER.info("GET closable survey units resulting in 200");
			return new ResponseEntity<>(lstSu, HttpStatus.OK);
		}
	}
}