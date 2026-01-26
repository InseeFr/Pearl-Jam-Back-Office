package fr.insee.pearljam.api.controller;

import java.util.List;

import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.api.dto.campaign.*;
import fr.insee.pearljam.domain.exception.*;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.CampaignService;
import fr.insee.pearljam.api.service.ReferentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.insee.pearljam.api.constants.Constants;

@RestController
@Tag(name = "01. Campaigns", description = "Endpoints for campaigns")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CampaignController {

	private final CampaignService campaignService;
	private final ReferentService referentService;
	private final AuthenticatedUserService authenticatedUserService;

	private static final String DEFAULT_FORCE_VALUE = "false";

	/**
	 * This method is used to create a campaign
	 * @param campaignDto campaign to create
	 */
	@Operation(summary = "Create a Campaign")
	@PostMapping(path = Constants.API_CAMPAIGN)
	public void createCampaign(@Valid @NotNull @RequestBody CampaignCreateDto campaignDto)
            throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException {
		campaignService.createCampaign(campaignDto);
	}

	/**
	 * This method is used to get the list of preferred Campaigns for current user
	 *
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get user preferred Campaigns")
	@GetMapping(path = Constants.API_CAMPAIGNS)
	public List<CampaignDto> getUserPreferredCampaigns() {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("User {} : GET preferred campaigns", userId);
		List<CampaignDto> lstCampaigns = campaignService.getPreferredCampaigns(userId);
		log.info("User {} -> {} preferred campaigns found", userId, lstCampaigns.size());
		return lstCampaigns;
	}

	/**
	 * This method is used to get the list of Campaigns for current user
	 *
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get user related Campaigns")
	@GetMapping(path = Constants.API_CAMPAIGNS_PREFERENCES)
	public List<CampaignPreferenceDto> getUserCampaigns() {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("User {} : GET related campaigns", userId);
		List<CampaignPreferenceDto> lstCampaigns = campaignService.getCampaignPreferences(userId);
		log.info("User {} -> {} related campaigns found", userId, lstCampaigns.size());
		return lstCampaigns;
	}

	/**
	 * This method return the list of all Campaigns
	 * 
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get Campaigns")
	@GetMapping(path = Constants.API_ADMIN_CAMPAIGNS)
	public ResponseEntity<List<CampaignDto>> getAllCampaigns() {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("User {} : GET all campaigns", userId);
		List<CampaignDto> lstCampaigns = campaignService.getAllCampaigns();
		log.info("User {}, GET all campaigns ({} campaigns found) resulting in 200", userId,
				lstCampaigns.size());
		return new ResponseEntity<>(lstCampaigns, HttpStatus.OK);

	}

	/**
	 * This method return the list of Campaigns for current interviewer
	 * 
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get interviewer related Campaigns")
	@GetMapping(path = Constants.API_INTERVIEWER_CAMPAIGNS)
	public ResponseEntity<List<CampaignDto>> getInterviewerCampaigns() {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("Interviewer {} : GET related campaigns", userId);
		List<CampaignDto> lstCampaigns = campaignService.getInterviewerCampaigns(userId);
		log.info("Interviewer {} : returned {} campaigns, resulting in 200", userId, lstCampaigns.size());
		return new ResponseEntity<>(lstCampaigns, HttpStatus.OK);

	}

	/**
	 * This method is used to get the list of interviewers associated with the
	 * campaign {id} for current user
	 * 
	 * @param id campaign id
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get interviewers")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_INTERVIEWERS)
	public ResponseEntity<List<InterviewerDto>> getListInterviewers(@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
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
	 * This method is used to count survey units that are abandoned by campaign
	 * Return the sum of survey units states by campaign as a list
	 *
	 * @param id campaign id
	 * @return
	 */
	@Operation(summary = "Get numberSUAbandoned")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_SU_ABANDONED)
	public ResponseEntity<CountDto> getNbSUAbandoned(@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
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
	 * @param campaignId campaign id
	 * @return
	 */
	@Operation(summary = "Get numberSUNotAttributed")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED)
	public ResponseEntity<CountDto> getNbSUNotAttributed(@PathVariable(value = "id") String campaignId) {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to get campaign[{}] not attributed survey-units ", userId, campaignId);
		CountDto nbSUNotAttributed;
		try {
			nbSUNotAttributed = campaignService.getNbSUNotAttributedByCampaign(userId, campaignId);
		} catch (NotFoundException e) {
			log.info("Get numberSUAbandoned resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("Get numberSUAbandoned resulting in 200");
		return new ResponseEntity<>(nbSUNotAttributed, HttpStatus.OK);

	}

	/**
	 * This method deletes a campaign
	 * 
	 * @param campaignId the value to delete
	 * @return {@link HttpStatus}
	 * 
	 */
	@Operation(summary = "Delete a campaign")
	@DeleteMapping(path = Constants.API_CAMPAIGN_ID)
	public void deleteCampaignById(
			@NotBlank @PathVariable(value = "id")
			String campaignId,
			@RequestParam(required = false, defaultValue = DEFAULT_FORCE_VALUE)
			boolean force)
			throws CampaignNotFoundException, CampaignOnGoingException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to delete campaign {}", userId, campaignId);

		campaignService.delete(campaignId, force);
		log.info("DELETE campaign with id {} resulting in 200", campaignId);
	}

	/**
	 * Updates the collection start and end dates for a campaign
	 * @param id campaign id
	 * @param campaign campaign to update
	 */
	@Operation(summary = "Update campaign (label, email, configurations, visibilities, communication-informations, referents)")
	@PutMapping(path = Constants.API_CAMPAIGN_ID)
	public void updateCampaign(@NotBlank @PathVariable(value = "id") String id,
			@Valid @NotNull @RequestBody CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException, OrganizationalUnitNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to update campaign {} collection dates", userId, id);

		campaignService.updateCampaign(id, campaign);
		log.info("Campaign with id {} updated", id);
	}

	/**
	 * This method returns campaign ongoing status
	 * 
	 * @param id campaign id
	 * @return {@link OngoingDto} , {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Check if campaign is on-going")
	@GetMapping(path = Constants.API_CAMPAIGNS_ID_ON_GOING)
	public ResponseEntity<OngoingDto> isOngoing(@PathVariable(value = "id") String id) throws CampaignNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} check if {} is on-going", userId, id);

		if (!campaignService.findById(id).isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		OngoingDto campaignOngoing = new OngoingDto();
		campaignOngoing.setOngoing(campaignService.isCampaignOngoing(id));

		log.info("{} checked if campaign {} is on-going : {}", userId, id, campaignOngoing.isOngoing());
		return new ResponseEntity<>(campaignOngoing, HttpStatus.OK);
	}

	/**
	 * This method returns campaign ongoing
	 *
	 * @return {@link CampaignSensitivityDto} the campaign
	 */
	@Operation(summary = "get ongoing sensitive campaigns")
	@GetMapping(value = Constants.API_CAMPAIGNS_ON_GOING, produces = "application/json")
	public List<CampaignSensitivityDto> getCampaignSensitivityDto() throws CampaignNotFoundException {
		return campaignService.getCampaignSensitivityDto();
	}

	/**
	 * This method returns target campaign
	 * 
	 * @param campaignId campaign id
	 * @return {@link CampaignResponseDto} the campaign
	 */
	@Operation(summary = "Get target campaign")
	@GetMapping(path = {Constants.API_CAMPAIGN_ID, Constants.API_CAMPAIGNS_ID})
	public CampaignResponseDto getCampaign(@NotBlank @PathVariable(value = "id") String campaignId) throws CampaignNotFoundException {
		return campaignService.getCampaignDtoById(campaignId);
	}

	// API for REFERENT entity

	@Operation(summary = "Get referents of targeted campaign")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_REFERENTS)
	public ResponseEntity<List<ReferentDto>> getReferents(@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to GET {} referents", userId, id);
		if (!campaignService.findById(id).isPresent()) {
			log.warn("Campaign {} is not present, can't get referents", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<ReferentDto> referents = referentService.findByCampaignId(id);

		log.info("{}  GOT {} referents for campaign {}", userId, referents.size(), id);

		return new ResponseEntity<>(referents, HttpStatus.OK);
	}

	@Operation(summary = "Get commons campaign")
	@GetMapping(value = Constants.API_CAMPAIGNS_COMMONS_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CampaignCommonsDto.class))),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public CampaignCommonsDto getCommonsCampaignsById(@PathVariable("id") String id) throws CampaignNotFoundException {
		return campaignService.findCampaignCommonsById(id);
	}

	@Operation(summary = "Get commons ongoing campaigns")
	@GetMapping(value = Constants.API_CAMPAIGNS_COMMONS_ONGOING, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CampaignCommonsDto> getCommonsOngoingCampaigns() throws CampaignNotFoundException {
		return campaignService.findCampaignsCommonsOngoing();
	}



}
