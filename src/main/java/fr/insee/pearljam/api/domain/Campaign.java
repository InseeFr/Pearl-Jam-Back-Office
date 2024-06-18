package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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
@Table
@Getter
@Setter
@NoArgsConstructor
public class Campaign implements Serializable {

	/**
	 * 
	 */
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

	@Column
	private Boolean communicationConfiguration;

	@Column(length = 255)
	private String email;

	/**
	 * The reference to visibility table
	 */
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Visibility> visibilities;

	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Referent> referents;

	public Campaign(String id, String label, IdentificationConfiguration identConfig,
			ContactOutcomeConfiguration contOutConfig, ContactAttemptConfiguration contAttConfig, String email,
			Boolean communicationConfiguration) {
		super();
		this.id = id;
		this.label = label;
		this.contactAttemptConfiguration = contAttConfig;
		this.contactOutcomeConfiguration = contOutConfig;
		this.identificationConfiguration = identConfig;
		this.email = email;
		this.communicationConfiguration = Optional.ofNullable(communicationConfiguration).orElse(false);
	}

}
