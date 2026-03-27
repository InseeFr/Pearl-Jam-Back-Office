package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.reporting.model.CampaignPhase;
import fr.insee.pearljam.domain.reporting.model.CampaignSummaryWithStateCount;
import fr.insee.pearljam.domain.reporting.port.in.CampaignSummaryWithStateCountPort;
import fr.insee.pearljam.domain.reporting.port.out.CampaignSummaryWithStateCountRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CampaignSummaryWithStateCountService implements CampaignSummaryWithStateCountPort {

    private final CampaignSummaryWithStateCountRepositoryPort campaignSummaryWithStateCountRepositoryPort;
    private final UserService userService;
    private final DateService dateService;


    public List<CampaignSummaryWithStateCount> getCampaignSummaryWithStateCount(String userId) {
        List<String> organizationUnitIds = userService
                .getUserOUs(userId, true)
                .stream()
                .map(OrganizationUnitDto::getId)
                .toList();

        Long currentTimestamp = dateService.getCurrentTimestamp();
        List<CampaignWithVisibility> userCampaignWithVisibility = campaignSummaryWithStateCountRepositoryPort.findByUserAndManagementVisibility(organizationUnitIds, userId, currentTimestamp);
        return userCampaignWithVisibility.stream()
                .map(camp -> new CampaignSummaryWithStateCount(
                        camp.id(),
                        camp.label(),
                        camp.collectionStartDate(),
                        camp.collectionEndDate(),
                        camp.endDate(),
                        CampaignPhase.fromDates(currentTimestamp, camp.managementStartDate(), camp.collectionStartDate(), camp.collectionEndDate(), camp.endDate()),
                        new CampaignSummaryWithStateCount.SurveyUnits(0L, 0L, 0L, 0L, 0L, 0L)
                ))
                .toList();
    }


}

