package fr.insee.pearljam.api.campaign.dto.input.visibility;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.campaign.model.Visibility;

/**
 * A Data Transfer Object for representing visibility information.
 *
 * @param collectionStartDate         Collection start date of the visibility
 * @param collectionEndDate           Collection end date of the visibility
 * @param identificationPhaseStartDate Identification phase start date of the visibility
 * @param interviewerStartDate        Interviewer start date of the visibility
 * @param managementStartDate         Manager start date of the visibility
 * @param endDate                     End date of the visibility
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VisibilityUpdateDto(
        Long managementStartDate,
        Long interviewerStartDate,
        Long identificationPhaseStartDate,
        Long collectionStartDate,
        Long collectionEndDate,
        Long endDate
) {

    public static Visibility toModel(VisibilityUpdateDto visibilityDto, String campaignId, String ouId) {
        return new Visibility(campaignId, ouId,
                visibilityDto.managementStartDate(),
                visibilityDto.interviewerStartDate(),
                visibilityDto.identificationPhaseStartDate(),
                visibilityDto.collectionStartDate(),
                visibilityDto.collectionEndDate(),
                visibilityDto.endDate(),
                null);
    }
}
