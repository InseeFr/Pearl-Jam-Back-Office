package fr.insee.pearljam.api.dto.campaign;

import fr.insee.pearljam.api.domain.Campaign;

import java.util.List;

public class CampaignDto {
	private String id;
	private String label;
	private Long collectionStartDate;
	private Long collectionEndDate;
	private Long visibilityStartDate;
  private Long treatmentEndDate;
  private Long allocated;
  private Long toProcessInterviewer;
  private Long toAffect;
	private Long toFollowUp;
  private Long toReview;
	private Long finalized;
	private Boolean preference;
	
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
	
	public CampaignDto(String id, String label, Long collectionStartDate, Long collectionEndDate, Long visibilityStartDate, Long treatmentEndDate,
			Long allocated, Long toProcessInterviewer, Long toAffect, Long toFollowUp, Long toReview, Long finalized, Boolean preference) {
		super();
		this.id = id;
		this.label = label;
		this.collectionStartDate = collectionStartDate;
		this.collectionEndDate = collectionEndDate;
		this.visibilityStartDate = visibilityStartDate;
    this.treatmentEndDate = treatmentEndDate;
    this.allocated = allocated;
    this.toProcessInterviewer = toProcessInterviewer;
    this.toAffect = toAffect;
    this.toFollowUp = toFollowUp;
    this.toReview = toReview;
    this.finalized = finalized;
    this.preference = preference;
  }
  
	
	public CampaignDto() {
		super();
  }
  
  public void setCampaignStats(List<Object[]> obj) {
		if (obj != null && !obj.isEmpty() && obj.get(0).length > 3 && obj.get(0)[0] != null) {
      this.toProcessInterviewer = ((java.math.BigInteger) obj.get(0)[0]).longValue();
      this.toReview = ((java.math.BigInteger) obj.get(0)[1]).longValue();
      this.finalized = ((java.math.BigInteger) obj.get(0)[2]).longValue();
			this.allocated = ((java.math.BigInteger) obj.get(0)[3]).longValue();
			this.toAffect = 0L;
			this.toFollowUp = 0L;
		}
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

	public Long getVisibilityStartDate() {
		return visibilityStartDate;
	}

	public void setVisibilityStartDate(Long visibilityStartDate) {
		this.visibilityStartDate = visibilityStartDate;
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
	 * @return the allocated
	 */
	public Long getAllocated() {
		return allocated;
	}

	/**
	 * @param allocated the allocated to set
	 */
	public void setAllocated(Long allocated) {
		this.allocated = allocated;
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
	 * @return the toProcessInterviewer
	 */
	public Long getToProcessInterviewer() {
		return toProcessInterviewer;
	}

	/**
	 * @param toProcessInterviewer the inProgress to set
	 */
	public void setToProcessInterviewer(Long toProcessInterviewer) {
		this.toProcessInterviewer = toProcessInterviewer;
	}

	/**
	 * @return the toReview
	 */
	public Long getToReview() {
		return toReview;
	}

	/**
	 * @param toReview the toReview to set
	 */
	public void setToReview(Long toReview) {
		this.toReview = toReview;
	}

	public Long getFinalized() {
		return finalized;
	}

	public void setFinalized(Long finalized) {
		this.finalized = finalized;
	}

	public Long getToFollowUp() {
		return toFollowUp;
	}

	public void setToFollowUp(Long toFollowUp) {
		this.toFollowUp = toFollowUp;
	}

	public Boolean getPreference() {
		return preference;
	}

	public void setPreference(Boolean preference) {
		this.preference = preference;
	}
	
	
	
	
}
