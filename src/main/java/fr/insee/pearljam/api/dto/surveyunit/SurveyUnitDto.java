package fr.insee.pearljam.api.dto.surveyunit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitDto {
	/**
	 * Id of the SurveyUnit
	 */
	private String id;

	/**
	 * Id of the Campaign
	 */
	private String campaign;

	/**
	 * Label of the Campaign
	 */
	private String campaignLabel;

	/**
	 * Start Date of the Campaign
	 */
	private Long managementStartDate;

	/**
	 * Start Date of the Campaign
	 */
	private Long interviewerStartDate;

	/**
	 * Start Date of the Campaign
	 */
	private Long identificationPhaseStartDate;

	/**
	 * Start Date of the Campaign
	 */
	private Long collectionStartDate;

	/**
	 * Start Date of the Campaign
	 */
	private Long collectionEndDate;

	/**
	 * Start Date of the Campaign
	 */
	private Long endDate;

	private IdentificationConfiguration identificationConfiguration;
	private ContactOutcomeConfiguration contactOutcomeConfiguration;
	private ContactAttemptConfiguration contactAttemptConfiguration;
	private boolean communicationRequestConfiguration;

	private List<PersonDto> persons;

	private AddressDto address;

	public SurveyUnitDto() {
	}

	public SurveyUnitDto(SurveyUnit su, VisibilityDto visibility, Boolean extended) {
		this.id = su.getId();
		this.campaign = su.getCampaign().getId();
		this.campaignLabel = su.getCampaign().getLabel();
		this.managementStartDate = visibility.getManagementStartDate();
		this.interviewerStartDate = visibility.getInterviewerStartDate();
		this.identificationPhaseStartDate = visibility.getIdentificationPhaseStartDate();
		this.collectionStartDate = visibility.getCollectionStartDate();
		this.collectionEndDate = visibility.getCollectionEndDate();
		this.endDate = visibility.getEndDate();
		this.identificationConfiguration = su.getCampaign().getIdentificationConfiguration();
		this.contactAttemptConfiguration = su.getCampaign().getContactAttemptConfiguration();
		this.contactOutcomeConfiguration = su.getCampaign().getContactOutcomeConfiguration();
		this.communicationRequestConfiguration = Optional.ofNullable(su.getCampaign().getCommunicationConfiguration())
				.orElse(false);
		if (Boolean.TRUE.equals(extended)) {
			this.persons = su.getPersons().stream()
					.map(person -> new PersonDto(person))
					.collect(Collectors.toList());
			this.address = new AddressDto(su.getAddress());
		}
	}

	public SurveyUnitDto(String idSurveyUnit, CampaignDto campaign, VisibilityDto visibility) {
		this.id = idSurveyUnit;
		this.campaign = campaign.getId();
		this.campaignLabel = campaign.getLabel();
		this.managementStartDate = visibility.getManagementStartDate();
		this.interviewerStartDate = visibility.getInterviewerStartDate();
		this.identificationPhaseStartDate = visibility.getIdentificationPhaseStartDate();
		this.collectionStartDate = visibility.getCollectionStartDate();
		this.collectionEndDate = visibility.getCollectionEndDate();
		this.endDate = visibility.getEndDate();
		this.identificationConfiguration = campaign.getIdentificationConfiguration();
		this.contactAttemptConfiguration = campaign.getContactAttemptConfiguration();
		this.contactOutcomeConfiguration = campaign.getContactOutcomeConfiguration();
		this.communicationRequestConfiguration = Optional.of(campaign.getCommunicationRequestConfiguration())
				.orElse(false);
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
	 * @return the idCampaign
	 */
	public String getCampaign() {
		return campaign;
	}

	/**
	 * @param idCampaign the idCampaign to set
	 */
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	/**
	 * @return the labelCampaign
	 */
	public String getCampaignLabel() {
		return campaignLabel;
	}

	/**
	 * @param labelCampaign the labelCampaign to set
	 */
	public void setCampaignLabel(String campaignLabel) {
		this.campaignLabel = campaignLabel;
	}

	/**
	 * @return the managementStartDate
	 */
	public Long getManagementStartDate() {
		return managementStartDate;
	}

	/**
	 * @param managementStartDate the managementStartDate to set
	 */
	public void setManagementStartDate(Long managementStartDate) {
		this.managementStartDate = managementStartDate;
	}

	/**
	 * @return the interviewerStartDate
	 */
	public Long getInterviewerStartDate() {
		return interviewerStartDate;
	}

	/**
	 * @param interviewerStartDate the interviewerStartDate to set
	 */
	public void setInterviewerStartDate(Long interviewerStartDate) {
		this.interviewerStartDate = interviewerStartDate;
	}

	/**
	 * @return the identificationPhaseStartDate
	 */
	public Long getIdentificationPhaseStartDate() {
		return identificationPhaseStartDate;
	}

	/**
	 * @param identificationPhaseStartDate the identificationPhaseStartDate to set
	 */
	public void setIdentificationPhaseStartDate(Long identificationPhaseStartDate) {
		this.identificationPhaseStartDate = identificationPhaseStartDate;
	}

	/**
	 * @return the collectionStartDate
	 */
	public Long getCollectionStartDate() {
		return collectionStartDate;
	}

	/**
	 * @param collectionStartDate the collectionStartDate to set
	 */
	public void setCollectionStartDate(Long collectionStartDate) {
		this.collectionStartDate = collectionStartDate;
	}

	/**
	 * @return the collectionEndDate
	 */
	public Long getCollectionEndDate() {
		return collectionEndDate;
	}

	/**
	 * @param collectionEndDate the collectionEndDate to set
	 */
	public void setCollectionEndDate(Long collectionEndDate) {
		this.collectionEndDate = collectionEndDate;
	}

	/**
	 * @return the endDate
	 */
	public Long getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public List<PersonDto> getPersons() {
		return persons;
	}

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

	public IdentificationConfiguration getIdentificationConfiguration() {
		return this.identificationConfiguration;
	}

	public void setIdentificationConfiguration(IdentificationConfiguration identificationConfiguration) {
		this.identificationConfiguration = identificationConfiguration;
	}

	public ContactOutcomeConfiguration getContactOutcomeConfiguration() {
		return this.contactOutcomeConfiguration;
	}

	public void setContactOutcomeConfiguration(ContactOutcomeConfiguration contactOutcomeConfiguration) {
		this.contactOutcomeConfiguration = contactOutcomeConfiguration;
	}

	public ContactAttemptConfiguration getContactAttemptConfiguration() {
		return this.contactAttemptConfiguration;
	}

	public void setContactAttemptConfiguration(ContactAttemptConfiguration contactAttemptConfiguration) {
		this.contactAttemptConfiguration = contactAttemptConfiguration;
	}

	public boolean isCommunicationRequestConfiguration() {
		return communicationRequestConfiguration;
	}

	public void setCommunicationRequestConfiguration(boolean communicationRequestConfiguration) {
		this.communicationRequestConfiguration = communicationRequestConfiguration;
	}

}
