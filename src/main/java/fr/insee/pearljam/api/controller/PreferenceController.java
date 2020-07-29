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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class PreferenceController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceController.class);

	@Autowired
	PreferenceService preferenceService;
	
	@Autowired
	InterviewerService interviewerService;
	
	@Autowired
	UtilsService utilsService;
	
	/**
	* This method is using to update the state of Survey Units listed in request body 
	* 
	* @param commentValue the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* @throws ParseException 
	* @throws SQLException 
	* 
	*/
	@ApiOperation(value = "Update preferences with campaigns listed in request body")
	@PutMapping(path = "/preferences")
	public ResponseEntity<Object> updateSurveyUnit(HttpServletRequest request, @RequestBody List<String> listPreference) {
		String userId = utilsService.getUserId(request);
		if(StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}else {
			HttpStatus returnCode = preferenceService.setPreferences(listPreference, userId);
			LOGGER.info("PUT preferences '{}' for user {} resulting in {}", String.join(", ", listPreference) , userId, returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}
}
