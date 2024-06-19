package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.FetchType;

import jakarta.persistence.ManyToOne;

/**
 * Entity MessageRecipient : represent the entity table in DB
 * 
 * @author Paul Guillemet
 * 
 */

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
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

	public CampaignMessageRecipient(Message message, Campaign campaign) {
		super();
		this.message = message;
		this.campaign = campaign;
	}

}
