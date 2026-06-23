package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.contracts.campaign.dto.output.CampaignVisibilityPeriodDto;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import fr.insee.pearljam.domain.shared.model.Response;
import fr.insee.pearljam.domain.surveyunit.port.in.InterviewerService;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "04. Interviewers", description = "Endpoints for interviewers")
@Slf4j
@RequiredArgsConstructor
public class InterviewerController {

	private final InterviewerService interviewerService;
	private final AuthenticatedUserService authenticatedUserService;

	/**
	 * This method is used to post the list of interviewers defined in request body
	 * 
	 * @return List of {@link InterviewerDB} if exists, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Post interviewers")
	@PostMapping(path = Constants.API_INTERVIEWERS)
	public ResponseEntity<String> postInterviewers(@RequestBody List<InterviewerContextDto> interviewers) {
		String userId = authenticatedUserService.getCurrentUserId();
		Response response = interviewerService.createInterviewers(interviewers);
		log.info("{} : POST /interviewers resulting in {} with response [{}]", userId, response.getHttpStatus(),
				response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}

	/**
	 * This method returns the list of interviewers related to current user
	 *
	 * @return Set of {@link InterviewerDB} (can be empty),
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get interviewers")
	@GetMapping(path = Constants.API_INTERVIEWERS)
	public List<InterviewerDto> getListInterviewers() {

		return interviewerService.getInterviewersForCurrentUser();
	}

	@Operation(summary = "Get interviewer by Id")
	@GetMapping(path = Constants.API_INTERVIEWER_ID)
	public ResponseEntity<InterviewerContextDto> getInterviewer(@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
		Optional<InterviewerContextDto> interviewer = interviewerService.findDtoById(id);
		if (interviewer.isEmpty()) {
			log.info("{} -> Get interviewer [{}] resulting in 404", userId, id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("{} -> Get interviewer [{}] resulting in 200", userId, id);
		return new ResponseEntity<>(interviewer.get(), HttpStatus.OK);

	}

	@Operation(summary = "Get all interviewers")
	@GetMapping(path = Constants.API_ADMIN_INTERVIEWERS)
	public List<InterviewerContextDto> getCompleteListInterviewers() {
		return interviewerService.getCompleteListInterviewers();
	}

	@Operation(summary = "Get interviewer campaigns")
	@GetMapping(path = Constants.API_INTERVIEWER_ID_CAMPAIGNS)
	public List<CampaignVisibilityPeriodDto> getListCampaigns(@PathVariable(value = "id") String id) {
		return  interviewerService.findCampaignsOfInterviewer(id);
	}

	@Operation(summary = "Update interviewer")
	@PutMapping(path = Constants.API_INTERVIEWER_ID)
	public InterviewerContextDto updateInterviewer(
			@PathVariable(value = "id") @NotBlank String id,
			@RequestBody InterviewerContextDto interviewer) {
		return interviewerService.update(id, interviewer);
	}

	@Operation(summary = "Delete interviewer")
	@DeleteMapping(path = Constants.API_INTERVIEWER_ID)
	public void deleteInterviewer(@PathVariable(value = "id") String id) {
		interviewerService.delete(id);
	}

	/**
	 * This method is used to get the list of interviewers associated with the
	 * campaign {id} for current user
	 *
	 * @param id campaign id
	 * @return List of {@link InterviewerDB} if exists, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@Deprecated(forRemoval = true)
	@Operation(summary = "Get interviewers for target campaign")
	@GetMapping(path = Constants.API_CAMPAIGN_ID_INTERVIEWERS)
	public List<InterviewerDto> getInterviewersByCampaignForCurrentUser(@PathVariable(value = "id") String id)throws CampaignNotFoundException {
		return interviewerService.getInterviewersByUserAndCampaign(id);
	}

}
