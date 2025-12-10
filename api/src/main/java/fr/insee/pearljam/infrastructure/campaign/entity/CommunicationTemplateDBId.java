package fr.insee.pearljam.infrastructure.campaign.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

import lombok.*;

/**
 * @author camille corbin
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CommunicationTemplateDBId implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * The meshuggah Id
	 */
	@Column(name = "meshuggah_id")
	private String meshuggahId;

	/**
	 * The campaign Id
	 */
	@Column(name = "campaign_id")
	private String campaignId;

}
