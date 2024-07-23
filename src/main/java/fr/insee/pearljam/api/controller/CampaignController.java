package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.api.campaign.dto.output.visibility.VisibilityCampaignDto;
import fr.insee.pearljam.api.campaign.dto.input.visibility.VisibilityUpdateDto;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
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

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.OngoingDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.domain.exception.CampaignAlreadyExistException;
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
			throws CampaignAlreadyExistException,
			CampaignNotFoundException, OrganizationalUnitNotFoundException {
		campaignService.createCampaign(campaignDto);
	}

	/**
	 * This method is used to get the list of Campaigns for current user
	 * 
	 * @return List of {@link CampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get user related Campaigns")
	@GetMapping(path = Constants.API_CAMPAIGNS)
	public ResponseEntity<List<CampaignDto>> getListCampaign() {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("User {} : GET related campaigns", userId);
		List<CampaignDto> lstCampaigns = campaignService.getListCampaign(userId);
		log.info("User {} -> {} related campaigns found", userId, lstCampaigns.size());
		return new ResponseEntity<>(lstCampaigns, HttpStatus.OK);
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
	 * @param id
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
	 * This method returns the list of visibilities associated with the
	 * campaign {id}
	 * 
	 * @param campaignId campaign id
	 * @return List of {@link VisibilityCampaignDto}
	 */
	@Operation(summary = "Get campaign visibilities")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_VISIBILITIES)
	public List<VisibilityCampaignDto> getVisibilities(@PathVariable(value = "id") String campaignId) throws CampaignNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to get campaign[{}] visibilities ", userId, campaignId);

		List<Visibility> visibilities = campaignService.findAllVisibilitiesByCampaign(campaignId);
		return VisibilityCampaignDto.fromModel(visibilities);
	}

	/**
	 * This method is used to count survey units that are abandoned by campaign
	 * Return the sum of survey units states by campaign as a list
	 *
	 * @param id
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
	 * Update the visibility dates for a given campaign and organizational unit
	 *
	 * @param visibilityToUpdate the visibility to update
	 * @param idCampaign campaign identifier
	 * @param idOu organizational unit id
	 * @throws VisibilityNotFoundException if the visibility does not exist
	 */
	@Operation(summary = "Change visibility of a campaign for an Organizational Unit")
	@PutMapping(path = Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY)
	public void updateVisibility(
			@Valid @NotNull @RequestBody VisibilityUpdateDto visibilityToUpdate,
			@NotBlank @PathVariable(value = "idCampaign") String idCampaign,
			@NotBlank @PathVariable(value = "idOu") String idOu) throws VisibilityNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to change OU[{}] visibility on campaign[{}] ", userId, idOu, idCampaign);
		campaignService.updateVisibility(VisibilityUpdateDto.toModel(visibilityToUpdate, idCampaign, idOu));
		log.info("Visibility with CampaignId {} for Organizational Unit {} updated", idCampaign,
				idOu);
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
	public ResponseEntity<Object> deleteCampaignById(
			@PathVariable(value = "id") String campaignId,
			@RequestParam(required = false, defaultValue = DEFAULT_FORCE_VALUE) Boolean force) throws CampaignNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to delete campaign {}", userId, campaignId);

		Optional<Campaign> campaignOptional = campaignService.findById(campaignId);
		if (!campaignOptional.isPresent()) {
			log.error("DELETE campaign with id {} resulting in 404 because it does not exists", campaignId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (Boolean.FALSE.equals(force) && campaignService.isCampaignOngoing(campaignId)) {
			String errorMessage = String.format("Campaign %s is on-going and can't be deleted", campaignId);
			log.info(errorMessage);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
		}

		campaignService.delete(campaignOptional.get());
		log.info("DELETE campaign with id {} resulting in 200", campaignId);
		return ResponseEntity.ok().build();
	}

	/**
	 * Updates the collection start and end dates for a campaign
	 * @param id campaign id
	 * @param campaign campaign to update
	 */
	@Operation(summary = "Update campaign (label, email, configurations, visibilities")
	@PutMapping(path = Constants.API_CAMPAIGN_ID)
	public void updateCampaign(@NotBlank @PathVariable(value = "id") String id,
			@Valid @NotNull @RequestBody CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to update campaign {} collection dates", userId, id);

		campaignService.updateCampaign(id, campaign);
		log.info("Campaign with id {} updated", id);

	}

	/**
	 * This method returns campaign ongoing status
	 * 
	 * @param id
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
	 * This method returns target campaign
	 * 
	 * @param campaignId campaign id
	 * @return {@link CampaignResponseDto} the campaign
	 */
	@Operation(summary = "Get target campaign")
	@GetMapping(path = Constants.API_CAMPAIGN_ID)
	public CampaignResponseDto getCampaign(@NotBlank @PathVariable(value = "id") String campaignId) throws CampaignNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to GET {}", userId, campaignId);
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

}
