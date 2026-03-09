package fr.insee.pearljam.api.surveyunit.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.contracts.surveyunit.dto.person.PersonDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.AddressDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.CommentDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.ContactAttemptDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.ContactOutcomeDto;
import fr.insee.pearljam.api.surveyunit.dto.contacthistory.NextContactHistoryDto;
import fr.insee.pearljam.contracts.surveyunit.dto.identification.RawIdentificationDto;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Record representing a SurveyUnitResponseDto
 *
 * @param id                       The ID of the survey unit
 * @param persons                  The list of persons associated with the survey unit
 * @param address                  The address of the survey unit
 * @param move                     Indicates if the survey unit has moved
 * @param comments                 The list of comments related to the survey unit
 * @param states                   The list of states of the survey unit
 * @param contactAttempts          The list of contact attempts for the survey unit
 * @param contactOutcome           The contact outcome of the survey unit
 * @param identification           The identification information of the survey unit
 * @param communicationRequests    The list of communication requests for the survey unit
 * @param nextContactHistory       The contacts to be used for next campaign
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitUpdateDto(
    String id,
    @Valid
    List<PersonDto> persons,
    AddressDto address,
    Boolean move,
    @Valid
    List<CommentDto> comments,
    List<StateDto> states,
    List<ContactAttemptDto> contactAttempts,
    ContactOutcomeDto contactOutcome,
    RawIdentificationDto identification,
    @Valid
    List<CommunicationRequestCreateDto> communicationRequests,
    @Valid
    NextContactHistoryDto nextContactHistory
) {
}
