package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.*;
import fr.insee.pearljam.api.service.StateService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.campaign.port.serverside.VisibilityRepository;
import fr.insee.pearljam.domain.count.model.ClosingCauseCount;
import fr.insee.pearljam.domain.count.model.CommunicationRequestCount;
import fr.insee.pearljam.domain.count.model.StateCount;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.InterviewerNotFoundException;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

  private final CampaignRepository campaignRepository;
  private final StateRepository stateRepository;
  private final ClosingCauseRepository closingCauseRepository;
  private final InterviewerRepository interviewerRepository;
  private final VisibilityRepository visibilityRepository;
  private final OrganizationUnitRepository organizationUnitRepository;
  private final UserService userService;
  private final UtilsService utilsService;
  private final CommunicationRequestRepository communicationRequestRepository;


  public StateCountDto getStateCount(String userId, String campaignId, String interviewerId,
      Long date,
      List<String> associatedOrgUnits) throws CampaignNotFoundException {
    StateCountDto stateCountDto = new StateCountDto();
    userService.checkUserAssociationToCampaign(campaignId, userId);
    if (interviewerRepository.findById(interviewerId).isEmpty()) {
      throw new InterviewerNotFoundException(interviewerId);
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
          communicationRequestRepository.getCommRequestCountByInterviewersAndType(
                  List.of(campaignId), Set.of(interviewerId), CommunicationType.NOTICE, userOuIds, dateToUse)
              .stream().findFirst().map(InterviewerCountDto::count).orElse(0L));

      stateCounts.put(Constants.REMINDER_COUNT,
          communicationRequestRepository.getCommRequestCountByInterviewersAndType(
                  List.of(campaignId), Set.of(interviewerId), CommunicationType.REMINDER, userOuIds, dateToUse)
              .stream().findFirst().map(InterviewerCountDto::count).orElse(0L));



      stateCountDto = new StateCountDto(stateCounts);
      stateCountDto.addClosingCauseCount(
          closingCauseRepository.getStateClosedByClosingCauseCount(campaignId,
              interviewerId, userOuIds, dateToUse));
    }
    if (stateCountDto.getTotal() == null) {
      log.warn("No matching interviewers {} were found for the user {} and the campaign {}", interviewerId, userId, campaignId);
      throw new InterviewerNotFoundException("All");
    }
    return stateCountDto;
  }

  public StateCountCampaignDto getStateCountByCampaign(String userId, String campaignId, Long date)
          throws NotFoundException, CampaignNotFoundException {
    StateCountCampaignDto stateCountCampaignDto = new StateCountCampaignDto();
    userService.checkUserAssociationToCampaign(campaignId, userId);
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
            communicationRequestRepository.getCommRequestCountByCampaignTypeAndOrgaUnit(
                campaignId, CommunicationType.NOTICE, dateToUse, List.of(id)));
        stateCountsByCampaign.put(Constants.REMINDER_COUNT,
            communicationRequestRepository.getCommRequestCountByCampaignTypeAndOrgaUnit(
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
        communicationRequestRepository.getCommRequestCountByCampaignAndType(
            campaignId, CommunicationType.NOTICE, dateToUse));
    stateCountsByCampaign.put(Constants.REMINDER_COUNT,
        communicationRequestRepository.getCommRequestCountByCampaignAndType(
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
    Long dateToUse = (date != null) ? date : System.currentTimeMillis();

    List<String> userOrgUnitIds = userService
            .getUserOUs(userId, true)
            .stream().map(OrganizationUnitDto::getId).toList();
    if (userOrgUnitIds.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, CampaignDto> campaigns = campaignRepository.findAllDtoByOuIds(userOrgUnitIds)
            .stream().collect(Collectors.toMap(CampaignDto::getId, campaign -> campaign));

    List<String> campaignIds = campaignRepository.findAllCampaignIdsByOuIds(userOrgUnitIds);
    if (campaignIds.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, StateCountDto> stateCountsByCampaign = toDtos(
            stateRepository.findGroupedByCampaign(campaignIds, userOrgUnitIds, dateToUse)
    );

    Map<String, CommunicationRequestCount> commRequestCountsByCampaign =
            communicationRequestRepository.getCommRequestCountByCampaigns(campaignIds, userOrgUnitIds, dateToUse)
                    .stream()
                    .collect(Collectors.toMap(CommunicationRequestCount::campaignId, projection -> projection));

    Map<String, ClosingCauseCount> closingCauseCountsByCampaign =
            closingCauseRepository.getStateClosedByClosingCauseCountByCampaigns(campaignIds, userOrgUnitIds, dateToUse)
                    .stream()
                    .collect(Collectors.toMap(ClosingCauseCount::campaignId, projection -> projection));


    return campaignIds.stream()
            .map(id -> {
              StateCountDto campaignSum = mergeCounts(
                      stateCountsByCampaign.get(id),
                      commRequestCountsByCampaign.get(id),
                      closingCauseCountsByCampaign.get(id)
              );
              campaignSum.setCampaign(campaigns.get(id));
              return campaignSum;
            })
            .toList();
  }

  private Map<String, StateCountDto> toDtos(List<StateCount> results) {
    return results.stream()
            .collect(Collectors.toMap(StateCount::campaignId, this::toDto));
  }

  private StateCountDto toDto(StateCount projection) {
    Map<String, Long> counts = new HashMap<>();
    counts.put(Constants.NVM_COUNT, nullToZero(projection.nvmCount()));
    counts.put(Constants.NNS_COUNT, nullToZero(projection.nnsCount()));
    counts.put(Constants.ANV_COUNT, nullToZero(projection.anvCount()));
    counts.put(Constants.VIN_COUNT, nullToZero(projection.vinCount()));
    counts.put(Constants.VIC_COUNT, nullToZero(projection.vicCount()));
    counts.put(Constants.PRC_COUNT, nullToZero(projection.prcCount()));
    counts.put(Constants.AOC_COUNT, nullToZero(projection.aocCount()));
    counts.put(Constants.APS_COUNT, nullToZero(projection.apsCount()));
    counts.put(Constants.INS_COUNT, nullToZero(projection.insCount()));
    counts.put(Constants.WFT_COUNT, nullToZero(projection.wftCount()));
    counts.put(Constants.WFS_COUNT, nullToZero(projection.wfsCount()));
    counts.put(Constants.TBR_COUNT, nullToZero(projection.tbrCount()));
    counts.put(Constants.FIN_COUNT, nullToZero(projection.finCount()));
    counts.put(Constants.CLO_COUNT, nullToZero(projection.cloCount()));
    counts.put(Constants.NVA_COUNT, nullToZero(projection.nvaCount()));
    counts.put(Constants.TOTAL_COUNT, nullToZero(projection.total()));
    return new StateCountDto(counts);
  }


  private Long nullToZero(Long value) {
    return value == null ? 0L : value;
  }

  private StateCountDto mergeCounts(StateCountDto stateCounts,
                                    CommunicationRequestCount commCounts,
                                    ClosingCauseCount closingCauseCounts) {

    StateCountDto merged = stateCounts != null ? stateCounts : new StateCountDto(Collections.emptyMap());

    merged.setNoticeCount(commCounts != null && commCounts.noticeCount() != null
            ? commCounts.noticeCount()
            : 0L);
    merged.setReminderCount(commCounts != null && commCounts.reminderCount() != null
            ? commCounts.reminderCount()
            : 0L);

    merged.addClosingCauseCount(toClosingCauseCountMap(closingCauseCounts));
    return merged;
  }

  private Map<String, Long> toClosingCauseCountMap(ClosingCauseCount projection) {
    if (projection == null) {
      return Collections.emptyMap();
    }
    Map<String, Long> counts = new HashMap<>();
    counts.put(Constants.NPA_COUNT, projection.npaCount() == null ? 0L : projection.npaCount());
    counts.put(Constants.NPI_COUNT, projection.npiCount() == null ? 0L : projection.npiCount());
    counts.put(Constants.NPX_COUNT, projection.npxCount() == null ? 0L : projection.npxCount());
    counts.put(Constants.ROW_COUNT, projection.rowCount() == null ? 0L : projection.rowCount());
    return counts;
  }




  @Override
  public List<StateCountDto> getStateCountByInterviewer(String userId, Long date) {
    List<String> campaignIds = campaignRepository.findAllCampaignIdsByOuIds(
        userService.getUserOUs(userId, true).stream()
            .map(OrganizationUnitDto::getId)
            .toList()
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
        .getCommRequestCountByInterviewersAndType(campaignIds, interviewerIds,
            CommunicationType.NOTICE, userOrgUnitIds, dateToUse)
        .stream()
        .collect(Collectors.toMap(InterviewerCountDto::interviewerId, InterviewerCountDto::count));

    Map<String, Long> reminderCounts = communicationRequestRepository
        .getCommRequestCountByInterviewersAndType(campaignIds, interviewerIds,
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
  public StateCountDto getNbSUNotAttributedStateCount(String userId, String campaignId, Long date)
          throws CampaignNotFoundException {
    userService.checkUserAssociationToCampaign(campaignId, userId);

    List<String> organizationUnits = userService.getUserOUs(userId, true)
        .stream().map(OrganizationUnitDto::getId)
        .collect(Collectors.toList());
    Long dateToUse = date;
    if (dateToUse == null) {
      dateToUse = System.currentTimeMillis();
    }

    StateCountDto interviewerSum = new StateCountDto(
            stateRepository.getStateCountNotAttributed(campaignId, organizationUnits, dateToUse));
    interviewerSum.addClosingCauseCount(
            closingCauseRepository.getClosingCauseCountNotAttributed(campaignId, organizationUnits, dateToUse));

    return interviewerSum;
  }
}
