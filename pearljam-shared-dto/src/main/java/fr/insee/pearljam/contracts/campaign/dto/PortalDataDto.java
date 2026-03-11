package fr.insee.pearljam.contracts.campaign.dto;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.SurveyUnitCounts;
import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;

import java.util.List;

public record PortalDataDto(
    String id,
    String label,
    String email,
    Long managementStartDate,
    Long interviewerStartDate,
    Long identificationPhaseStartDate,
    Long collectionStartDate,
    Long collectionEndDate,
    Long endDate,
    List<ReferentDto> referents,
    List<InterviewerPortalDto> interviewers,
    int abandoned,
    int unallocated,
    int total
) {
    public static PortalDataDto fromModel(
            String campaignId,
            String campaignLabel,
            String campaignEmail,
            CampaignVisibility campaignVisibility,
            List<ReferentDto> referents,
            List<InterviewerCount> interviewerCounts,
            SurveyUnitCounts surveyUnitCounts
    ) {
        List<InterviewerPortalDto> interviewers = interviewerCounts.stream()
                .map(InterviewerPortalDto::fromModel)
                .toList();

        return new PortalDataDto(
            campaignId,
            campaignLabel,
            campaignEmail,
            campaignVisibility.managementStartDate(),
            campaignVisibility.interviewerStartDate(),
            campaignVisibility.identificationPhaseStartDate(),
            campaignVisibility.collectionStartDate(),
            campaignVisibility.collectionEndDate(),
            campaignVisibility.endDate(),
            referents,
            interviewers,
            surveyUnitCounts.abandoned(),
            surveyUnitCounts.unallocated(),
            surveyUnitCounts.total()
        );
    }
}
