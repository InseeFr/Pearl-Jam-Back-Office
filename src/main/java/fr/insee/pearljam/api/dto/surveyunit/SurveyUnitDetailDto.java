package fr.insee.pearljam.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.bussinessrules.BusinessRules;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.CommunicationRequestResponseDto;
import fr.insee.pearljam.api.surveyunit.dto.ContactOutcomeDto;
import fr.insee.pearljam.api.surveyunit.dto.PersonDto;
import fr.insee.pearljam.api.surveyunit.dto.contactHistory.NextContactHistoryDto;
import fr.insee.pearljam.api.surveyunit.dto.contactHistory.PreviousContactHistoryDto;
import fr.insee.pearljam.api.surveyunit.dto.identification.IdentificationDto;
import fr.insee.pearljam.infrastructure.surveyunit.entity.PersonDB;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class SurveyUnitDetailDto {
	private String id;
	private List<PersonDto> persons;
	private AddressDto address;
	private Boolean priority;
	private Boolean move;
	private String campaign;
	private List<CommentDto> comments;
	private SampleIdentifiersDto sampleIdentifiers;
	private List<StateDto> states;
	private List<ContactAttemptDto> contactAttempts;
	private ContactOutcomeDto contactOutcome;
	private IdentificationDto identification;
	private List<CommunicationRequestResponseDto> communicationRequests;
	private PreviousContactHistoryDto previousContactHistory;
	private NextContactHistoryDto nextContactHistory;

	public SurveyUnitDetailDto(SurveyUnit surveyUnit) {
		this.id = surveyUnit.getId();
		this.setPersons(surveyUnit.getPersons().stream()
				.map(person -> PersonDB.toModel(person,null))
				.map(PersonDto::fromModel)
				.toList());
		this.priority = surveyUnit.isPriority();
		this.campaign = surveyUnit.getCampaign().getId();
		this.address = new AddressDto(surveyUnit.getAddress());
		if (surveyUnit.getSampleIdentifier() != null) {
			this.sampleIdentifiers = new SampleIdentifiersDto(surveyUnit.getSampleIdentifier());
		}
		this.comments = CommentDto.fromModel(surveyUnit.getModelComments());
		this.contactAttempts = surveyUnit.getContactAttempts().stream().map(ContactAttemptDto::new)
				.toList();
		if (surveyUnit.getContactOutcome() != null) {
			this.contactOutcome = ContactOutcomeDto.fromModel(surveyUnit.getModelContactOutcome());
		}
		this.states = surveyUnit.getStates().stream()
				.sorted(Comparator.comparing(State::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.filter(s -> BusinessRules.stateCanBeSeenByInterviewerBussinessRules(s.getType()))
				.map(StateDto::new)
				.toList();

		this.identification = IdentificationDto.fromModel(surveyUnit.getModelIdentification());

		this.move = surveyUnit.getMove();
		this.communicationRequests = CommunicationRequestResponseDto.fromModel(surveyUnit.getModelCommunicationRequests());
		this.previousContactHistory=PreviousContactHistoryDto.fromModel(surveyUnit.getPreviousContactHistory());
		this.nextContactHistory=NextContactHistoryDto.fromModel(surveyUnit.getNextContactHistory());
	}
}
