package fr.insee.pearljam.api.dto.surveyunit;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;

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
	private Long collectionStartDate;
	
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
	public SurveyUnitDto(String idSurveyUnit, CampaignDto campaign) {
		this.id=idSurveyUnit;
		this.campaign=campaign.getId();
		this.campaignLabel=campaign.getLabel();
		this.collectionStartDate=campaign.getStartDate();
	}
	
	public SurveyUnitDto(String idSurveyUnit, CampaignDto campaign, VisibilityDto visibility) {
		this.id=idSurveyUnit;
		this.campaign=campaign.getId();
		this.campaignLabel=campaign.getLabel();
		this.collectionStartDate=visibility.getCollectionStartDate();
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
}
