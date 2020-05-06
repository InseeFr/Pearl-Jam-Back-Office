package fr.insee.pearljam.api.dto.surveyunit;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;

public class SurveyUnitDto {
	
	/**
	 * Id of the SurveyUnit
	 */
	String id;
	
	/**
	 * Id of the Campaign
	 */
	String campaign;
	
	/**
	 * Label of the Campaign
	 */
	String campaignLabel;
	
	/**
	 * Start Date of the Campaign
	 */
	Long collectionStartDate;
	
	/**
	 * End Date of the Campaign
	 */
	Long collectionEndDate;
	
	/**
	 * Default constructor from SurveyUnitDto
	 * @param surveyUnit
	 */
	public SurveyUnitDto(SurveyUnit surveyUnit) {
		this.id = surveyUnit.getId();
		this.campaign = surveyUnit.getCampaign().getId();
		this.campaignLabel = surveyUnit.getCampaign().getLabel();
		this.collectionStartDate = surveyUnit.getCampaign().getCollectionStartDate();
		this.collectionEndDate = surveyUnit.getCampaign().getCollectionEndDate();
	}
	public SurveyUnitDto() {
	}
	public SurveyUnitDto(String idSurveyUnit, CampaignDto campaign) {
		this.id=idSurveyUnit;
		this.campaign=campaign.getId();
		this.campaignLabel=campaign.getLabel();
		this.collectionStartDate=campaign.getCollectionStartDate();
		this.collectionEndDate=campaign.getCollectionEndDate();
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
}
