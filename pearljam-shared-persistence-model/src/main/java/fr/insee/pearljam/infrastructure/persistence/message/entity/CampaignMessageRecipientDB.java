package fr.insee.pearljam.infrastructure.persistence.message.entity;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity MessageRecipient : represent the entity table in DB
 * 
 * @author Paul Guillemet
 * 
 */

@Entity
@Table(name = "campaign_message_recipient", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class CampaignMessageRecipientDB implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CampaignMessageRecipientDBId messageRecipientId;

	/**
	 * The id of MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", insertable = false, updatable = false)
	private MessageDB message;

	/**
	 * The first name of the MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "campaign_id", insertable = false, updatable = false)
	private CampaignDB campaign;

}
