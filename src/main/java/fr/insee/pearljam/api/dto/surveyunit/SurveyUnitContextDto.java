package fr.insee.pearljam.api.dto.surveyunit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.closingcause.ClosingCauseDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;
import fr.insee.pearljam.api.dto.state.StateDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitContextDto {
	private String id;
	private List<PersonDto> persons;
	private AddressDto address;
	private String organizationUnitId;
	private Boolean priority;
	private String campaign;
	private SampleIdentifiersDto sampleIdentifiers;
	private List<StateDto> states;
	private List<ContactAttemptDto> contactAttempts;
	private ContactOutcomeDto contactOutcome;
	private ClosingCauseDto closingCause;

	public SurveyUnitContextDto() {
	}
	
	public SurveyUnitContextDto(String id, List<PersonDto> persons, AddressDto address, 
			String organizationUnitId, Boolean priority, String campaign, SampleIdentifiersDto sampleIdentifiers,
			List<StateDto> states, List<ContactAttemptDto> contactAttempts, ContactOutcomeDto contactOutcome, ClosingCauseDto closingCause) {
		super();
		this.id = id;
		this.persons = persons;
		this.address = address;
		this.organizationUnitId = organizationUnitId;
		this.priority = priority;
		this.campaign = campaign;
		this.sampleIdentifiers = sampleIdentifiers;
		this.states = states;
		this.contactAttempts = contactAttempts;
		this.contactOutcome = contactOutcome;
		this.closingCause = closingCause;
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
	 * @return the persons
	 */
	public List<PersonDto> getPersons() {
		return persons;
	}

	/**
	 * @param persons the persons to set
	 */
	public void setPersons(List<PersonDto> persons) {
		this.persons = persons;
	}

	/**
	 * @return the address
	 */
	public AddressDto getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(AddressDto address) {
		this.address = address;
	}

	/**
	 * @return the organizationUnitId
	 */
	public String getOrganizationUnitId() {
		return organizationUnitId;
	}
	/**
	 * @param organizationUnitId the organizationUnitId to set
	 */
	public void setOrganizationUnitId(String organizationUnitId) {
		this.organizationUnitId = organizationUnitId;
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
	 * @return the sampleIdentifiers
	 */
	public SampleIdentifiersDto getSampleIdentifiers() {
		return sampleIdentifiers;
	}

	/**
	 * @param sampleIdentifiers the sampleIdentifiers to set
	 */
	public void setSampleIdentifiers(SampleIdentifiersDto sampleIdentifiers) {
		this.sampleIdentifiers = sampleIdentifiers;
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

	/**
	 * @return the closingCause
	 */
	public ClosingCauseDto getClosingCause() {
		return closingCause;
	}

	/**
	 * @param closingCause the closingCause to set
	 */
	public void setClosingCause(ClosingCauseDto closingCause) {
		this.closingCause = closingCause;
	}

	/**
	 * This method checks if mandatory fields in the Survey Unit are valid or not
	 * @return boolean
	 */
	@JsonIgnore
	public boolean isValid() {
		return (this.id!=null && !this.id.isBlank()
				&& this.campaign!=null && !this.campaign.isBlank()
				&& this.address != null				
				&& this.persons != null && !this.persons.isEmpty()
				&& this.sampleIdentifiers != null
				&& this.priority != null);
	}
}
