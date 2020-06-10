package fr.insee.pearljam.api.dto.campaign;

import fr.insee.pearljam.api.domain.Campaign;

public class CampaignDto {
	private String id;
	private String label;
	private Long collectionStartDate;
	private Long collectionEndDate;
	
	
	public CampaignDto(String id, String label, Long collectionStartDate, Long collectionEndDate) {
		super();
		this.id = id;
		this.label = label;
		this.collectionStartDate = collectionStartDate;
		this.collectionEndDate = collectionEndDate;
	}
	
	public CampaignDto(Campaign campaign) {
		super();
		this.id = campaign.getId();
		this.label = campaign.getLabel();
		this.collectionStartDate = campaign.getCollectionStartDate();
		this.collectionEndDate = campaign.getCollectionEndDate();
	}
	
	
	public CampaignDto() {
		super();
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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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
