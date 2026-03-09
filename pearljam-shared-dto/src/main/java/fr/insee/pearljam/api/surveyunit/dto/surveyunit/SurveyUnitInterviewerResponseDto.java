package fr.insee.pearljam.api.surveyunit.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.contracts.surveyunit.dto.person.PersonDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.AddressDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.CommentDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.ContactAttemptDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.ContactOutcomeDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.SampleIdentifiersDto;
import fr.insee.pearljam.api.campaign.dto.output.CommunicationTemplateResponseDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.api.surveyunit.dto.contacthistory.NextContactHistoryDto;
import fr.insee.pearljam.contracts.surveyunit.dto.contacthistory.PreviousContactHistoryDto;
import fr.insee.pearljam.contracts.surveyunit.dto.identification.IdentificationDto;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitInterviewerResponseDto(
	String id,
	String displayName,
	List<PersonDto> persons,
	AddressDto address,
	Boolean priority,
	Boolean move,
	String campaign,
	List<CommentDto> comments,
	SampleIdentifiersDto sampleIdentifiers,
	List<StateDto> states,
	List<ContactAttemptDto> contactAttempts,
	ContactOutcomeDto contactOutcome,
	IdentificationDto identification,
	List<CommunicationTemplateResponseDto> communicationTemplates,
	List<CommunicationRequestResponseDto> communicationRequests,
	PreviousContactHistoryDto previousContactHistory,
	NextContactHistoryDto nextContactHistory
) {
}
