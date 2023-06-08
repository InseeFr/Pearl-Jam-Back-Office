package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
public class InterviewerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InterviewerController.class);

	@Autowired
	InterviewerService interviewerService;

	@Autowired
	SurveyUnitService surveyUnitService;

	@Autowired
	UserService userService;

	@Autowired
	UtilsService utilsService;

	/**
	 * This method is used to post the list of interviewers defined in request body
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Post interviewers")
	@PostMapping(path = Constants.API_INTERVIEWERS)
	public ResponseEntity<String> postInterviewers(HttpServletRequest request,
			@RequestBody List<InterviewerContextDto> interviewers) {
		Response response = interviewerService.createInterviewers(interviewers);
		LOGGER.info("POST /interviewers resulting in {} with response [{}]", response.getHttpStatus(),
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
	@ApiOperation(value = "Get interviewers")
	@GetMapping(path = Constants.API_INTERVIEWERS)
	public ResponseEntity<Set<InterviewerDto>> getListInterviewers(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Set<InterviewerDto> lstInterviewer = interviewerService.getListInterviewers(userId);
		if (lstInterviewer == null) {
			LOGGER.info("Get interviewers resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		LOGGER.info("Get interviewers resulting in 200");
		return new ResponseEntity<>(lstInterviewer, HttpStatus.OK);

	}

	@ApiOperation(value = "Get interviewer campaigns")
	@GetMapping(path = Constants.API_INTERVIEWER_ID_CAMPAIGNS)
	public ResponseEntity<List<CampaignDto>> getListCampaigns(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		List<CampaignDto> list;
		try {
			list = interviewerService.findCampaignsOfInterviewer(id);
		} catch (NotFoundException e) {
			LOGGER.error(e.getMessage());
			LOGGER.info("{} -> Get interviewer campaigns resulting in 404", userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		LOGGER.info("{} -> Get interviewers campaigns resulting in 200", userId);
		return new ResponseEntity<>(list, HttpStatus.OK);

	}

	@ApiOperation(value = "Update interviewer")
	@PutMapping(path = Constants.API_INTERVIEWER_ID)
	public ResponseEntity<InterviewerDto> updateInterviewer(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestBody InterviewerContextDto interviewer) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId))
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);

		if (id == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		InterviewerDto updatedInterviewer;
		try {
			updatedInterviewer = interviewerService.update(id, interviewer);
		} catch (NotFoundException e) {
			LOGGER.error("{} : UPDATE interviewer {} resulting in 404. => {}", userId, id, e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		LOGGER.info("{} : UPDATE interviewer {} resulting in 200", userId, id);
		return new ResponseEntity<>(updatedInterviewer, HttpStatus.OK);

	}

	@ApiOperation(value = "Delete interviewer")
	@DeleteMapping(path = Constants.API_INTERVIEWER_ID)
	public ResponseEntity<Object> deleteInterviewer(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			LOGGER.warn("{} : DELETE interviewer with id {} resulting in 403.", userId, id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		if (id == null) {
			LOGGER.warn("{} : no interviewerId provided : resulting in 400.", userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		try {
			interviewerService.delete(id);
		} catch (NotFoundException e) {
			LOGGER.warn("{} : DELETE interviewer with id {} resulting in 404. => {}", userId, id, e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		LOGGER.info("{} : DELETE interviewer with id {} resulting in 200", userId, id);
		return ResponseEntity.ok().build();

	}

}
