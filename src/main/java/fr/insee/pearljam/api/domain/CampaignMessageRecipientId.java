package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * This class is used to defines the association between OrganizationUnit and
 * Campaign tables.
 * @author Guillemet Paul
 */
@Embeddable
public class CampaignMessageRecipientId implements Serializable{

	
	private static final long serialVersionUID = 1L;
  
	/**
	 * The organizationUnit Id 
	 */
	@Column(name = "message_id")
	private Long messageId;
 
	/**
	 * The campaign Id
	 */
	@Column(name = "campaign_id", nullable = true)
	private String campaignId;
  
	/**
	 * Default constructor for the entity
	 */
	public CampaignMessageRecipientId() {
	}
  
	public CampaignMessageRecipientId(Long messageId, String campaignId) {
	    super();
	    this.messageId = messageId;
	    this.campaignId = campaignId;
	}

	/**
	 * @return the campaignId
	 */
	public Long getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId the campaignId to set
	 */
	public void setCampaignId(Long messageId) {
		this.messageId = messageId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return false;
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getMessageId(), getCampaignId());
    }
  
    /**
	 * @return the campaignId
	 */
	public String getCampaignId() {
		return campaignId;
	}

	/**
	 * @param InterviewerId the campaignId to set
	 */
	public void CampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

}
