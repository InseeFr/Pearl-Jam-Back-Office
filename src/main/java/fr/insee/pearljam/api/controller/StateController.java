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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.service.StateService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class StateController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StateController.class);

	@Autowired
	StateService stateService;

	@Autowired
	UtilsService utilsService;

	/**
	 * This method is used to count survey units by states, interviewer and campaign
	 * 
	 * @param request
	 * @param id
	 * @param idep
	 * @param date
	 * @return {@link StateCountDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get interviewerStateCount")
	@GetMapping(path = "/campaign/{id}/survey-units/interviewer/{idep}/state-count")
	public ResponseEntity<StateCountDto> getInterviewerStateCount(HttpServletRequest request,
			@PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
			@RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		List<String> associatedOrgUnits = utilsService.getRelatedOrganizationUnits(userId);

		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
      StateCountDto stateCountDto = stateService.getStateCount(userId, id, idep, date, associatedOrgUnits);
			if (stateCountDto == null) {
				LOGGER.info("Get interviewerStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get interviewerStateCount resulting in 200");
			return new ResponseEntity<>(stateCountDto, HttpStatus.OK);
		}

	}
  
  	/**
	 * This method is used to count survey units not attributed by states
	 * 
	 * @param request
	 * @param id
	 * @return {@link StateCountDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get state count for non attributted SUs")
	@GetMapping(path = "/campaign/{id}/survey-units/not-attributed/state-count")
	public ResponseEntity<StateCountDto> getNbSUNotAttributedStateCount(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountDto stateCountDto = stateService.getNbSUNotAttributedStateCount(userId, id, date);
			if (stateCountDto == null) {
				LOGGER.info("Get state count for non attributted SUs resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get state count for non attributted SUs resulting in 200");
			return new ResponseEntity<>(stateCountDto, HttpStatus.OK);
		}
	}

	/**
	 * This method is used to count survey units by states, organizational units and
	 * campaign
	 * 
	 * @param request
	 * @param id
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get campaignStateCount")
	@GetMapping(path = "/campaign/{id}/survey-units/state-count")
	public ResponseEntity<StateCountCampaignDto> getCampaignStateCount(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountCampaignDto stateCountCampaignDto = stateService.getStateCountByCampaign(userId, id, date);
			if (stateCountCampaignDto == null) {
				LOGGER.info("Get campaignStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get campaignStateCount resulting in 200");
			return new ResponseEntity<>(stateCountCampaignDto, HttpStatus.OK);
		}
	}

	/**
	 * Return the sum of survey units states by interviewer as a list
	 * 
	 * @param request
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get interviewersStateCount")
	@GetMapping(path = "/interviewers/survey-units/state-count")
	public ResponseEntity<List<StateCountDto>> getInterviewersStateCount(HttpServletRequest request,
			@RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<StateCountDto> stateCountCampaignsDto = stateService.getStateCountByInterviewer(userId, date);
			if (stateCountCampaignsDto == null) {
				LOGGER.info("Get interviewersStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get interviewersStateCount resulting in 200");
			return new ResponseEntity<>(stateCountCampaignsDto, HttpStatus.OK);
		}
	}

	/**
	 * Return the sum of survey units states by campaign as a list
	 * 
	 * @param request
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get campaignStateCount")
	@GetMapping(path = "/campaigns/survey-units/state-count")
	public ResponseEntity<List<StateCountDto>> getCampaignsStateCount(HttpServletRequest request,
			@RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<StateCountDto> stateCountCampaignsDto = stateService.getStateCountByCampaigns(userId, date);
			if (stateCountCampaignsDto == null) {
				LOGGER.info("Get campaignStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get campaignStateCount resulting in 200");
			return new ResponseEntity<>(stateCountCampaignsDto, HttpStatus.OK);
		}
	}
}
