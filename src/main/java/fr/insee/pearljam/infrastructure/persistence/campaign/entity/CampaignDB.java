package fr.insee.pearljam.infrastructure.persistence.campaign.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity Campaign : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table(name = "campaign", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class CampaignDB implements Serializable {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * The id of Campaign
	 */
	@Id
	@Column(length = 50)
	private String id;

	/**
	 * The label of Campaign
	 */
	@Column(length = 255)
	private String label;

	@Column
	@Enumerated(EnumType.STRING)
	private IdentificationConfiguration identificationConfiguration;

	@Column
	@Enumerated(EnumType.STRING)
	private ContactOutcomeConfiguration contactOutcomeConfiguration;

	@Column
	@Enumerated(EnumType.STRING)
	private ContactAttemptConfiguration contactAttemptConfiguration;

	@Column(length = 255)
	private String email;

	/**
	 * The reference to visibility table
	 */
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VisibilityDB> visibilities;

	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReferentDB> referents;

	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunicationTemplateDB> communicationTemplates;

	@Column
	private Boolean sensitivity;

	@Column
	private boolean collectNextContacts;

	public CampaignDB(String id, String label, IdentificationConfiguration identConfig,
					  ContactOutcomeConfiguration contOutConfig, ContactAttemptConfiguration contAttConfig, String email, boolean sensitivity, boolean collectNextContacts) {
		super();
		this.id = id;
		this.label = label;
		this.contactAttemptConfiguration = contAttConfig;
		this.contactOutcomeConfiguration = contOutConfig;
		this.identificationConfiguration = identConfig;
		this.email = email;
		this.sensitivity = sensitivity;
		this.collectNextContacts = collectNextContacts;
	}
}
