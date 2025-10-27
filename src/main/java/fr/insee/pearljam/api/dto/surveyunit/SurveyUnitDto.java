package fr.insee.pearljam.api.dto.surveyunit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.surveyunit.dto.PersonDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitVisibilityDto;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
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
	private boolean collectNextContacts;
	private boolean useLetterCommunication;

	private List<PersonDto> persons;

	private AddressDto address;

	public SurveyUnitDto(String idSurveyUnit, CampaignDto campaign, SurveyUnitVisibilityDto visibility) {
		this.id = idSurveyUnit;
		this.campaign = campaign.getId();
		this.campaignLabel = campaign.getLabel();
		this.managementStartDate = visibility.managementStartDate();
		this.interviewerStartDate = visibility.interviewerStartDate();
		this.identificationPhaseStartDate = visibility.identificationPhaseStartDate();
		this.collectionStartDate = visibility.collectionStartDate();
		this.collectionEndDate = visibility.collectionEndDate();
		this.endDate = visibility.endDate();
		this.useLetterCommunication = visibility.useLetterCommunication();
		this.identificationConfiguration = campaign.getIdentificationConfiguration();
		this.contactAttemptConfiguration = campaign.getContactAttemptConfiguration();
		this.contactOutcomeConfiguration = campaign.getContactOutcomeConfiguration();
		this.collectNextContacts = campaign.isCollectNextContacts();
	}
}
