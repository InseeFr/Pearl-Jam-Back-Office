package fr.insee.pearljam.api.controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
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
	 * This method is used to get the list of interviewers associated with the
	 * campaign {id} for current user
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get interviewers")
	@GetMapping(path = "/interviewers")
	public ResponseEntity<Object> getListInterviewers(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<String> lstOuId = userService.getUserOUs(userId, true).stream().map(OrganizationUnitDto::getId)
					.collect(Collectors.toList());
			Set<InterviewerDto> lstInterviewer = surveyUnitService.getSurveyUnitIdByOrganizationUnits(lstOuId).stream()
					.map(SurveyUnit::getInterviewer)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet())
					.stream()
					.map(intw -> new InterviewerDto(intw))
					.collect(Collectors.toSet());

			if (lstInterviewer == null) {
				LOGGER.info("Get interviewers resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get interviewers resulting in 200");
			return new ResponseEntity<>(lstInterviewer, HttpStatus.OK);
		}
	}
	
	/**
	 * This method is used to get the contact outcome type count associated with the
	 * campaign {id} for current an interviewer
	 * @param request
	 * @param id
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get contact-outcome type for an interviewer on a specific campaign")
	@GetMapping(path = "/campaign/{id}/survey-units/interviewer/{idep}/contact-outcomes")
	public ResponseEntity<Object> getContactOuctomeByCampaignAndInterviewer(HttpServletRequest request,
			@PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
			@RequestParam(required = false, name = "date") Long date) {
        String userId = utilsService.getUserId(request);
        if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
          return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
        ContactOutcomeTypeCountDto cotd = interviewerService.getContactOutcomeByInterviewerAndCampaign(userId, id, idep, date);
        if (cotd == null) {
          LOGGER.info("Get contactOutcomeTypeCount resulting in 404");
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Get contactOutcomeTypeCount resulting in 200");
        return new ResponseEntity<>(cotd, HttpStatus.OK);
      }
    }

    
	@ApiOperation(value = "Get interviewer campaigns")
	@GetMapping(path = "/interviewer/{id}/campaigns")
	public ResponseEntity<Object> getListCampaigns(HttpServletRequest request, @PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, Constants.USER)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<CampaignDto> list = interviewerService.findCampaignsOfInterviewer(id);
			if (list == null) {
				LOGGER.info("Get interviewer campaigns resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("Get interviewers campaigns resulting in 200");
			return new ResponseEntity<>(list, HttpStatus.OK);
		}
	}

}
