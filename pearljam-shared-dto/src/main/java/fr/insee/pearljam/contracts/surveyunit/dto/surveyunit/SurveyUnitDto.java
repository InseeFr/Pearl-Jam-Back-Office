package fr.insee.pearljam.contracts.surveyunit.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SurveyUnitDto {
	private String id;
	private String campaign;
	private String campaignLabel;

	private Long managementStartDate;
	private Long interviewerStartDate;
	private Long identificationPhaseStartDate;
	private Long collectionStartDate;
	private Long collectionEndDate;
	private Long endDate;

	private IdentificationConfiguration identificationConfiguration;
	private ContactOutcomeConfiguration contactOutcomeConfiguration;
	private ContactAttemptConfiguration contactAttemptConfiguration;
	private boolean collectNextContacts;
	private boolean useLetterCommunication;

	private long lastUpdated;

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
		this.lastUpdated = 0L; // Default value, will be set from repository
	}
}
