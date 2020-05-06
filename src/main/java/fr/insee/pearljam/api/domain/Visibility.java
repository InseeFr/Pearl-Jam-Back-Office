package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
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
	 * The start date of Visibility
	 */
	public Long collectionStartDate;
	
	/**
	 * The end date of Visibility
	 */
	public Long collectionEndDate;

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
	 * @return the collection start date for the visibility
	 */
	public Long getCollectionStartDate() {
		return collectionStartDate;
	}

	/**
	 * @param collectionStartDate for the visibility
	 */
	public void setCollectionStartDate(Long collectionStartDate) {
		this.collectionStartDate = collectionStartDate;
	}

	/**
	 * @return the collection end date for the visibility
	 */
	public Long getCollectionEndDate() {
		return collectionEndDate;
	}

	/**
	 * @param collectionEndDate for the visibility
	 */
	public void setCollectionEndDate(Long collectionEndDate) {
		this.collectionEndDate = collectionEndDate;
	}

}
