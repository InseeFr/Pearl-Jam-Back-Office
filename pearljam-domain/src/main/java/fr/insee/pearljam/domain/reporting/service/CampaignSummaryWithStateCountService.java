package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.reporting.model.CampaignPhase;
import fr.insee.pearljam.domain.reporting.model.CampaignSummaryWithStateCount;
import fr.insee.pearljam.domain.reporting.port.in.CampaignSummaryWithStateCountPort;
import fr.insee.pearljam.domain.reporting.port.out.CampaignStateCountRepositoryPort;
import fr.insee.pearljam.domain.reporting.port.out.CampaignSummaryWithStateCountRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.StateCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CampaignSummaryWithStateCountService implements CampaignSummaryWithStateCountPort {

    private final CampaignSummaryWithStateCountRepositoryPort campaignSummaryWithStateCountRepositoryPort;
    private final CampaignStateCountRepositoryPort campaignStateCountRepositoryPort;

    private final UserService userService;
    private final DateService dateService;


    public List<CampaignSummaryWithStateCount> getCampaignSummaryWithStateCount(String userId) {

        long currentTimestamp = dateService.getCurrentTimestamp();
        List<String> organizationUnitIds = userService
                .getUserOUs(userId, true)
                .stream()
                .map(OrganizationUnitDto::getId)
                .toList();

        List<CampaignWithVisibility> userCampaignWithVisibility = campaignSummaryWithStateCountRepositoryPort.findByUserAndManagementVisibility(organizationUnitIds, userId, currentTimestamp);

        Map<String, StateCount> stateCountMap = getStringStateCountMapForCampaigns(userCampaignWithVisibility, organizationUnitIds, currentTimestamp);

        return userCampaignWithVisibility.stream()
                .map(camp -> {
                    StateCount sc = stateCountMap.getOrDefault(camp.id(), StateCount.empty(camp.id())); //state counts to  0 by default

                    return new CampaignSummaryWithStateCount(
                            camp.id(),
                            camp.label(),
                            camp.collectionStartDate(),
                            camp.collectionEndDate(),
                            camp.endDate(),
                            CampaignPhase.fromDates(currentTimestamp, camp.managementStartDate(), camp.collectionStartDate(), camp.collectionEndDate(), camp.endDate()),
                            toSurveyUnitsStateCount(sc)
                    );
                })
                .toList();
    }

    private Map<String, StateCount> getStringStateCountMapForCampaigns(List<CampaignWithVisibility> userCampaignWithVisibility, List<String> organizationUnitIds, long currentTimestamp) {
        List<String> campaignIds = userCampaignWithVisibility.stream()
                .map(CampaignWithVisibility::id)
                .toList();
        List<StateCount> stateCountList = campaignStateCountRepositoryPort.getStateCountByCampaignsAndOrganisationUnits(campaignIds, organizationUnitIds, Instant.ofEpochMilli(currentTimestamp));
        return stateCountList.stream()
                .collect(Collectors.toMap(StateCount::entityId, Function.identity()));
    }

    CampaignSummaryWithStateCount.SurveyUnits toSurveyUnitsStateCount(StateCount sc) {
        return new CampaignSummaryWithStateCount.SurveyUnits(
                sc.total(),
                sc.vicCount() + sc.prcCount() + sc.aocCount() + sc.apsCount() + sc.insCount() + sc.wftCount(), // VIC/PRC/AOC/APS/INS/WFT
                sc.tbrCount(),
                sc.finCount() + sc.cloCount(), // FIN/CLO
                0L // TODO
        );
    }

}

