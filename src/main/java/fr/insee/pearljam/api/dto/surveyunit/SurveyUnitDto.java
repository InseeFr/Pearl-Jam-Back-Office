package fr.insee.pearljam.api.dto.surveyunit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

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
	
	private List<PersonDto> persons;
	
	private AddressDto address;
	
	/**
	 * Default constructor from SurveyUnitDto
	 * @param surveyUnit
	 */
	public SurveyUnitDto(SurveyUnit surveyUnit) {
		this.id = surveyUnit.getId();
		this.campaign = surveyUnit.getCampaign().getId();
		this.campaignLabel = surveyUnit.getCampaign().getLabel();
		this.collectionStartDate = surveyUnit.getCampaign().getStartDate();
	}
	public SurveyUnitDto() {
	}
	
	public SurveyUnitDto(Optional<SurveyUnit> su, VisibilityDto visibility, Boolean extended) {
		if(su.isPresent()) {
			this.id = su.get().getId();
			this.campaign = su.get().getCampaign().getId();
			this.campaignLabel = su.get().getCampaign().getLabel();
			this.collectionStartDate = su.get().getCampaign().getStartDate();
			this.managementStartDate=visibility.getManagementStartDate();
			this.interviewerStartDate=visibility.getInterviewerStartDate();
			this.identificationPhaseStartDate=visibility.getIdentificationPhaseStartDate();
			this.collectionStartDate=visibility.getCollectionStartDate();
			this.collectionEndDate=visibility.getCollectionEndDate();
			this.endDate=visibility.getEndDate();
			if(Boolean.TRUE.equals(extended)) {
				this.persons = su.get().getPersons().stream()
						.map(person -> new PersonDto(person))
						.collect(Collectors.toList());
				this.address = new AddressDto(su.get().getAddress());
			}
		}
	}
	
	public SurveyUnitDto(String idSurveyUnit, CampaignDto campaign, VisibilityDto visibility) {
		this.id=idSurveyUnit;
		this.campaign=campaign.getId();
		this.campaignLabel=campaign.getLabel();
		this.managementStartDate=visibility.getManagementStartDate();
		this.interviewerStartDate=visibility.getInterviewerStartDate();
		this.identificationPhaseStartDate=visibility.getIdentificationPhaseStartDate();
		this.collectionStartDate=visibility.getCollectionStartDate();
		this.collectionEndDate=visibility.getCollectionEndDate();
		this.endDate=visibility.getEndDate();
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
	
}
