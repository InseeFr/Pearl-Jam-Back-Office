package fr.insee.pearljam.api.dto.campaign;

import fr.insee.pearljam.api.domain.Campaign;

public class CampaignDto {
	private String id;
	private String label;
	private Long collectionStartDate;
	private Long collectionEndDate;
	private Long treatmentEndDate;
	private Long affected;
	private Long toAffect;
	private Long inProgress;
	private Long toControl;
	
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
	
	public CampaignDto(String id, String label, Long collectionStartDate, Long collectionEndDate, Long treatmentEndDate,
			Long affected, Long toAffect, Long inProgress, Long toControl) {
		super();
		this.id = id;
		this.label = label;
		this.collectionStartDate = collectionStartDate;
		this.collectionEndDate = collectionEndDate;
		this.treatmentEndDate = treatmentEndDate;
		this.affected = affected;
		this.toAffect = toAffect;
		this.inProgress = inProgress;
		this.toControl = toControl;
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

	/**
	 * @return the treatmentEndDate
	 */
	public Long getTreatmentEndDate() {
		return treatmentEndDate;
	}

	/**
	 * @param treatmentEndDate the treatmentEndDate to set
	 */
	public void setTreatmentEndDate(Long treatmentEndDate) {
		this.treatmentEndDate = treatmentEndDate;
	}

	/**
	 * @return the affected
	 */
	public Long getAffected() {
		return affected;
	}

	/**
	 * @param affected the affected to set
	 */
	public void setAffected(Long affected) {
		this.affected = affected;
	}

	/**
	 * @return the toAffect
	 */
	public Long getToAffect() {
		return toAffect;
	}

	/**
	 * @param toAffect the toAffect to set
	 */
	public void setToAffect(Long toAffect) {
		this.toAffect = toAffect;
	}

	/**
	 * @return the inProgress
	 */
	public Long getInProgress() {
		return inProgress;
	}

	/**
	 * @param inProgress the inProgress to set
	 */
	public void setInProgress(Long inProgress) {
		this.inProgress = inProgress;
	}

	/**
	 * @return the toControl
	 */
	public Long getToControl() {
		return toControl;
	}

	/**
	 * @param toControl the toControl to set
	 */
	public void setToControl(Long toControl) {
		this.toControl = toControl;
	}
	
	
	
	
}
