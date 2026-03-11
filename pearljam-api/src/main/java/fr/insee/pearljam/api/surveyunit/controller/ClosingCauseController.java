package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.api.surveyunit.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.surveyunit.port.in.ClosingCauseService;
import fr.insee.pearljam.domain.organizationunit.port.in.RelatedOrganizationUnitService;
import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "06. Closing causes", description = "Endpoints for closing causes")
@Slf4j
@RequiredArgsConstructor
public class ClosingCauseController {

  private final ClosingCauseService closingCauseService;
  private final RelatedOrganizationUnitService relatedOrganizationUnitService;
  private final AuthenticatedUserService authenticatedUserService;

  /**
   * This method is used to count survey units by states, interviewer and campaign
   *
   * @param id
   * @param idep
   * @param date
   * @return {@link ClosingCauseCountDto} if exist, {@link HttpStatus} NOT_FOUND, or
   * {@link HttpStatus} FORBIDDEN
   */
  @Operation(summary = "Get interviewerStateCount")
  @GetMapping(Constants.API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES)
  public ClosingCauseCountDto getClosingCauseCount(
      @PathVariable(value = "id") String id, @PathVariable(value = "idep") String idep,
      @RequestParam(required = false, name = "date") Long date) throws CampaignNotFoundException {
    String userId = authenticatedUserService.getCurrentUserId();
    List<String> associatedOrgUnits = relatedOrganizationUnitService.getRelatedOrganizationUnits(userId);

      return closingCauseService.getClosingCauseCount(userId, id, idep, date,
          associatedOrgUnits);
  }
}
