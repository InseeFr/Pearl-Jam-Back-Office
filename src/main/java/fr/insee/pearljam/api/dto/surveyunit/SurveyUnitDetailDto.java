package fr.insee.pearljam.api.dto.surveyunit;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.bussinessrules.BussinessRules;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.identification.IdentificationDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;
import fr.insee.pearljam.api.dto.state.StateDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
	private List<CommunicationRequestDto> communicationRequests;

	public SurveyUnitDetailDto() {
	}

	public SurveyUnitDetailDto(SurveyUnit surveyUnit) {
		this.id = surveyUnit.getId();
		this.setPersons(surveyUnit.getPersons().stream()
				.map(PersonDto::new)
				.collect(Collectors.toList()));
		this.priority = surveyUnit.isPriority();
		this.campaign = surveyUnit.getCampaign().getId();
		this.address = new AddressDto(surveyUnit.getAddress());
		if (surveyUnit.getSampleIdentifier() != null) {
			this.sampleIdentifiers = new SampleIdentifiersDto(surveyUnit.getSampleIdentifier());
		}
		this.comments = surveyUnit.getComments().stream().map(CommentDto::new).collect(Collectors.toList());
		this.contactAttempts = surveyUnit.getContactAttempts().stream().map(ContactAttemptDto::new)
				.collect(Collectors.toList());
		if (surveyUnit.getContactOucome() != null) {
			this.contactOutcome = new ContactOutcomeDto(surveyUnit.getContactOucome());
		}
		this.states = surveyUnit.getStates().stream()
				.sorted(Comparator.comparing(State::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.filter(s -> BussinessRules.stateCanBeSeenByInterviewerBussinessRules(s.getType()))
				.map(StateDto::new)
				.collect(Collectors.toList());
		if (surveyUnit.getIdentification() != null) {
			this.identification = new IdentificationDto(surveyUnit.getIdentification());
		}
		this.move = surveyUnit.isMove();
		this.communicationRequests = surveyUnit.getCommunicationRequests().stream().map(CommunicationRequestDto::new)
				.collect(Collectors.toList());
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the address
	 */
	public AddressDto getAddress() {
		return address;
	}

	/**
	 * @param address2 the address to set
	 */
	public void setAddress(AddressDto address) {
		this.address = address;
	}

	/**
	 * @return the priority
	 */
	public Boolean getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Boolean priority) {
		this.priority = priority;
	}

	/**
	 * @return the campaign
	 */
	public String getCampaign() {
		return campaign;
	}

	/**
	 * @param campaign the campaign to set
	 */
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	/**
	 * @return the comments
	 */
	public List<CommentDto> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<CommentDto> comments) {
		this.comments = comments;
	}

	/**
	 * @return the sampleIdentifiers
	 */
	public SampleIdentifiersDto getSampleIdentifiers() {
		return sampleIdentifiers;
	}

	/**
	 * @param sampleIdentifier the sampleIdentifiers to set
	 */
	public void setSampleIdentifiers(SampleIdentifiersDto sampleIdentifier) {
		this.sampleIdentifiers = sampleIdentifier;
	}

	/**
	 * @return the states
	 */
	public List<StateDto> getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(List<StateDto> states) {
		this.states = states;
	}

	/**
	 * @return the contactAttempts
	 */
	public List<ContactAttemptDto> getContactAttempts() {
		return contactAttempts;
	}

	/**
	 * @param contactAttempts the contactAttempts to set
	 */
	public void setContactAttempts(List<ContactAttemptDto> contactAttempts) {
		this.contactAttempts = contactAttempts;
	}

	/**
	 * @return the contactOutcome
	 */
	public ContactOutcomeDto getContactOutcome() {
		return contactOutcome;
	}

	/**
	 * @param contactOutcome the contactOutcome to set
	 */
	public void setContactOutcome(ContactOutcomeDto contactOutcome) {
		this.contactOutcome = contactOutcome;
	}

	public List<PersonDto> getPersons() {
		return persons;
	}

	public void setPersons(List<PersonDto> persons) {
		this.persons = persons;
	}

	public Boolean isPriority() {
		return this.priority;
	}

	public IdentificationDto getIdentification() {
		return this.identification;
	}

	public void setIdentification(IdentificationDto identification) {
		this.identification = identification;
	}

	public Boolean isMove() {
		return this.move;
	}

	public Boolean getMove() {
		return this.move;
	}

	public void setMove(Boolean move) {
		this.move = move;
	}

	public List<CommunicationRequestDto> getCommunicationRequests() {
		return this.communicationRequests;
	}

	public void setCommunicationRequests(List<CommunicationRequestDto> communicationRequests) {
		this.communicationRequests = communicationRequests;
	}

	@Override
	public String toString() {
		return "SurveyUnitDetailDto [id=" + id
				+ ", address=" + address
				+ ", priority=" + priority
				+ ", campaign=" + campaign
				+ ", comments=" + comments
				+ ", sampleIdentifiers=" + sampleIdentifiers
				+ ", states=" + states
				+ ", identification=" + identification
				+ ", contactAttempts=" + contactAttempts
				+ ", contactOutcome=" + contactOutcome
				+ ", mailRequests=" + communicationRequests
				+ ", move=" + move + "]";
	}

	/**
	 * This method checks if mandatory fields in the Survey Unit are valid or not
	 * 
	 * @return boolean
	 */
	@JsonIgnore
	public boolean isValid() {
		return (StringUtils.isNotBlank(this.id)
				&& StringUtils.isNotBlank(this.campaign)
				&& (this.states != null && !this.states.isEmpty()) && this.address != null
				&& this.sampleIdentifiers != null && this.priority != null);
	}

}
