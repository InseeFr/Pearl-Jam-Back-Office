package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;

import javax.persistence.FetchType;



import javax.persistence.ManyToOne;

/**
* Entity MessageRecipient : represent the entity table in DB
* 
* @author Paul Guillemet
* 
*/

@Entity
@Table
public class CampaignMessageRecipient implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CampaignMessageRecipientId messageRecipientId;

	/**
 	* The id of MessageRecipient
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", insertable = false, updatable = false)
	private Message message;

	/**
	* The first name of the MessageRecipient 
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "campaign_id", insertable = false, updatable = false)
	private Campaign campaign;
	
	public CampaignMessageRecipient(){
		super();
	}

	public CampaignMessageRecipient(Message message, Campaign campaign) {
		super();
		this.message = message;
		this.campaign = campaign;
	}
  
  	/**
	 * @return id of comment
	 */
	public CampaignMessageRecipientId getId() {
		return messageRecipientId;
	}
  
	public void setId(CampaignMessageRecipientId messageRecipientId) {
		this.messageRecipientId = messageRecipientId;
	}

	/**
	 * @return the text of the MessageRecipient
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param text of the MessageRecipient
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * @return the last name of the MessageRecipient
	 */
	public Campaign getCampaign() {
		return campaign;
	}

	/**
	 * @param the last name of the MessageRecipient
	 */
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}
