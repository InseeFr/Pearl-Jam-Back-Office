package fr.insee.pearljam.surveyunit.infrastructure.rest.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.insee.pearljam.configuration.web.Constants;
import fr.insee.pearljam.security.domain.port.userside.AuthenticatedUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.Interviewer;
import fr.insee.pearljam.shared.Response;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.surveyunit.domain.port.userside.InterviewerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		if (!list.isPresent()) {
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
		if (!updatedInterviewer.isPresent()) {
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
