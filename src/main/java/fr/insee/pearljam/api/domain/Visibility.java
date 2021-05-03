package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
* Entity Visibility : represent the entity table in DB
* 
* @author Corcaud Samuel
* 
*/
@Entity
@Table
public class Visibility implements Serializable {

	private static final long serialVersionUID = 1L;
		
	@EmbeddedId
	private VisibilityId visibilityId;
	/**
	* The organizationUnit associated to Visibility 
	*/
	@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("organization_unit_id")
    private OrganizationUnit organizationUnit;
    
	/**
	* The Campaign associated to Visibility 
	*/
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("campaign_id")
    private Campaign campaign;
	/**
	 * The start date of Visibility of management
	 */
	private Long managementStartDate;
	
	/**
	 * The start date of Visibility of interviewer
	 */
	private Long interviewerStartDate;
	
	/**
	 * The start date of identification phase
	 */
	private Long identificationPhaseStartDate;
	
	/**
	 * The start date of collection
	 */
	private Long collectionStartDate;
	
	/**
	 * The end date of collection
	 */
	private Long collectionEndDate;
	
	/**
	 * The end date
	 */
	private Long endDate;
	
	public Visibility() {
		super();
	}

	/**
	 * @return the organizationUnit
	 */
	public OrganizationUnit getOrganizationUnit() {
		return organizationUnit;
	}

	/**
	 * @param organizationUnit the organizationUnit to set
	 */
	public void setOrganizationUnit(OrganizationUnit organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	/**
	 * @return the campaign
	 */
	public Campaign getCampaign() {
		return campaign;
	}

	/**
	 * @param campaign the campaign to set
	 */
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	/**
	 * @return the visibilityId
	 */
	public VisibilityId getVisibilityId() {
		return visibilityId;
	}

	/**
	 * @param visibilityId the visibilityId to set
	 */
	public void setVisibilityId(VisibilityId visibilityId) {
		this.visibilityId = visibilityId;
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
	
	public boolean checkDateConsistency() {
		return managementStartDate < interviewerStartDate && interviewerStartDate < identificationPhaseStartDate
				&& identificationPhaseStartDate < collectionStartDate && collectionStartDate < collectionEndDate
				&& collectionEndDate < endDate;
	}

}
