package fr.insee.pearljam.api.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.projection.SurveyUnitCampaign;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
* SurveyUnitController is the Controller using to manage {@link SurveyUnit} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class SurveyUnitController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyUnitController.class);

	@Autowired
	SurveyUnitService surveyUnitService;
	
	@Autowired
	InterviewerService interviewerService;
	
	@Autowired
	SurveyUnitRepository surveyUnitRepository;
	
	@Autowired
	UtilsService utilsService;
	
	/**
	* This method is using to get the list of SurveyUnit for current user
	* @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	*/
	@ApiOperation(value = "Get SurveyUnits")
	@GetMapping(path = "/survey-units")
	public ResponseEntity<Object> getListSurveyUnit(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if(StringUtils.isBlank(userId) || !utilsService.existUser(userId, "interviewer")) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<SurveyUnitDto> lstSurveyUnit = surveyUnitService.getSurveyUnitDto(userId);
			if(lstSurveyUnit==null || lstSurveyUnit.isEmpty()){
				LOGGER.info("GET SurveyUnit resulting in 404" );
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET SurveyUnit resulting in 200" );
			return new ResponseEntity<>(lstSurveyUnit, HttpStatus.OK);
		}
	}
	
	
	/**
	* This method is using to get the list of SurveyUnit for current user
	* @param id	the id of reporting unit
	* @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	*/
	@ApiOperation(value = "Get detail of specific survey unit ")
	@GetMapping(path = "/survey-unit/{id}")
	public ResponseEntity<Object>  getSurveyUnitById(HttpServletRequest request ,@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if(StringUtils.isBlank(userId) || !utilsService.existUser(userId, "interviewer")) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}else {
			SurveyUnitDetailDto surveyUnit = surveyUnitService.getSurveyUnitDetail(userId, id);
			if (surveyUnit==null) {
				LOGGER.info("GET SurveyUnit with id {} resulting in 404", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				LOGGER.info("GET SurveyUnit with id {} resulting in 200", id);
				return new ResponseEntity<>(surveyUnit, HttpStatus.OK);
			}
		}
	}
	
	/**
	* This method is using to update the comment associated to a specific reporting unit 
	* 
	* @param commentValue the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* @throws ParseException 
	* @throws SQLException 
	* 
	*/
	@ApiOperation(value = "Update the Survey Unit")
	@PutMapping(path = "/survey-unit/{id}")
	public ResponseEntity<Object> updateSurveyUnit(HttpServletRequest request, @RequestBody SurveyUnitDetailDto surveyUnitUpdated, @PathVariable(value = "id") String id) throws SQLException {
		String userId = utilsService.getUserId(request);
		if(StringUtils.isBlank(userId) || !utilsService.existUser(userId, "interviewer")) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}else {
			HttpStatus returnCode = surveyUnitService.updateSurveyUnitDetail(userId, id, surveyUnitUpdated);
			LOGGER.info("PUT SurveyUnit with id {} resulting in {}", id, returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}
	
	/**
	* This method is using to update the comment associated to a specific reporting unit 
	* 
	* @param commentValue the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* @throws ParseException 
	* @throws SQLException 
	* 
	*/
	@ApiOperation(value = "Update the Survey Unit")
	@GetMapping(path = "/campaign/{id}/survey-units")
	public ResponseEntity<Object> getSurveyUnitByCampaignId(HttpServletRequest request, @PathVariable(value = "id") String id) throws SQLException {
		String userId = utilsService.getUserId(request);
		if(StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}else {
			List<SurveyUnitCampaign> surveyUnit = surveyUnitService.getSurveyUnitByCampaign(id, userId);
			if (surveyUnit==null) {
				LOGGER.info("GET SurveyUnit with id {} resulting in 404", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				LOGGER.info("GET SurveyUnit with id {} resulting in 200", id);
				return new ResponseEntity<>(surveyUnit, HttpStatus.OK);
			}
		}
	}
}
