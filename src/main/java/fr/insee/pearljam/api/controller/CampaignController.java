package fr.insee.pearljam.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.service.CampaignService;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class CampaignController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignController.class);
	
	@Autowired
	CampaignService campaignService;
	
	@Autowired
	InterviewerService interviewerService;
	
	@Autowired
	UtilsService utilsService;
	
	/**
	* This method is using to get the list of Campaigns for current user
	* @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	*/
	@ApiOperation(value = "Get Campaigns")
	@GetMapping(path = "/campaigns")
	public ResponseEntity<Object> getListCampaign(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if(StringUtils.isBlank(userId) || !utilsService.existUser(userId, "user")) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<CampaignDto> lstCampaigns = campaignService.getListCampaign(userId);
			if(lstCampaigns==null || lstCampaigns.isEmpty()){
				LOGGER.info("GET Campaign resulting in 404" );
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET Campaign resulting in 200" );
			return new ResponseEntity<>(lstCampaigns, HttpStatus.OK);
		}
	}


  /**
	* This method is used to get the list of interviewers associated with the campaign {id} for current user
	* @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	*/
	@ApiOperation(value = "Get interviewers")
	@GetMapping(path = "/campaigns/{id}/interviewers")
	public ResponseEntity<Object> getListInterviewers(HttpServletRequest request, @PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
    List<String> associatedOrgUnits = utilsService.getRelatedOrganizationUnits(userId);
		if(StringUtils.isBlank(userId) || !utilsService.existUser(userId, "user")) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<InterviewerDto> lstSurveyUnit = campaignService.getListInterviewers(userId, id, associatedOrgUnits);
			if(lstSurveyUnit==null || lstSurveyUnit.isEmpty()){
				LOGGER.info("Get interviewers resulting in 404" );
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get interviewers resulting in 200" );
			return new ResponseEntity<>(lstSurveyUnit, HttpStatus.OK);
	  }
	
  }


  /**
	* This method is using to survey units counts by state of and interviewer for a  campaign
	* @return {@link StateCountDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	*/
	@ApiOperation(value = "Get interviewerStateCount")
	@GetMapping(path = "/campaigns/{id}/survey-units/interviewer/{idep}/state-count")
	public ResponseEntity<Object> getInterviewerStateCount(HttpServletRequest request, @PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep, @RequestParam(required=false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
    List<String> associatedOrgUnits = utilsService.getRelatedOrganizationUnits(userId);
    
		if(StringUtils.isBlank(userId) || !utilsService.existUser(userId, "user")) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountDto stateCountDto = campaignService.getStateCount(userId, id, idep, date, associatedOrgUnits);
			if(stateCountDto==null){
				LOGGER.info("Get interviewerStateCount resulting in 404" );
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
      
			LOGGER.info("Get interviewerStateCount resulting in 200" );
			return new ResponseEntity<>(stateCountDto, HttpStatus.OK);
	  }
	
  }
	
	/**
	* This method is using to survey units counts by state and organizational units for a campaign
	* @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	*/
	@ApiOperation(value = "Get campaignStateCount")
	@GetMapping(path = "/campaign/{id}/survey-units/state-count")
	public ResponseEntity<Object> getCampaignStateCount(HttpServletRequest request, @PathVariable(value = "id") String id, @RequestParam(required=false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if(StringUtils.isBlank(userId) || !utilsService.existUser(userId, "user")) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountCampaignDto stateCountCampaignDto = campaignService.getStateCountByCampaign(userId, id, date);
			if(stateCountCampaignDto==null){
				LOGGER.info("Get campaignStateCount resulting in 404" );
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get campaignStateCount resulting in 200" );
			return new ResponseEntity<>(stateCountCampaignDto, HttpStatus.OK);
	  }
	
  }
}
