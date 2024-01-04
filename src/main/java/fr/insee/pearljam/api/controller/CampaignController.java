package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignContextDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.OngoingDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.VisibilityException;
import fr.insee.pearljam.api.service.CampaignService;
import fr.insee.pearljam.api.service.ReferentService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.insee.pearljam.api.constants.Constants;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CampaignController {

	private final CampaignService campaignService;

	private final UtilsService utilsService;

	private final ReferentService referentService;

	private static final String NO_USER_ID = "No userId : access denied.";
	private static final String DEFAULT_FORCE_VALUE = "false";

	/**
	 * This method is used to post the campaign defined in request body
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Post Campaign")
	@PostMapping(path = Constants.API_CAMPAIGN)
	public ResponseEntity<Object> postCampaign(HttpServletRequest request,
			@RequestBody CampaignContextDto campaignDto) {
		Response response;
		try {
			response = campaignService.postCampaign(campaignDto);
		} catch (NoOrganizationUnitException | VisibilityException e) {
			log.error(e.getMessage());
			response = new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		log.info("POST /campaign resulting in {} with response [{}]", response.getHttpStatus(),
				response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}

	/**
	 * This method is used to get the list of Campaigns for current user
	 * 
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get user related Campaigns")
	@GetMapping(path = Constants.API_CAMPAIGNS)
	public ResponseEntity<List<CampaignDto>> getListCampaign(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		log.info("User {} : GET related campaigns", userId);
		if (StringUtils.isBlank(userId)) {
			log.warn(NO_USER_ID);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<CampaignDto> lstCampaigns = campaignService.getListCampaign(userId);
			log.info("User {} -> {} related campaigns found", userId, lstCampaigns.size());
			return new ResponseEntity<>(lstCampaigns, HttpStatus.OK);
		}
	}

	/**
	 * This method return the list of all Campaigns
	 * 
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get Campaigns")
	@GetMapping(path = Constants.API_ADMIN_CAMPAIGNS)
	public ResponseEntity<List<CampaignDto>> getAllCampaigns(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		log.info("User {} : GET all campaigns", userId);
		if (StringUtils.isBlank(userId)) {
			log.warn(NO_USER_ID);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<CampaignDto> lstCampaigns = campaignService.getAllCampaigns();
			log.info("User {}, GET all campaigns ({} campaigns found) resulting in 200", userId,
					lstCampaigns.size());
			return new ResponseEntity<>(lstCampaigns, HttpStatus.OK);
		}
	}

	/**
	 * This method return the list of Campaigns for current interviewer
	 * 
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get interviewer related Campaigns")
	@GetMapping(path = Constants.API_INTERVIEWER_CAMPAIGNS)
	public ResponseEntity<List<CampaignDto>> getInterviewerCampaigns(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			log.warn(NO_USER_ID);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		log.info("Interviewer {} : GET related campaigns", userId);
		List<CampaignDto> lstCampaigns = campaignService.getInterviewerCampaigns(userId);
		log.info("Interviewer {} : returned {} campaigns, resulting in 200", userId, lstCampaigns.size());
		return new ResponseEntity<>(lstCampaigns, HttpStatus.OK);

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
	@GetMapping(path = Constants.API_CAMPAIGN_ID_INTERVIEWERS)
	public ResponseEntity<List<InterviewerDto>> getListInterviewers(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			log.warn(NO_USER_ID);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		log.info("{} try to get campaign[{}] interviewers ", userId, id);
		List<InterviewerDto> lstInterviewer;
		try {
			lstInterviewer = campaignService.getListInterviewers(userId, id);
		} catch (NotFoundException e) {
			log.error(e.getMessage());
			log.info("Get interviewers resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		log.info("Get interviewers resulting in 200");
		return new ResponseEntity<>(lstInterviewer, HttpStatus.OK);

	}

	/**
	 * This method returns the list of visibilities associated with the
	 * campaign {id}
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link VisibilityContextDto} if exist, {@link HttpStatus}
	 *         NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get campaign visibilities")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_VISIBILITIES)
	public ResponseEntity<List<VisibilityContextDto>> getVisibilities(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			log.warn(NO_USER_ID);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		log.info("{} try to get campaign[{}] visibilities ", userId, id);
		if (!campaignService.findById(id).isPresent()) {
			log.warn("Can't find visibilities : campaign {} is missing", id);
			return ResponseEntity.notFound().build();
		}

		List<VisibilityContextDto> visibilities = campaignService.findAllVisiblitiesByCampaign(id);

		log.info("Get visibilities resulting in 200");
		return new ResponseEntity<>(visibilities, HttpStatus.OK);

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
	@GetMapping(path = Constants.API_CAMPAIGN_ID_SU_ABANDONED)
	public ResponseEntity<CountDto> getNbSUAbandoned(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			log.warn(NO_USER_ID);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		log.info("{} try to get campaign[{}] abandoned survey-units ", userId, id);
		CountDto nbSUAbandoned;
		try {
			nbSUAbandoned = campaignService.getNbSUAbandonedByCampaign(userId, id);
		} catch (NotFoundException e) {
			log.info("Get numberSUAbandoned resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("Get numberSUAbandoned resulting in 200");
		return new ResponseEntity<>(nbSUAbandoned, HttpStatus.OK);

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
	@GetMapping(path = Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED)
	public ResponseEntity<CountDto> getNbSUNotAttributed(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String callerId = utilsService.getUserId(request);
		if (StringUtils.isBlank(callerId)) {
			log.warn(NO_USER_ID);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		log.info("{} try to get campaign[{}] not attributed survey-units ", callerId, id);
		CountDto nbSUNotAttributed;
		try {
			nbSUNotAttributed = campaignService.getNbSUNotAttributedByCampaign(callerId, id);
		} catch (NotFoundException e) {
			log.info("Get numberSUAbandoned resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("Get numberSUAbandoned resulting in 200");
		return new ResponseEntity<>(nbSUNotAttributed, HttpStatus.OK);

	}

	/**
	 * Update the visibility dates for a given campaign and organizational unit
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Change visibility of a campaign for an Organizational Unit")
	@PutMapping(path = Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY)
	public ResponseEntity<Object> putVisibilityDate(HttpServletRequest request,
			@RequestBody VisibilityDto visibilityUpdated, @PathVariable(value = "idCampaign") String idCampaign,
			@PathVariable(value = "idOu") String idOu) {
		String callerId = utilsService.getUserId(request);
		if (StringUtils.isBlank(callerId)) {
			log.info(NO_USER_ID);
			return new ResponseEntity<>(NO_USER_ID, HttpStatus.FORBIDDEN);
		}
		log.info("{} try to change OU[{}] visibility on campaign[{}] ", callerId, idOu, idCampaign);
		HttpStatus returnCode = campaignService.updateVisibility(idCampaign, idOu, visibilityUpdated);
		log.info("PUT visibility with CampaignId {} for Organizational Unit {} resulting in {}", idCampaign,
				idOu, returnCode.value());
		return new ResponseEntity<>(returnCode);

	}

	/**
	 * This method deletes a campaign
	 * 
	 * @param campaign the value to delete
	 * @return {@link HttpStatus}
	 * 
	 */
	@ApiOperation(value = "Delete a campaign")
	@DeleteMapping(path = Constants.API_CAMPAIGN_ID)
	public ResponseEntity<Object> deleteCampaignById(HttpServletRequest request, @PathVariable(value = "id") String id,
			@RequestParam(required = false, defaultValue = DEFAULT_FORCE_VALUE) Boolean force) {
		String callerId = utilsService.getUserId(request);
		log.info("{} try to delete campaign {}", callerId, id);

		Optional<Campaign> campaignOptional = campaignService.findById(id);
		if (!campaignOptional.isPresent()) {
			log.error("DELETE campaign with id {} resulting in 404 because it does not exists", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (Boolean.FALSE.equals(force) && campaignService.isCampaignOngoing(id)) {
			String errorMessage = String.format("Campaign %s is on-going and can't be deleted", id);
			log.info(errorMessage);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
		}

		campaignService.delete(campaignOptional.get());
		log.info("DELETE campaign with id {} resulting in 200", id);
		return ResponseEntity.ok().build();
	}

	/**
	 * Updates the collection start and end dates for a campaign
	 * 
	 * @body CampaignDto
	 * @param id
	 * @return {@link HttpStatus}
	 */
	@ApiOperation(value = "Update campaign (label, email, configurations, visibilities")
	@PutMapping(path = Constants.API_CAMPAIGN_ID)
	public ResponseEntity<Object> putCampaign(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestBody CampaignContextDto campaign) {
		String callerId = utilsService.getUserId(request);
		log.info("{} try to update campaign {} collection dates", callerId, id);

		if (StringUtils.isBlank(callerId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		HttpStatus returnCode = campaignService.updateCampaign(id, campaign);
		log.info("PUT campaign with id {} resulting in {}", id, returnCode.value());
		return new ResponseEntity<>(returnCode);

	}

	/**
	 * This method returns campaign ongoing status
	 * 
	 * @param request
	 * @param id
	 * @return {@link OngoingDto} , {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Check if campaign is on-going")
	@GetMapping(path = Constants.API_CAMPAIGNS_ID_ON_GOING)
	public ResponseEntity<OngoingDto> isOngoing(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String callerId = utilsService.getUserId(request);
		log.info("{} check if {} is on-going", callerId, id);

		if (!campaignService.findById(id).isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		OngoingDto campaignOngoing = new OngoingDto();
		campaignOngoing.setOngoing(campaignService.isCampaignOngoing(id));

		log.info("{} checked if campaign {} is on-going : {}", callerId, id, campaignOngoing.isOngoing());
		return new ResponseEntity<>(campaignOngoing, HttpStatus.OK);
	}

	/**
	 * This method returns target campaign
	 * 
	 * @param request
	 * @param id
	 * @return {@link CampaignContextDto} , {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get target campaign")
	@GetMapping(path = Constants.API_CAMPAIGN_ID)
	public ResponseEntity<CampaignContextDto> getCampaign(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String callerId = utilsService.getUserId(request);
		log.info("{} try to GET {}", callerId, id);

		if (!campaignService.findById(id).isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		CampaignContextDto campaign = campaignService.getCampaignDtoById(id);

		log.info("{} GET campaign {} : {}", callerId, id, HttpStatus.OK.toString());
		return new ResponseEntity<>(campaign, HttpStatus.OK);
	}

	// API for REFERENT entity

	@ApiOperation(value = "Get referents of targeted campaign")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_REFERENTS)
	public ResponseEntity<List<ReferentDto>> getReferents(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String callerId = utilsService.getUserId(request);
		log.info("{} try to GET {} referents", callerId, id);
		if (!campaignService.findById(id).isPresent()) {
			log.warn("Campaign {} is not present, can't get referents", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<ReferentDto> referents = referentService.findByCampaignId(id);

		log.info("{}  GOT {} referents for campaign {}", callerId, referents.size(), id);

		return new ResponseEntity<>(referents, HttpStatus.OK);
	}

}
