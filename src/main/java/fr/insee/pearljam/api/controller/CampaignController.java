package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignContextDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CollectionDatesDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.VisibilityException;
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
	 * This method is used to post the campaign defined in request body
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Post Campaign")
	@PostMapping(path = "/campaign")
	public ResponseEntity<Object> postCampaign(HttpServletRequest request,
			@RequestBody CampaignContextDto campaignDto) {
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Response response;
		try {
			response = campaignService.postCampaign(campaignDto);
		} catch (NoOrganizationUnitException | VisibilityException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
			response = new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
		} 
		LOGGER.info("POST /campaign resulting in {} with response [{}]", response.getHttpStatus(), response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}
	
	/**
	 * This method is used to get the list of Campaigns for current user
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get Campaigns")
	@GetMapping(path = "/campaigns")
	public ResponseEntity<List<CampaignDto>> getListCampaign(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<CampaignDto> lstCampaigns = campaignService.getListCampaign(userId);
			if (lstCampaigns == null) {
				LOGGER.info("GET Campaign resulting in 500");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get interviewers")
	@GetMapping(path = "/campaign/{id}/interviewers")
	public ResponseEntity<List<InterviewerDto>> getListInterviewers(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<InterviewerDto> lstInterviewer;
			try {
				lstInterviewer = campaignService.getListInterviewers(userId, id);
			}
			catch(NotFoundException e) {
				LOGGER.error(e.getMessage());
				LOGGER.info("Get interviewers resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
			LOGGER.info("Get interviewers resulting in 200");
			return new ResponseEntity<>(lstInterviewer, HttpStatus.OK);
		}
	}

	/**
	 * This method is used to count survey units that are abandoned by campaign
	 * Return the sum of survey units states by campaign as a list
	 * 
	 * @param request
	 * @param id
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get numberSUAbandoned")
	@GetMapping(path = "/campaign/{id}/survey-units/abandoned")
	public ResponseEntity<CountDto> getNbSUAbandoned(HttpServletRequest request, @PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			CountDto nbSUAbandoned;
			try {
				nbSUAbandoned = campaignService.getNbSUAbandonedByCampaign(userId, id);
			}
			catch(NotFoundException e) {
				LOGGER.error(e.getMessage());
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
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get numberSUNotAttributed")
	@GetMapping(path = "/campaign/{id}/survey-units/not-attributed")
	public ResponseEntity<CountDto> getNbSUNotAttributed(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			CountDto nbSUNotAttributed;
			try {
				nbSUNotAttributed = campaignService.getNbSUNotAttributedByCampaign(userId, id);
			}
			catch(NotFoundException e) {
				LOGGER.error(e.getMessage());
				LOGGER.info("Get numberSUAbandoned resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get numberSUAbandoned resulting in 200");
			return new ResponseEntity<>(nbSUNotAttributed, HttpStatus.OK);
		}
	}

	/**
	 * Updates the collection start and end dates for a campaign
	 * 
	 * @body CampaignDto
	 * @param id
	 * @return {@link HttpStatus}
	 */
	@ApiOperation(value = "Put campaignCollectionDates")
	@PutMapping(path = "/campaign/{id}/collection-dates")
	public ResponseEntity<Object> putCampaignsCollectionDates(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestBody CollectionDatesDto campaign) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = campaignService.updateDates(userId, id, campaign);
			LOGGER.info("PUT campaignCollectionDates with id {} resulting in {}", id, returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}

	/**
	 * Update the visibility dates for a given campaign and organizational unit
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Change visibility of a campaign for an Organizational Unit")
	@PutMapping(path = "/campaign/{idCampaign}/organizational-unit/{idOu}/visibility")
	public ResponseEntity<Object> putVisibilityDate(HttpServletRequest request,
			@RequestBody VisibilityDto visibilityUpdated, @PathVariable(value = "idCampaign") String idCampaign,
			@PathVariable(value = "idOu") String idOu) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = campaignService.updateVisibility(idCampaign, idOu, visibilityUpdated);
			LOGGER.info("PUT visibility with CampaignId {} for Organizational Unit {} resulting in {}", idCampaign,
					idOu, returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}
	
	
	/**
	* This method is using to delete a campaign
	* 
	* @param campaign the value to delete
	* @return {@link HttpStatus}
	* 
	*/
	@ApiOperation(value = "Delete a campaign")
	@DeleteMapping(path = "/campaign/{id}")
	public ResponseEntity<Object> deleteCampaignById(HttpServletRequest request, @PathVariable(value = "id") String id) {
		Optional<Campaign> campaignOptional = campaignService.findById(id);
		if (!campaignOptional.isPresent()) {
			LOGGER.error("DELETE campaign with id {} resulting in 404 because it does not exists", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		campaignService.delete(campaignOptional.get());
		LOGGER.info("DELETE campaign with id {} resulting in 200", id);
		return ResponseEntity.ok().build();
	}
	
	
	/**
	 * Updates the collection start and end dates for a campaign
	 * 
	 * @body CampaignDto
	 * @param id
	 * @return {@link HttpStatus}
	 */
	@ApiOperation(value = "Put campaignCollectionDates")
	@PutMapping(path = "/campaign/{id}")
	public ResponseEntity<Object> putCampaign(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestBody CampaignContextDto campaign) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = campaignService.updateCampaign(userId, id, campaign);
			LOGGER.info("PUT campaignCollectionDates with id {} resulting in {}", id, returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}
}
