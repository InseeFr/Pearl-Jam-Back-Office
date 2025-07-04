package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.ClosingCauseService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "06. Closing causes", description = "Endpoints for closing causes")
@Slf4j
@RequiredArgsConstructor
public class ClosingCauseController {

  private final ClosingCauseService closingCauseService;
  private final UtilsService utilsService;
  private final AuthenticatedUserService authenticatedUserService;

  /**
   * This method is used to count survey units by states, interviewer and campaign
   *
   * @param request
   * @param id
   * @param idep
   * @param date
   * @return {@link ClosingCauseCountDto} if exist, {@link HttpStatus} NOT_FOUND, or
   * {@link HttpStatus} FORBIDDEN
   */
  @Operation(summary = "Get interviewerStateCount")
  @GetMapping(Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES)
  public ResponseEntity<ClosingCauseCountDto> getClosingCauseCount(
      @PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
      @RequestParam(required = false, name = "date") Long date) {
    String userId = authenticatedUserService.getCurrentUserId();
    List<String> associatedOrgUnits = utilsService.getRelatedOrganizationUnits(userId);

    ClosingCauseCountDto closingCountDto;
    try {
      closingCountDto = closingCauseService.getClosingCauseCount(userId, id, idep, date,
          associatedOrgUnits);
    } catch (NotFoundException e) {
      log.error(e.getMessage());
      log.info("Get ClosingCauseCount resulting in 404");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    log.info("Get ClosingCauseCount resulting in 200");
    return new ResponseEntity<>(closingCountDto, HttpStatus.OK);
  }
}
