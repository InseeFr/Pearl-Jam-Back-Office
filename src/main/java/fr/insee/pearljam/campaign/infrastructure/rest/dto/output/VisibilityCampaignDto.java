package fr.insee.pearljam.campaign.infrastructure.rest.dto.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fr.insee.pearljam.campaign.domain.model.Visibility;

import java.util.List;

@JsonPropertyOrder({ "organizationalUnit", "managementStartDate", "interviewerStartDate",
        "identificationPhaseStartDate", "collectionStartDate", "collectionEndDate", "endDate", "useLetterCommunication"})
public record VisibilityCampaignDto(
        String organizationalUnit,
        Long managementStartDate,
        Long interviewerStartDate,
        Long identificationPhaseStartDate,
        Long collectionStartDate,
        Long collectionEndDate,
        Long endDate,
        boolean useLetterCommunication,
        String mail,
        String tel) {

    public static List<VisibilityCampaignDto> fromModel(List<Visibility> visibilities) {
        return visibilities.stream()
                .map(visibility -> new VisibilityCampaignDto(
                    visibility.organizationalUnitId(),
                    visibility.managementStartDate(),
                    visibility.interviewerStartDate(),
                    visibility.identificationPhaseStartDate(),
                    visibility.collectionStartDate(),
                    visibility.collectionEndDate(),
                    visibility.endDate(),
                    visibility.useLetterCommunication(),
                    visibility.mail(),
                    visibility.tel()
                ))
                .toList();
    }
}
