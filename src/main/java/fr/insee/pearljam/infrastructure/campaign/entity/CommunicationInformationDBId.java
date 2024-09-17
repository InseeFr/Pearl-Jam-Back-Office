package fr.insee.pearljam.infrastructure.campaign.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

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
public class CommunicationInformationDBId implements Serializable {

	@Serial
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

}
