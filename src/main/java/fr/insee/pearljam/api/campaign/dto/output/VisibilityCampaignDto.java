package fr.insee.pearljam.api.campaign.dto.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fr.insee.pearljam.domain.campaign.model.Visibility;

import java.util.List;

@JsonPropertyOrder({ "organizationalUnit", "managementStartDate", "interviewerStartDate",
        "identificationPhaseStartDate", "collectionStartDate", "collectionEndDate", "endDate"})
public record VisibilityCampaignDto(
        String organizationalUnit,
        Long managementStartDate,
        Long interviewerStartDate,
        Long identificationPhaseStartDate,
        Long collectionStartDate,
        Long collectionEndDate,
        Long endDate) {

    public static List<VisibilityCampaignDto> fromModel(List<Visibility> visibilities) {
        return visibilities.stream()
                .map(visibility -> new VisibilityCampaignDto(
                    visibility.organizationalUnitId(),
                    visibility.managementStartDate(),
                    visibility.interviewerStartDate(),
                    visibility.identificationPhaseStartDate(),
                    visibility.collectionStartDate(),
                    visibility.collectionEndDate(),
                    visibility.endDate()
                ))
                .toList();
    }
}
