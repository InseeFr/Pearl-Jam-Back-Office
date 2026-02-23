package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
	 * @param request
	 * @param id
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND,
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
	 * This method is used to get the list of interviewers associated with the
	 * campaign {id} for current user
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get interviewers")
	@GetMapping(path = Constants.API_INTERVIEWERS)
	public ResponseEntity<Set<InterviewerDto>> getListInterviewers() {
		String userId = authenticatedUserService.getCurrentUserId();
		Set<InterviewerDto> lstInterviewer = interviewerService.getListInterviewers(userId);
		if (lstInterviewer == null) {
			log.info("Get interviewers resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("Get interviewers resulting in 200");
		return new ResponseEntity<>(lstInterviewer, HttpStatus.OK);

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
	public ResponseEntity<List<InterviewerContextDto>> getCompleteListInterviewers() {
		String userId = authenticatedUserService.getCurrentUserId();
		List<InterviewerContextDto> lstInterviewer = interviewerService.getCompleteListInterviewers();
		if (lstInterviewer.isEmpty()) {
			log.info("{} -> Get all interviewers resulting in 404 : no interviewers", userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("{} -> Get all interviewers resulting in 200", userId);
		return new ResponseEntity<>(lstInterviewer, HttpStatus.OK);

	}

	@Operation(summary = "Get interviewer campaigns")
	@GetMapping(path = Constants.API_INTERVIEWER_ID_CAMPAIGNS)
	public ResponseEntity<List<CampaignDto>> getListCampaigns(@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
		Optional<List<CampaignDto>> list = interviewerService.findCampaignsOfInterviewer(id);
		if (list.isEmpty()) {
			log.info("{} -> Get interviewer campaigns resulting in 404", userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("{} -> Get interviewers campaigns resulting in 200", userId);
		return new ResponseEntity<>(list.get(), HttpStatus.OK);

	}

	@Operation(summary = "Update interviewer")
	@PutMapping(path = Constants.API_INTERVIEWER_ID)
	public ResponseEntity<InterviewerContextDto> updateInterviewer(
			@PathVariable(value = "id") String id, 
			@RequestBody InterviewerContextDto interviewer) {
		String userId = authenticatedUserService.getCurrentUserId();

		if (id == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		Optional<InterviewerContextDto> updatedInterviewer = interviewerService.update(id, interviewer);
		if (updatedInterviewer.isEmpty()) {
			log.error("{} : UPDATE interviewer {} resulting in 404. ", userId, id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		log.info("{} : UPDATE interviewer {} resulting in 200", userId, id);
		return new ResponseEntity<>(updatedInterviewer.get(), HttpStatus.OK);

	}

	@Operation(summary = "Delete interviewer")
	@DeleteMapping(path = Constants.API_INTERVIEWER_ID)
	public ResponseEntity<Object> deleteInterviewer(@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();

		if (id == null) {
			log.warn("{} : no interviewerId provided : resulting in 400.", userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		boolean wasPresent = interviewerService.delete(id);
		if (!wasPresent) {
			log.warn("{} : DELETE interviewer with id {} resulting in 404.", userId, id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		log.info("{} : DELETE interviewer with id {} resulting in 200", userId, id);
		return ResponseEntity.ok().build();

	}

}
