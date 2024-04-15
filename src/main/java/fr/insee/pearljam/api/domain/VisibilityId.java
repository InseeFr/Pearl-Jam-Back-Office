package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * This class is used to defines the association between OrganizationUnit and
 * Campaign tables.
 * 
 * @author scorcaud
 */
@Embeddable
public class VisibilityId implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The organizationUnit Id
	 */
	@Column(name = "organization_unit_id")
	private String organizationUnitId;

	/**
	 * The campaign Id
	 */
	@Column(name = "campaign_id")
	private String campaignId;

	/**
	 * Default constructor for the entity
	 */
	public VisibilityId() {

	}

	/**
	 * Constructor for the entity
	 * 
	 * @param organizationUnitId
	 * @param campaignId
	 */
	public VisibilityId(String organizationUnitId, String campaignId) {
		super();
		this.organizationUnitId = organizationUnitId;
		this.campaignId = campaignId;
	}

	/**
	 * @return the organizationUnitId
	 */
	public String getOrganizationUnitId() {
		return organizationUnitId;
	}

	/**
	 * @param organizationUnitId the organizationUnitId to set
	 */
	public void setOrganizationUnitId(String organizationUnitId) {
		this.organizationUnitId = organizationUnitId;
	}

	/**
	 * @return the campaignId
	 */
	public String getCampaignId() {
		return campaignId;
	}

	/**
	 * @param campaignId the campaignId to set
	 */
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

}
