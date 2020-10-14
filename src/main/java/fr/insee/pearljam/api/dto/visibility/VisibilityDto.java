package fr.insee.pearljam.api.dto.visibility;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisibilityDto {
	
	/**
	 * Collection start date of the visibility
	 */
	private Long collectionStartDate;
	
	/**
	 * Collection end date of the visibility
	 */
	private Long collectionEndDate;
	
	/**
	 * Identification phase start date of the visibility
	 */
	private Long identificationPhaseStartDate;
	
	/**
	 * interviewer start date of the visibility
	 */
	private Long interviewerStartDate;
	
	/**
	 * Manager start date of the visibility
	 */
	private Long managementStartDate;
	
	/**
	 * End date of the visibility
	 */
	private Long endDate;

	
	
	public VisibilityDto(Long collectionstartDate, Long collectionEndDate, Long identificationPhaseStartDate,
			Long interviewerStartDate, Long managerStartDate, Long endDate) {
		super();
		this.collectionStartDate = collectionstartDate;
		this.collectionEndDate = collectionEndDate;
		this.identificationPhaseStartDate = identificationPhaseStartDate;
		this.interviewerStartDate = interviewerStartDate;
		this.managementStartDate = managerStartDate;
		this.endDate = endDate;
	}
	
	public VisibilityDto(Long collectionstartDate) {
		super();
		this.collectionStartDate = collectionstartDate;
	}
	
	public VisibilityDto() {
		super();
	}

	/**
	 * @return the collectionstartDate
	 */
	public Long getCollectionStartDate() {
		return collectionStartDate;
	}

	/**
	 * @param collectionstartDate the collectionstartDate to set
	 */
	public void setCollectionStartDate(Long collectionstartDate) {
		this.collectionStartDate = collectionstartDate;
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
	 * @return the managerStartDate
	 */
	public Long getManagementStartDate() {
		return managementStartDate;
	}

	/**
	 * @param managementStartDate the managerStartDate to set
	 */
	public void setManagementStartDate(Long managementStartDate) {
		this.managementStartDate = managementStartDate;
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

	public boolean isOneDateFilled() {
		return collectionStartDate != null || collectionEndDate!=null || identificationPhaseStartDate!=null ||
		interviewerStartDate!=null || managementStartDate!=null || endDate!=null;
	}
	
	
	
}
