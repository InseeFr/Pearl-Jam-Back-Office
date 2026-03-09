package fr.insee.pearljam.api.surveyunit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.bussinessrules.BusinessRules;
import fr.insee.pearljam.api.campaign.dto.output.CommunicationTemplateResponseDto;
import fr.insee.pearljam.domain.state.model.State;
import fr.insee.pearljam.domain.surveyunit.model.SurveyUnit;
import fr.insee.pearljam.api.state.dto.StateDto;
import fr.insee.pearljam.api.surveyunit.dto.contacthistory.NextContactHistoryDto;
import fr.insee.pearljam.api.surveyunit.dto.contacthistory.PreviousContactHistoryDto;
import fr.insee.pearljam.api.surveyunit.dto.identification.IdentificationDto;
import fr.insee.pearljam.domain.surveyunit.model.SurveyUnitForInterviewer;

import java.util.Comparator;
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

	public static SurveyUnitInterviewerResponseDto fromModel(SurveyUnitForInterviewer surveyUnitForInterviewer) {
		SurveyUnit surveyUnit = surveyUnitForInterviewer.surveyUnit();
		List<PersonDto> persons = surveyUnit.getModelPersons().stream()
				.map(PersonDto::fromModel)
				.toList();
		PreviousContactHistoryDto previousContactHistory = PreviousContactHistoryDto.fromModel(surveyUnit.getPreviousContactHistory());
		NextContactHistoryDto nextContactHistory = NextContactHistoryDto.fromModel(surveyUnit.getNextContactHistory());

		List<CommentDto> comments = CommentDto.fromModel(surveyUnit.getModelComments());
		AddressDto address = new AddressDto(surveyUnit.getAddress());
		List<ContactAttemptDto> contactAttempts = surveyUnit
				.getContactAttempts()
				.stream()
				.map(ContactAttemptDto::new)
				.toList();
		List<StateDto> states = surveyUnit
				.getStates()
				.stream()
				.sorted(Comparator.comparing(State::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.filter(s -> BusinessRules.stateCanBeSeenByInterviewerBussinessRules(s.getType()))
				.map(StateDto::new)
				.toList();
		ContactOutcomeDto contactOutcome = null;
		if (surveyUnit.getContactOutcome() != null) {
			contactOutcome = ContactOutcomeDto.fromModel(surveyUnit.getModelContactOutcome());
		}

		SampleIdentifiersDto sampleIdentifiers = null;
		if (surveyUnit.getSampleIdentifier() != null) {
			sampleIdentifiers = new SampleIdentifiersDto(surveyUnit.getSampleIdentifier());
		}
		return new SurveyUnitInterviewerResponseDto(surveyUnit.getId(), surveyUnit.getDisplayName(), persons, address,
				surveyUnit.isPriority(), surveyUnit.getMove(), surveyUnit.getCampaign().getId(),
				comments, sampleIdentifiers, states, contactAttempts, contactOutcome,
				IdentificationDto.fromModel(surveyUnit.getModelIdentification()),
				CommunicationTemplateResponseDto.fromModel(surveyUnitForInterviewer.communicationTemplates()),
				CommunicationRequestResponseDto.fromModel(surveyUnit.getModelCommunicationRequests()),
				previousContactHistory, nextContactHistory);
	}
}
