package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class VisibilityId implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "organization_unit_id")
    private String organizationUnitId;
 
    @Column(name = "campaign_id")
    private String campaignId;
  
    public VisibilityId() {
    	
    }
    
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
