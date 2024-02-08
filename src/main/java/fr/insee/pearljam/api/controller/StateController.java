package fr.insee.pearljam.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.StateService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
public class StateController {

	private final StateService stateService;
	private final UtilsService utilsService;

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

		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountDto stateCountDto;
			try {
				stateCountDto = stateService.getStateCount(userId, id, idep, date, associatedOrgUnits);
			} catch (NotFoundException e) {
				log.error(e.getMessage());
				log.info("Get interviewerStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get interviewerStateCount resulting in 200");
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
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountDto stateCountDto;
			try {
				stateCountDto = stateService.getNbSUNotAttributedStateCount(userId, id, date);
			} catch (NotFoundException e) {
				log.error(e.getMessage());
				log.info("Get state count for non attributted SUs resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get state count for non attributted SUs resulting in 200");
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
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountCampaignDto stateCountCampaignDto;
			try {
				stateCountCampaignDto = stateService.getStateCountByCampaign(userId, id, date);
			} catch (NotFoundException e) {
				log.error(e.getMessage());
				log.info("Get campaignStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get campaignStateCount resulting in 200");
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
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<StateCountDto> stateCountCampaignsDto = stateService.getStateCountByInterviewer(userId, date);
			if (stateCountCampaignsDto == null) {
				log.info("Get interviewersStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get interviewersStateCount resulting in 200");
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
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<StateCountDto> stateCountCampaignsDto = stateService.getStateCountByCampaigns(userId, date);
			if (stateCountCampaignsDto == null) {
				log.info("Get campaignStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get campaignStateCount resulting in 200");
			return new ResponseEntity<>(stateCountCampaignsDto, HttpStatus.OK);
		}
	}
}
