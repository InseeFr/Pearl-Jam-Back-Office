package fr.insee.pearljam.api.dto.surveyunit;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.geographicallocation.GeographicalLocationDto;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;
import fr.insee.pearljam.api.dto.state.StateDto;

public class SurveyUnitDetailDto {
	private String id;
	private String firstName;
	private String lastName;
	private List<String> phoneNumbers;
	private AddressDto address;
	private GeographicalLocationDto geographicalLocation;
	private Boolean priority;
	private String campaign;
	private List<CommentDto> comments;
	private SampleIdentifiersDto sampleIdentifiers;
	private StateDto lastState;
	private List<StateDto> states;
	private List<ContactAttemptDto> contactAttempts;
	private ContactOutcomeDto contactOutcome;

	public SurveyUnitDetailDto() {
	}

	public SurveyUnitDetailDto(SurveyUnit surveyUnit) {
		this.id=surveyUnit.getId();
		this.firstName=surveyUnit.getFirstName();
		this.lastName=surveyUnit.getLasttName();
		this.phoneNumbers=surveyUnit.getPhoneNumbers();
		this.priority=surveyUnit.isPriority();
		this.campaign=surveyUnit.getCampaign().getId();
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
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the phoneNumbers
	 */
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers the phoneNumbers to set
	 */
	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
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
	 * @return the geographicalLocation
	 */
	public GeographicalLocationDto getGeographicalLocation() {
		return geographicalLocation;
	}

	/**
	 * @param geographicalLocation the geographicalLocation to set
	 */
	public void setGeographicalLocation(GeographicalLocationDto geographicalLocation) {
		this.geographicalLocation = geographicalLocation;
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
	 * @return the lastState
	 */
	public StateDto getLastState() {
		return lastState;
	}

	/**
	 * @param state the lastState to set
	 */
	public void setLastState(StateDto state) {
		this.lastState = state;
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
	
	@Override
	public String toString() {
		return "SurveyUnitDetailDto [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", phoneNumbers=" + phoneNumbers + ", address=" + address + ", geographicalLocation="
				+ geographicalLocation + ", priority=" + priority + ", campaign=" + campaign + ", comments=" + comments
				+ ", sampleIdentifiers=" + sampleIdentifiers + ", lastState=" + lastState + ", states=" + states
				+ ", contactAttempts=" + contactAttempts + ", contactOutcome=" + contactOutcome + "]";
	}
	
	/**
	 * This method checks if mandatory fields in the Survey Unit are valid or not
	 * @return boolean
	 */
	@JsonIgnore
	public boolean isValid() {
		return (StringUtils.isNotBlank(this.id) && StringUtils.isNotBlank(this.firstName) && StringUtils.isNotBlank(this.lastName) 
				&& StringUtils.isNotBlank(this.campaign) && (this.phoneNumbers != null && !this.phoneNumbers.isEmpty()) 
				&& (this.states != null && !this.states.isEmpty()) && this.address != null && this.geographicalLocation != null 
				&& this.sampleIdentifiers != null && this.priority != null);
	}
	
	
}
