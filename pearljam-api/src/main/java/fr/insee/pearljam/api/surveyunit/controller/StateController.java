package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.campaign.controller.EndpointDisabledException;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountDto;
import fr.insee.pearljam.domain.organizationunit.port.in.RelatedOrganizationUnitService;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;
import fr.insee.pearljam.domain.surveyunit.port.in.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "07. State-count", description = "Endpoints for state counts")
@Slf4j
@RequiredArgsConstructor
public class StateController {

  private final StateService stateService;
  private final RelatedOrganizationUnitService relatedOrganizationUnitService;
  private final AuthenticatedUserService authenticatedUserService;

  @Value("${feature.deprecated.endpoints.enabled}")
  private final boolean deprecatedEndpointsEnabled;

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
  @GetMapping(Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT)
  public StateCountDto getInterviewerStateCount(
      @PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
      @RequestParam(required = false, name = "date") Long date) throws CampaignNotFoundException {
    String userId = authenticatedUserService.getCurrentUserId();
    List<String> associatedOrgUnits = relatedOrganizationUnitService.getRelatedOrganizationUnits(userId);

    return stateService.getStateCount(userId, id, idep, date, associatedOrgUnits);

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
  @GetMapping(Constants.API_CAMPAIGN_ID_INTERVIEWERS_STATECOUNT)
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
  @GetMapping(Constants.API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT)
  public ResponseEntity<StateCountDto> getNbSUNotAttributedStateCount(
      @PathVariable(value = "id") String id,
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    StateCountDto stateCountDto;
    try {
      stateCountDto = stateService.getNbSUNotAttributedStateCount(userId, id, date);
    } catch (CampaignNotFoundException e) {
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
  @Deprecated(forRemoval = true)
  @Operation(summary = "Get campaignStateCount")
  @GetMapping(Constants.API_CAMPAIGN_ID_SU_STATECOUNT)
  public ResponseEntity<StateCountCampaignDto> getCampaignStateCount(
      @PathVariable(value = "id") String id,
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    StateCountCampaignDto stateCountCampaignDto;
    try {
      stateCountCampaignDto = stateService.getStateCountByCampaign(userId, id, date);
    } catch (EntityNotFoundException e) {
      log.error(e.getMessage());
      log.info("Get campaignStateCount resulting in 404");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    log.info("Get campaignStateCount resulting in 200");
    return new ResponseEntity<>(stateCountCampaignDto, HttpStatus.OK);
  }

  /**
   * @deprecated
   * Return the sum of survey units states by interviewer as a list
   *
   * @param date
   * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or
   * {@link HttpStatus} FORBIDDEN
   */
  @Operation(summary = "Get interviewersStateCount")
  @GetMapping(Constants.API_INTERVIEWERS_SU_STATECOUNT)
  @Deprecated(forRemoval = true)
  public ResponseEntity<List<StateCountDto>> getInterviewersStateCount(
      @RequestParam(required = false, name = "date") Long date) {
    if(!deprecatedEndpointsEnabled) {
      throw new EndpointDisabledException();
    }
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
   * @deprecated
   * Return the sum of survey units states by campaign as a list
   *
   * @param date
   * @return {@link StateCountCampaignDto} if exist, {@link HttpStatus} NOT_FOUND, or
   * {@link HttpStatus} FORBIDDEN
   */
  @Operation(summary = "Get campaignStateCount")
  @Deprecated(forRemoval = true)
  @GetMapping(Constants.API_CAMPAIGNS_SU_STATECOUNT)
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
