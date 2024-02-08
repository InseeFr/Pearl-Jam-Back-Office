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

import fr.insee.pearljam.api.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.ClosingCauseService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api")
@Slf4j
public class ClosingCauseController {

	@Autowired
	ClosingCauseService closingCauseService;

	@Autowired
	UtilsService utilsService;

	/**
	 * This method is used to count survey units by states, interviewer and campaign
	 * 
	 * @param request
	 * @param id
	 * @param idep
	 * @param date
	 * @return {@link StateCountDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get interviewerStateCount")
	@GetMapping(path = "/campaign/{id}/survey-units/interviewer/{idep}/closing-causes")
	public ResponseEntity<ClosingCauseCountDto> getClosingCauseCount(HttpServletRequest request,
			@PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
			@RequestParam(required = false, name = "date") Long date) {
		String userId = utilsService.getUserId(request);
		List<String> associatedOrgUnits = utilsService.getRelatedOrganizationUnits(userId);

		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			ClosingCauseCountDto closingCountDto;
			try {
				closingCountDto = closingCauseService.getClosingCauseCount(userId, id, idep, date, associatedOrgUnits);
			} catch (NotFoundException e) {
				log.error(e.getMessage());
				log.info("Get ClosingCauseCount resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("Get ClosingCauseCount resulting in 200");
			return new ResponseEntity<>(closingCountDto, HttpStatus.OK);
		}

	}
}
