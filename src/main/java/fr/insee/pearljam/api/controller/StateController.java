package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.StateService;
import fr.insee.pearljam.api.service.UtilsService;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@Tag(name = "07. State-count", description = "Endpoints for state counts")
@Slf4j
@RequiredArgsConstructor
public class StateController {

  private final StateService stateService;
  private final UtilsService utilsService;
  private final AuthenticatedUserService authenticatedUserService;

  /**
   * This method is used to count survey units by states, interviewer and campaign
   *
   * @param id
   * @param idep
   * @param date
   * @return {@link StateCountDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus}
   * FORBIDDEN
   */
  @Operation(summary = "Get interviewerStateCount")
  @GetMapping(path = "/campaign/{id}/survey-units/interviewer/{idep}/state-count")
  public ResponseEntity<StateCountDto> getInterviewerStateCount(
      @PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    List<String> associatedOrgUnits = utilsService.getRelatedOrganizationUnits(userId);

    StateCountDto stateCountDto;
    try {
      stateCountDto = stateService.getStateCount(userId, id, idep, date, associatedOrgUnits);
    } catch (NotFoundException e) {
      log.error(e.getMessage());
      log.info("Get interviewerStateCount resulting in 404");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    log.info("Get interviewerStateCount resulting in 200");
    return new ResponseEntity<>(stateCountDto, HttpStatus.OK);
  }

  /**
   * Return the interviewer state count by campaign
   *
   * @param id
   * @param date
   * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or
   * {@link HttpStatus} FORBIDDEN
   */
  @Operation(summary = "Get interviewersStateCount")
  @GetMapping(path = "/campaign/{id}/interviewers/state-count")
  public ResponseEntity<List<StateCountDto>> getInterviewersStateCountByCampaign(
      @PathVariable(value = "id") String id,
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    List<StateCountDto> stateCountCampaignsDto = stateService.getInterviewersStateCountByCampaign(
        userId, id, date);
    if (stateCountCampaignsDto == null) {
      log.info("Get interviewersStateCount resulting in 404");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    log.info("Get interviewersStateCount resulting in 200");
    return new ResponseEntity<>(stateCountCampaignsDto, HttpStatus.OK);
  }

  /**
   * This method is used to count survey units not attributed by states
   *
   * @param id
   * @param date
   * @return {@link StateCountDto} if exist, {@link HttpStatus} NOT_FOUND, or {@link HttpStatus}
   * FORBIDDEN
   */
  @Operation(summary = "Get state count for non attributted SUs")
  @GetMapping(path = "/campaign/{id}/survey-units/not-attributed/state-count")
  public ResponseEntity<StateCountDto> getNbSUNotAttributedStateCount(
      @PathVariable(value = "id") String id,
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    StateCountDto stateCountDto;
    try {
      stateCountDto = stateService.getNbSUNotAttributedStateCount(userId, id, date);
    } catch (NotFoundException e) {
      log.error(e.getMessage());
      log.info("Get state count for non attributted SUs resulting in 404");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    log.info("Get state count for non attributted SUs resulting in 200");
    return new ResponseEntity<>(stateCountDto, HttpStatus.OK);
  }

  /**
   * This method is used to count survey units by states, organizational units and campaign
   *
   * @param id
   * @param date
   * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or
   * {@link HttpStatus} FORBIDDEN
   */
  @Operation(summary = "Get campaignStateCount")
  @GetMapping(path = "/campaign/{id}/survey-units/state-count")
  public ResponseEntity<StateCountCampaignDto> getCampaignStateCount(
      @PathVariable(value = "id") String id,
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    StateCountCampaignDto stateCountCampaignDto;
    try {
      stateCountCampaignDto = stateService.getStateCountByCampaign(userId, id, date);
    } catch (NotFoundException e) {
      log.error(e.getMessage());
      log.info("Get campaignStateCount resulting in 404");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    log.info("Get campaignStateCount resulting in 200");
    return new ResponseEntity<>(stateCountCampaignDto, HttpStatus.OK);
  }

  /**
   * Return the sum of survey units states by interviewer as a list
   *
   * @param date
   * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or
   * {@link HttpStatus} FORBIDDEN
   */
  @Operation(summary = "Get interviewersStateCount")
  @GetMapping(path = "/interviewers/survey-units/state-count")
  public ResponseEntity<List<StateCountDto>> getInterviewersStateCount(
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    List<StateCountDto> stateCountCampaignsDto = stateService.getStateCountByInterviewer(userId,
        date);
    if (stateCountCampaignsDto == null) {
      log.info("Get interviewersStateCount resulting in 404");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    log.info("Get interviewersStateCount resulting in 200");
    return new ResponseEntity<>(stateCountCampaignsDto, HttpStatus.OK);
  }

  /**
   * Return the sum of survey units states by campaign as a list
   *
   * @param date
   * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or
   * {@link HttpStatus} FORBIDDEN
   */
  @Operation(summary = "Get campaignStateCount")
  @GetMapping(path = "/campaigns/survey-units/state-count")
  public ResponseEntity<List<StateCountDto>> getCampaignsStateCount(
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    List<StateCountDto> stateCountCampaignsDto = stateService.getStateCountByCampaigns(userId,
        date);
    if (stateCountCampaignsDto == null) {
      log.info("Get campaignStateCount resulting in 404");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    log.info("Get campaignStateCount resulting in 200");
    return new ResponseEntity<>(stateCountCampaignsDto, HttpStatus.OK);
  }
}
