package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.service.StateService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.campaign.port.serverside.VisibilityRepository;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationTemplateJpaRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the Service for the Interviewer entity
 *
 * @author scorcaud
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

  private static final String USER_CAMP_CONST_MSG = "No campaign with id %s  associated to the user %s";

  private final CampaignRepository campaignRepository;
  private final StateRepository stateRepository;
  private final ClosingCauseRepository closingCauseRepository;
  private final InterviewerRepository interviewerRepository;
  private final VisibilityRepository visibilityRepository;
  private final OrganizationUnitRepository organizationUnitRepository;
  private final CommunicationTemplateJpaRepository communicationTemplateRepository;
  private final UserService userService;
  private final UtilsService utilsService;
  private final CommunicationRequestRepository communicationRequestRepository;


  public StateCountDto getStateCount(String userId, String campaignId, String interviewerId,
      Long date,
      List<String> associatedOrgUnits) throws NotFoundException {
    StateCountDto stateCountDto = new StateCountDto();
    if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
      throw new NotFoundException(String.format(USER_CAMP_CONST_MSG, campaignId, userId));
    }
    if (!interviewerRepository.findById(interviewerId).isPresent()) {
      log.error("No interviewer found for the id {}", interviewerId);
      throw new NotFoundException(
          String.format("No interviewers found for the id %s", interviewerId));

    }
    List<String> userOuIds;
    if (!userId.equals(Constants.GUEST)) {
      userOuIds = utilsService.getRelatedOrganizationUnits(userId);
    } else {
      userOuIds = organizationUnitRepository.findAllId();
    }

    List<String> intervIds = interviewerRepository.findInterviewersByOrganizationUnits(
        associatedOrgUnits);
    Long dateToUse = date;
    if (dateToUse == null) {
      dateToUse = System.currentTimeMillis();
    }
    if (!intervIds.isEmpty() && (intervIds.contains(interviewerId)) || userId.equals(
        Constants.GUEST)) {
      Map<String, Long> stateCounts = new HashMap<>(
          stateRepository.getStateCount(campaignId, interviewerId, userOuIds, dateToUse));
          stateCounts.put(Constants.NOTICE_COUNT,
                  communicationRequestRepository.getCommunicationRequestCountByInterviewersAndCommunicationType(
                      List.of(campaignId), Set.of(interviewerId),CommunicationType.NOTICE, userOuIds, dateToUse).getFirst()
                      .count());
          stateCounts.put(Constants.REMINDER_COUNT,
                  communicationRequestRepository.getCommunicationRequestCountByInterviewersAndCommunicationType(
                      List.of(campaignId), Set.of(interviewerId), CommunicationType.REMINDER, userOuIds, dateToUse).getFirst()
                      .count());


      stateCountDto = new StateCountDto(stateCounts);
      stateCountDto.addClosingCauseCount(
          closingCauseRepository.getStateClosedByClosingCauseCount(campaignId,
              interviewerId, userOuIds, dateToUse));
    }
    if (stateCountDto.getTotal() == null) {
      throw new NotFoundException(String.format(
          "No matching interviewers %s were found for the user % and the campaign %s",
          interviewerId,
          userId, campaignId));
    }
    return stateCountDto;
  }

  public StateCountCampaignDto getStateCountByCampaign(String userId, String campaignId, Long date)
      throws NotFoundException {
    StateCountCampaignDto stateCountCampaignDto = new StateCountCampaignDto();
    if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
      throw new NotFoundException(String.format(USER_CAMP_CONST_MSG, campaignId, userId));
    }
    List<StateCountDto> stateCountList = new ArrayList<>();
    Long dateToUse = date;
    if (dateToUse == null) {
      dateToUse = System.currentTimeMillis();
    }
    for (String id : organizationUnitRepository.findAllId()) {
      if (organizationUnitRepository.findChildren(id).isEmpty()
          && visibilityRepository.findVisibility(campaignId, id).isPresent()) {

        Map<String, Long> stateCountsByCampaign = new HashMap<>(
            stateRepository.getStateCountByCampaignAndOU(campaignId, id, dateToUse));
        stateCountsByCampaign.put(Constants.NOTICE_COUNT,
            communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeByOU(
                campaignId, CommunicationType.NOTICE, dateToUse, List.of(id)));
        stateCountsByCampaign.put(Constants.REMINDER_COUNT,
            communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeByOU(
                campaignId, CommunicationType.REMINDER, dateToUse, List.of(id)));

        StateCountDto dto = new StateCountDto(id, organizationUnitRepository.findLabel(id), stateCountsByCampaign);
        dto.addClosingCauseCount(
            closingCauseRepository.getClosingCauseCountByCampaignAndOU(campaignId, id, dateToUse));
        stateCountList.add(dto);
      }
    }
    stateCountCampaignDto.setOrganizationUnits(stateCountList);

    Map<String, Long> stateCountsByCampaign = new HashMap<>(
        stateRepository.getStateCountByCampaignId(campaignId, dateToUse));
    stateCountsByCampaign.put(Constants.NOTICE_COUNT,
        communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationType(
            campaignId, CommunicationType.NOTICE, dateToUse));
    stateCountsByCampaign.put(Constants.REMINDER_COUNT,
        communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationType(
            campaignId, CommunicationType.REMINDER, dateToUse));
    StateCountDto dtoFrance = new StateCountDto(stateCountsByCampaign);
    dtoFrance.addClosingCauseCount(
        closingCauseRepository.getClosingCauseCountByCampaignId(campaignId, dateToUse));
    stateCountCampaignDto.setFrance(dtoFrance);
    if (stateCountCampaignDto.getFrance() == null
        || stateCountCampaignDto.getOrganizationUnits() == null) {
      throw new NotFoundException(String.format(
          "No matching survey units states were found for the user %s and the campaign %s", userId,
          campaignId));
    }
    return stateCountCampaignDto;
  }

  public List<StateCountDto> getStateCountByCampaigns(String userId, Long date) {
    List<StateCountDto> returnList = new ArrayList<>();
    List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, true);
    if (organizationUnits.isEmpty()) {
      return Collections.emptyList();
    }
    for (OrganizationUnitDto dto : organizationUnits) {
      log.info(dto.getId());
    }
    List<String> userOrgUnitIds = organizationUnits.stream().map(OrganizationUnitDto::getId)
        .collect(Collectors.toList());
    Long dateToUse = date;
    if (dateToUse == null) {
      dateToUse = System.currentTimeMillis();
    }
    List<String> campaignIds = campaignRepository.findAllCampaignIdsByOuIds(userOrgUnitIds);

    for (String id : campaignIds) {
      Map<String, Long> stateCountsByCampaign = new HashMap<>(
          stateRepository.getStateCountSumByCampaign(id, userOrgUnitIds, dateToUse));
      stateCountsByCampaign.put(Constants.NOTICE_COUNT,
          communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeByOU(
              id, CommunicationType.NOTICE, dateToUse, userOrgUnitIds));
      stateCountsByCampaign.put(Constants.REMINDER_COUNT,
          communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeByOU(
              id, CommunicationType.REMINDER, dateToUse, userOrgUnitIds));
      StateCountDto campaignSum = new StateCountDto(stateCountsByCampaign);
      campaignSum.addClosingCauseCount(
          closingCauseRepository.getgetStateClosedByClosingCauseCountByCampaign(id,
              userOrgUnitIds, dateToUse));
      if (campaignSum.getTotal() != null) {
        CampaignDto dto = campaignRepository.findDtoById(id);
        campaignSum.setCampaign(dto);
        returnList.add(campaignSum);
      }
    }
    return returnList;
  }

  @Override
  public List<StateCountDto> getStateCountByInterviewer(String userId, Long date) {
    List<String> campaignIds = campaignRepository.findAllCampaignIdsByOuIds(
        userService.getUserOUs(userId, true).stream()
            .map(OrganizationUnitDto::getId)
            .collect(Collectors.toList())
    );
    return getStateCountByInterviewerCommon(userId, campaignIds, date);
  }

  @Override
  public List<StateCountDto> getInterviewersStateCountByCampaign(String userId, String campaignId,
      Long date) {
    return getStateCountByInterviewerCommon(userId, List.of(campaignId), date);
  }


  private List<StateCountDto> getStateCountByInterviewerCommon(String userId,
      List<String> campaignIds, Long date) {
    List<StateCountDto> returnList = new ArrayList<>();

    List<String> userOrgUnitIds = userService.getUserOUs(userId, true).stream()
        .map(OrganizationUnitDto::getId)
        .collect(Collectors.toList());

    Long dateToUse = (date != null) ? date : System.currentTimeMillis();
    Set<String> interviewerIds = interviewerRepository.findIdsByOrganizationUnits(userOrgUnitIds);

    Map<String, Long> noticeCounts = communicationRequestRepository
        .getCommunicationRequestCountByInterviewersAndCommunicationType(campaignIds, interviewerIds,
            CommunicationType.NOTICE, userOrgUnitIds, dateToUse)
        .stream()
        .collect(Collectors.toMap(InterviewerCountDto::interviewerId, InterviewerCountDto::count));

    Map<String, Long> reminderCounts = communicationRequestRepository
        .getCommunicationRequestCountByInterviewersAndCommunicationType(campaignIds, interviewerIds,
            CommunicationType.REMINDER, userOrgUnitIds, dateToUse)
        .stream()
        .collect(Collectors.toMap(InterviewerCountDto::interviewerId, InterviewerCountDto::count));

    for (String id : interviewerIds) {
      Map<String, Long> stateCountsByInterviewerId = new HashMap<>(
          stateRepository.getStateCountSumByInterviewer(campaignIds, id, userOrgUnitIds, dateToUse)
      );

      StateCountDto interviewerSum = new StateCountDto(stateCountsByInterviewerId);
      interviewerSum.addClosingCauseCount(
          closingCauseRepository.getClosingCauseCountSumByInterviewer(campaignIds, id,
              userOrgUnitIds, dateToUse)
      );
      interviewerSum.setNoticeCount(noticeCounts.getOrDefault(id, 0L));
      interviewerSum.setReminderCount(reminderCounts.getOrDefault(id, 0L));

      if (interviewerSum.getTotal() != null) {
        interviewerSum.setInterviewer(interviewerRepository.findDtoById(id));
        returnList.add(interviewerSum);
      }
    }

    return returnList;
  }


  @Override
  public StateCountDto getNbSUNotAttributedStateCount(String userId, String id, Long date)
      throws NotFoundException {
    if (!utilsService.checkUserCampaignOUConstraints(userId, id)) {
      throw new NotFoundException(String.format(USER_CAMP_CONST_MSG, id, userId));
    }

    List<String> organizationUnits = userService.getUserOUs(userId, true)
        .stream().map(OrganizationUnitDto::getId)
        .collect(Collectors.toList());
    Long dateToUse = date;
    if (dateToUse == null) {
      dateToUse = System.currentTimeMillis();
    }

    StateCountDto interviewerSum = new StateCountDto(
        stateRepository.getStateCountNotAttributed(id, organizationUnits, dateToUse));
    interviewerSum.addClosingCauseCount(
        closingCauseRepository.getClosingCauseCountNotAttributed(id, organizationUnits, dateToUse));

    return interviewerSum;
  }
}
