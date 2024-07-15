package fr.insee.pearljam.api.dto.surveyunit;

import java.util.Comparator;
import java.util.List;

import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import jakarta.validation.Valid;
import lombok.Data;
import fr.insee.pearljam.infrastructure.surveyunit.entity.IdentificationDB;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.bussinessrules.BussinessRules;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.CommunicationRequestDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.surveyunit.dto.IdentificationDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;
import fr.insee.pearljam.api.dto.state.StateDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SurveyUnitDetailDto {
	private String id;
	private List<PersonDto> persons;
	private AddressDto address;
	private Boolean priority;
	private Boolean move;
	private String campaign;
	@Valid
	private List<CommentDto> comments;
	private SampleIdentifiersDto sampleIdentifiers;
	private List<StateDto> states;
	private List<ContactAttemptDto> contactAttempts;
	private ContactOutcomeDto contactOutcome;
	private IdentificationDto identification;
	@Valid
	private List<CommunicationRequestDto> communicationRequests;

	public SurveyUnitDetailDto() {
	}

	public SurveyUnitDetailDto(SurveyUnit surveyUnit) {
		this.id = surveyUnit.getId();
		this.setPersons(surveyUnit.getPersons().stream()
				.map(PersonDto::new)
				.toList());
		this.priority = surveyUnit.isPriority();
		this.campaign = surveyUnit.getCampaign().getId();
		this.address = new AddressDto(surveyUnit.getAddress());
		if (surveyUnit.getSampleIdentifier() != null) {
			this.sampleIdentifiers = new SampleIdentifiersDto(surveyUnit.getSampleIdentifier());
		}
		this.comments = surveyUnit.getDomainComments().stream()
				.map(CommentDto::fromModel)
				.toList();
		this.contactAttempts = surveyUnit.getContactAttempts().stream().map(ContactAttemptDto::new)
				.toList();
		if (surveyUnit.getContactOucome() != null) {
			this.contactOutcome = new ContactOutcomeDto(surveyUnit.getContactOucome());
		}
		this.states = surveyUnit.getStates().stream()
				.sorted(Comparator.comparing(State::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.filter(s -> BussinessRules.stateCanBeSeenByInterviewerBussinessRules(s.getType()))
				.map(StateDto::new)
				.toList();
		if (surveyUnit.getIdentification() != null) {
			this.identification = IdentificationDto.fromModel(IdentificationDB.toModel(surveyUnit.getIdentification()));
		}
		this.move = surveyUnit.getMove();
		this.communicationRequests = surveyUnit.getCommunicationRequests().stream()
				.map(CommunicationRequestDB::toModel)
				.map(CommunicationRequestDto::fromModel)
				.toList();
	}
}
