package fr.insee.pearljam.api.dto.campaign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDto {
	private String id;
	private String label;
	private Long managementStartDate;
	private Long interviewerStartDate;
	private Long identificationPhaseStartDate;
	private Long collectionStartDate;
	private Long collectionEndDate;
	private Long endDate;
	private Long allocated;
	private Long toProcessInterviewer;
	private Long toAffect;
	private Long toFollowUp;
	private Long toReview;
	private Long finalized;
	private Boolean preference;

	
	
	public CampaignDto() {
		super();
	}
	
	public CampaignDto(String id, String label) {
		super();
		this.id = id;
		this.label = label;
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
	 * @return the toProcessInterviewer
	 */
	public Long getToProcessInterviewer() {
		return toProcessInterviewer;
	}

	/**
	 * @param toProcessInterviewer the toProcessInterviewer to set
	 */
	public void setToProcessInterviewer(Long toProcessInterviewer) {
		this.toProcessInterviewer = toProcessInterviewer;
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
	 * @return the toFollowUp
	 */
	public Long getToFollowUp() {
		return toFollowUp;
	}

	/**
	 * @param toFollowUp the toFollowUp to set
	 */
	public void setToFollowUp(Long toFollowUp) {
		this.toFollowUp = toFollowUp;
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

	/**
	 * @return the finalized
	 */
	public Long getFinalized() {
		return finalized;
	}

	/**
	 * @param finalized the finalized to set
	 */
	public void setFinalized(Long finalized) {
		this.finalized = finalized;
	}

	/**
	 * @return the preference
	 */
	public Boolean getPreference() {
		return preference;
	}

	/**
	 * @param preference the preference to set
	 */
	public void setPreference(Boolean preference) {
		this.preference = preference;
	}

}
