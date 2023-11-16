package fr.insee.pearljam.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.ContactOutcomeService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api")
@Slf4j
public class ContactOutcomeController {

	@Autowired
	ContactOutcomeService contactOutcomeService;

	@Autowired
	UtilsService utilsService;

	/**
	 * This method is used to count survey units not attributed by contact-outcomes
	 * 
	 * @param request
	 * @param id
	 * @return {@link StateCountDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get Contact-outcomes count for non attributted SUs")
	@GetMapping(path = "/campaign/{id}/survey-units/not-attributed/contact-outcomes")
	public ResponseEntity<ContactOutcomeTypeCountDto> getNbSUNotAttributedContactOutcomes(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			ContactOutcomeTypeCountDto contactOutcomes;
			try {
				contactOutcomes = contactOutcomeService.getNbSUNotAttributedContactOutcomes(userId, id, date);
			} catch (NotFoundException e) {
				log.error(e.getMessage());
				log.info("Get Contact-outcomes count for non attributted SUs resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get Contact-outcomes count for non attributted SUs resulting in 200");
			return new ResponseEntity<>(contactOutcomes, HttpStatus.OK);
		}
	}

	/**
	 * Return the contact-outcome type count
	 * for each campaign
	 * 
	 * @param request
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get campaignStateCount")
	@GetMapping(path = "/campaigns/survey-units/contact-outcomes")
	public ResponseEntity<List<ContactOutcomeTypeCountDto>> getCampaignsContactOutcomeTypeCount(
			HttpServletRequest request,
			@RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			List<ContactOutcomeTypeCountDto> listContactOutcomeTypeCountDto = contactOutcomeService
					.getContactOutcomeTypeCountByCampaign(userId, date);
			if (listContactOutcomeTypeCountDto == null) {
				log.info("Get contactOutcomeTypeCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get contactOutcomeTypeCount resulting in 200");
			return new ResponseEntity<>(listContactOutcomeTypeCountDto, HttpStatus.OK);
		}
	}

	/**
	 * This method is used to count the contact-outcome types
	 * by organizational units and campaign
	 * 
	 * @param request
	 * @param id
	 * @param date
	 * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get campaignStateCount")
	@GetMapping(path = "/campaign/{id}/survey-units/contact-outcomes")
	public ResponseEntity<ContactOutcomeTypeCountCampaignDto> getContactOutcomeTypeCountByCampaign(
			HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			ContactOutcomeTypeCountCampaignDto stateCountCampaignDto;
			try {
				stateCountCampaignDto = contactOutcomeService.getContactOutcomeCountTypeByCampaign(userId, id, date);
			} catch (NotFoundException e) {
				log.error(e.getMessage());
				log.info("Get contact-outcome type count resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get contact-outcome type count resulting in 200");
			return new ResponseEntity<>(stateCountCampaignDto, HttpStatus.OK);
		}
	}

	/**
	 * This method is used to get the contact outcome type count associated with the
	 * campaign {id} for current an interviewer
	 * 
	 * @param request
	 * @param id
	 * @return List of {@link Interviewer} if exist, {@link HttpStatus} NOT_FOUND,
	 *         or {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get contact-outcome type for an interviewer on a specific campaign")
	@GetMapping(path = "/campaign/{id}/survey-units/interviewer/{idep}/contact-outcomes")
	public ResponseEntity<ContactOutcomeTypeCountDto> getContactOuctomeByCampaignAndInterviewer(
			HttpServletRequest request,
			@PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
			@RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		ContactOutcomeTypeCountDto cotd;
		try {
			cotd = contactOutcomeService.getContactOutcomeByInterviewerAndCampaign(userId, id, idep, date);
		} catch (NotFoundException e) {
			log.error(e.getMessage());
			log.info("Get contactOutcomeTypeCount resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("Get contactOutcomeTypeCount resulting in 200");
		return new ResponseEntity<>(cotd, HttpStatus.OK);

	}

}
