package fr.insee.pearljam.api.domain;

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
@Table
@Getter
@Setter
@NoArgsConstructor
public class CampaignMessageRecipient implements Serializable {

	@Serial
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

}
