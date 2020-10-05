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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CollectionDatesDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.service.CampaignService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class CampaignController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignController.class);

	@Autowired
	CampaignService campaignService;

	@Autowired
	UtilsService utilsService;

	/**
	 * This method is used to get the list of Campaigns for current user
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get Campaigns")
	@GetMapping(path = "/campaigns")
	public ResponseEntity<Object> getListCampaign(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<CampaignDto> lstCampaigns = campaignService.getListCampaign(userId);
			if (lstCampaigns == null || lstCampaigns.isEmpty()) {
				LOGGER.info("GET Campaign resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET Campaign resulting in 200");
			return new ResponseEntity<>(lstCampaigns, HttpStatus.OK);
		}
	}

	/**
	 * This method is used to get the list of interviewers associated with the
	 * campaign {id} for current user
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get interviewers")
	@GetMapping(path = "/campaign/{id}/interviewers")
	public ResponseEntity<Object> getListInterviewers(HttpServletRequest request, @PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<InterviewerDto> lstInterviewer = campaignService.getListInterviewers(userId, id);
			if (lstInterviewer == null || lstInterviewer.isEmpty()) {
				LOGGER.info("Get interviewers resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get interviewers resulting in 200");
			return new ResponseEntity<>(lstInterviewer, HttpStatus.OK);
		}
	}

	/**
	 * This method is used to count survey units by states, interviewer and
	 * campaign
	 * 
	 * @param request
	 * @param id
	 * @param idep
	 * @param date
	 * @return {@link StateCountDto} if exist, {@link HttpStatus} NOT_FOUND, or  {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get interviewerStateCount")
	@GetMapping(path = "/campaign/{id}/survey-units/interviewer/{idep}/state-count")
	public ResponseEntity<Object> getInterviewerStateCount(HttpServletRequest request,
			@PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
			@RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		List<String> associatedOrgUnits = utilsService.getRelatedOrganizationUnits(userId);

		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountDto stateCountDto = campaignService.getStateCount(userId, id, idep, date, associatedOrgUnits);
			if (stateCountDto == null) {
				LOGGER.info("Get interviewerStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get interviewerStateCount resulting in 200");
			return new ResponseEntity<>(stateCountDto, HttpStatus.OK);
		}

	}

	/**
	 * This method is used to count survey units by states, organizational units
	 * and campaign
	 * 
	 * @param request
	 * @param id
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get campaignStateCount")
	@GetMapping(path = "/campaign/{id}/survey-units/state-count")
	public ResponseEntity<Object> getCampaignStateCount(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			StateCountCampaignDto stateCountCampaignDto = campaignService.getStateCountByCampaign(userId, id, date);
			if (stateCountCampaignDto == null) {
				LOGGER.info("Get campaignStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get campaignStateCount resulting in 200");
			return new ResponseEntity<>(stateCountCampaignDto, HttpStatus.OK);
		}
	}
	
	/**
	 * This method is used to count survey units that are abandoned by campaign
	 * Return the sum of survey units states by campaign as a list
	 * 
	 * @param request
	 * @param id
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get numberSUAbandoned")
	@GetMapping(path = "/campaign/{id}/survey-units/abandoned")
	public ResponseEntity<Object> getNbSUAbandoned(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			CountDto nbSUAbandoned = campaignService.getNbSUAbandonedByCampaign(userId, id);
			if (nbSUAbandoned == null) {
				LOGGER.info("Get numberSUAbandoned resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get numberSUAbandoned resulting in 200");
			return new ResponseEntity<>(nbSUAbandoned, HttpStatus.OK);
		}
	}
	
	/**
	 * This method is used to count survey units that are not attributed by campaign
	 * 
	 * @param request
	 * @param id
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get numberSUNotAttributed")
	@GetMapping(path = "/campaign/{id}/survey-units/not-attributed")
	public ResponseEntity<Object> getNbSUNotAttributed(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			CountDto nbSUNotAttributed = campaignService.getNbSUNotAttributedByCampaign(userId, id);
			if (nbSUNotAttributed == null) {
				LOGGER.info("Get numberSUAbandoned resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get numberSUAbandoned resulting in 200");
      return new ResponseEntity<>(nbSUNotAttributed, HttpStatus.OK);
    }
  }
      
	/**
    * Return the sum of survey units states by interviewer as a list
    * @param request
    * @param date
    * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
    */
  @ApiOperation(value = "Get interviewersStateCount")
	@GetMapping(path = "/interviewers/survey-units/state-count")
	public ResponseEntity<Object> getInterviewersStateCount(HttpServletRequest request, @RequestParam(required = false, name = "date") Long date) {
    String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
      List<StateCountDto> stateCountCampaignsDto = campaignService.getStateCountByInterviewer(userId, date);
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
  * @param request
  * @param date
  * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus} FORBIDDEN
  */
	@ApiOperation(value = "Get campaignStateCount")
	@GetMapping(path = "/campaigns/survey-units/state-count")
	public ResponseEntity<Object> getCampaignsStateCount(HttpServletRequest request, @RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<StateCountDto> stateCountCampaignsDto = campaignService.getStateCountByCampaigns(userId, date);
			if (stateCountCampaignsDto == null) {
				LOGGER.info("Get campaignStateCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get campaignStateCount resulting in 200");
			return new ResponseEntity<>(stateCountCampaignsDto, HttpStatus.OK);
		}
	}
	
  /**
  * Updates the collection start and end dates for a campaign
  * @body CampaignDto
  * @param id
  * @return {@link HttpStatus}
  */
	@ApiOperation(value = "Put campaignCollectionDates")
	@PutMapping(path = "/campaign/{id}/collection-dates")
	public ResponseEntity<Object> putCampaignsCollectionDates(HttpServletRequest request, @PathVariable(value = "id") String id, @RequestBody CollectionDatesDto campaign) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = campaignService.updateDates(userId, id, campaign);
			LOGGER.info("PUT campaignCollectionDates with id {} resulting in {}", id, returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}
	/**
	  * Update the visibility dates for a given campaign and organizational unit
	  * @param request
	  * @param idCampaign
	  * @param idOu
	  */
		@ApiOperation(value = "Change visibility of a campaign for an Organizational Unit")
		@PutMapping(path = "/campaign/{idCampaign}/organizational-unit/{idOu}/visibility")
		public ResponseEntity<Object> putVisibilityDate(HttpServletRequest request, @RequestBody VisibilityDto visibilityUpdated,
				@PathVariable(value = "idCampaign") String idCampaign, @PathVariable(value = "idOu") String idOu) {
			String userId = utilsService.getUserId(request);
			if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			} else {
				HttpStatus returnCode = campaignService.updateVisibility(idCampaign, idOu, visibilityUpdated);
				LOGGER.info("PUT visibility with CampaignId {} for Organizational Unit {} resulting in {}", idCampaign, idOu, returnCode.value());
				return new ResponseEntity<>(returnCode);
			}
		}
}
