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

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.service.CampaignService;
import fr.insee.pearljam.api.service.InterviewerService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class CampaignController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignController.class);
	
	@Autowired
	CampaignService campaignService;
	
	@Autowired
	InterviewerService interviewerService;
	/**
	* This method is using to get the list of SurveyUnit for current user
	* @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	*/
	@ApiOperation(value = "Get SurveyUnits")
	@GetMapping(path = "/campaigns")
	public ResponseEntity<Object> getListCampaign(HttpServletRequest request) {
		String userId = interviewerService.getUserId(request);
		if(StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<CampaignDto> lstSurveyUnit = campaignService.getListCampaign(userId);
			if(lstSurveyUnit==null || lstSurveyUnit.isEmpty()){
				LOGGER.info("GET Campaign resulting in 404" );
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET Campaign resulting in 200" );
			return new ResponseEntity<>(lstSurveyUnit, HttpStatus.OK);
		}
	}

}
