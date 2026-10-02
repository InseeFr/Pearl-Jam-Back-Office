package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.model.count.ClosingCauseCount;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCount;
import fr.insee.pearljam.domain.surveyunit.model.count.StateCount;
import fr.insee.pearljam.domain.surveyunit.port.in.StateService;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
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
  private final UserService userService;
  private final CommunicationRequestRepository communicationRequestRepository;

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

    List<String> campaignIds = campaignRepository.findAllManagedAndNotClosedCampaignIdsByOuIds(userOrgUnitIds, dateToUse);
    if (campaignIds.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, StateCountDto> stateCountsByCampaign = toDtos(
            stateRepository.findGroupedByCampaign(campaignIds, userOrgUnitIds, dateToUse)
    );

    Map<String, CommunicationRequestCount> commRequestCountsByCampaign =
            communicationRequestRepository.getCommRequestCountByCampaigns(campaignIds, userOrgUnitIds, dateToUse)
                    .stream()
                    .collect(Collectors.toMap(CommunicationRequestCount::entityId, projection -> projection));

    Map<String, ClosingCauseCount> closingCauseCountsByCampaign =
            closingCauseRepository.getStateClosedByClosingCauseCountByCampaigns(campaignIds, userOrgUnitIds, dateToUse)
                    .stream()
                    .collect(Collectors.toMap(ClosingCauseCount::entityId, projection -> projection));

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
            .collect(Collectors.toMap(StateCount::entityId, this::toDto));
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

  private List<StateCountDto> getStateCountByInterviewerCommon(String userId,
      List<String> campaignIds, Long date) {
    List<StateCountDto> returnList = new ArrayList<>();

    List<String> userOrgUnitIds = userService.getUserOUs(userId, true).stream()
        .map(OrganizationUnitDto::getId)
        .toList();

    Long dateToUse = (date != null) ? date : System.currentTimeMillis();
    Set<String> interviewerIds = interviewerRepository.findIdsByOrganizationUnitsAndCampaignId(userOrgUnitIds, campaignIds);

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
}
