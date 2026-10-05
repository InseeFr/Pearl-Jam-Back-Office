package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.campaign.controller.EndpointDisabledException;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountDto;
import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import fr.insee.pearljam.domain.surveyunit.port.in.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "07. State-count", description = "Endpoints for state counts")
@Slf4j
@RequiredArgsConstructor
public class StateController {

  private final StateService stateService;
  private final AuthenticatedUserService authenticatedUserService;

  @Value("${feature.deprecated.endpoints.enabled}")
  private final boolean deprecatedEndpointsEnabled;

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
}
