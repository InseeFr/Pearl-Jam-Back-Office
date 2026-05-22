package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.contracts.campaign.dto.*;
import fr.insee.pearljam.contracts.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.contracts.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.contracts.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.api.campaign.dto.*;
import fr.insee.pearljam.domain.campaign.CampaignPreferenceModel;
import fr.insee.pearljam.domain.campaign.port.in.CampaignService;
import fr.insee.pearljam.domain.campaign.port.in.ReferentService;
import fr.insee.pearljam.domain.campaign.service.exception.*;
import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "01. Campaigns", description = "Endpoints for campaigns")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CampaignController {

	private final CampaignService campaignService;
	private final ReferentService referentService;
	private final AuthenticatedUserService authenticatedUserService;
	@Value("${feature.deprecated.endpoints.enabled}")
	private final boolean deprecatedEndpointsEnabled;

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
	 * @deprecated
	 * This method is used to get the list of preferred Campaigns for current user
	 *
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Deprecated(forRemoval = true)
	@Operation(summary = "Get user preferred Campaigns")
	@GetMapping(path = Constants.API_CAMPAIGNS)
	public List<CampaignDto> getUserPreferredCampaigns() {
		String userId = authenticatedUserService.getCurrentUserId();
		List<CampaignDto> lstCampaigns = campaignService.getPreferredCampaigns(userId);
		log.info("User {} -> {} preferred campaigns found", userId, lstCampaigns.size());
		return lstCampaigns;
	}

	/**
	 * This method is used to get the list of Campaigns for current user
	 * 
	 * @return List of {@link CampaignDto} if exists, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get user related Campaigns")
	@GetMapping(path = Constants.API_CAMPAIGNS_PREFERENCES)
	public List<CampaignPreferenceDto> getUserCampaigns() {
		String userId = authenticatedUserService.getCurrentUserId();
		List<CampaignPreferenceDto> lstCampaigns = campaignService.getCampaignPreferences(userId);
		log.info("User {} -> {} related campaigns found", userId, lstCampaigns.size());
		return lstCampaigns;
	}

	/**
	 * This method is used to get the list of Campaigns for current user and specific phase
	 *
	 * @return List of {@link CampaignDto} if exists, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get user related Campaigns for specific phase")
	@GetMapping(path = Constants.API_CAMPAIGNS_PREFERENCES_PHASE)
	public List<CampaignPreferenceModel> getUserCampaignsForSpecificPhase(@RequestParam CampaignPhase campaignPhase) {
		String userId = authenticatedUserService.getCurrentUserId();
		List<CampaignPreferenceModel> lstCampaigns = campaignService.getCampaignPreferencesForSpecificPhase(userId, campaignPhase);
		log.info("User {} -> {} related campaigns found for specific phase", userId, lstCampaigns.size());
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
	public List<CampaignDto> getAllCampaigns() {
		String userId = authenticatedUserService.getCurrentUserId();
		List<CampaignDto> lstCampaigns = campaignService.getAllCampaigns();
		log.info("User {}, GET all campaigns ({} campaigns found)", userId,
				lstCampaigns.size());
		return lstCampaigns;

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
	public List<CampaignDto> getInterviewerCampaigns() {
		String userId = authenticatedUserService.getCurrentUserId();
		List<CampaignDto> lstCampaigns = campaignService.getInterviewerCampaigns(userId);
		log.info("Interviewer {} : returned {} campaigns, resulting in 200", userId, lstCampaigns.size());
		return lstCampaigns;
	}

	/**
	 * This method is used to count survey units that are abandoned by campaign
	 * Return the sum of survey units states by campaign as a list
	 *
	 * @param id campaign id
	 * @return CountDto counts
	 */
	@Deprecated(forRemoval = true)
	@Operation(summary = "Get numberSUAbandoned")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_SU_ABANDONED)
	public CountDto getNbSUAbandoned(@PathVariable(value = "id") String id) throws CampaignNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		return campaignService.getNbSUAbandonedByCampaign(userId, id);

	}

	/**
	 * This method is used to count survey units that are not attributed by campaign
	 *
	 * @param campaignId campaign id
	 * @return CountDto counts
	 */
	@Deprecated(forRemoval = true)
	@Operation(summary = "Get numberSUNotAttributed")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_SU_NOTATTRIBUTED)
	public CountDto getNbSUNotAttributed(@PathVariable(value = "id") String campaignId) throws CampaignNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		return campaignService.getNbSUNotAttributedByCampaign(userId, campaignId);
	}

	/**
	 * This method deletes a campaign
	 * 
	 * @param campaignId the value to delete
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
		campaignService.delete(campaignId, force);
	}

	/**
	 * Updates the collection start and end dates for a campaign
	 * @param id campaign id
	 * @param campaign campaign to update
	 */
	@Operation(summary = "Update campaign (label, email, configurations, visibilities, communication-informations, referents)")
	@PutMapping(path = Constants.API_CAMPAIGN_ID)
	public void updateCampaign(@NotBlank @PathVariable(value = "id") String id,
							   @Valid @NotNull @RequestBody CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
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
	public OngoingDto isOngoing(@PathVariable(value = "id") String id) throws CampaignNotFoundException {
		return new OngoingDto(campaignService.isCampaignOngoing(id));
	}

	/**
	 * This method returns campaign ongoing
	 *
	 * @return {@link CampaignSensitivityDto} the campaign
	 */
	@Operation(summary = "get ongoing sensitive campaigns")
	@GetMapping(value = Constants.API_CAMPAIGNS_ON_GOING, produces = "application/json")
	public List<CampaignSensitivityDto> getCampaignSensitivityDto() {
		if(!deprecatedEndpointsEnabled) {
			throw new EndpointDisabledException();
		}
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
	@Deprecated(forRemoval = true)
	public List<ReferentDto> getReferents(@PathVariable(value = "id") String id) throws CampaignNotFoundException {
		if(!deprecatedEndpointsEnabled) {
			throw new EndpointDisabledException();
		}
		campaignService.findById(id).orElseThrow(CampaignNotFoundException::new);
		return referentService.findByCampaignId(id);
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

	@Operation(summary = "Get campaign portal data")
	@GetMapping(value = Constants.API_CAMPAIGN_ID_PORTAL_DATA, produces = MediaType.APPLICATION_JSON_VALUE)
	public PortalDataDto getPortalData(@PathVariable("id") String id) throws CampaignNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		return campaignService.findCampaignPortalData(id, userId);
	}
}
