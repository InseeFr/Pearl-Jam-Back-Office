package fr.insee.pearljam.infrastructure.campaign.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is used to defines the association between OrganizationUnit and
 * Campaign tables.
 * 
 * @author scorcaud
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommunicationTemplateDBId implements Serializable {

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
