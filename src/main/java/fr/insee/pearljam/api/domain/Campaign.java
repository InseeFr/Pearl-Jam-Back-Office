package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entity Campaign : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table
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

	@Column(length = 255)
	private String email;

	/**
	 * The reference to visibility table
	 */
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Visibility> visibilities;

	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Referent> referents;

	public Campaign() {
		super();
	}

	public Campaign(String id, String label, IdentificationConfiguration identConfig,
			ContactOutcomeConfiguration contOutConfig, ContactAttemptConfiguration contAttConfig,String email) {
		super();
		this.id = id;
		this.label = label;
		this.contactAttemptConfiguration = contAttConfig;
		this.contactOutcomeConfiguration = contOutConfig;
		this.identificationConfiguration = identConfig;
		this.email = email;
	}

	/**
	 * @return the id of the Campaign
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id of the Campaign
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the label of the Campaign
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label of the Campaign
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the visibilities
	 */
	public List<Visibility> getVisibilities() {
		return visibilities;
	}

	/**
	 * @param visibilities the visibilities to set
	 */
	public void setVisibilities(List<Visibility> visibilities) {
		this.visibilities = visibilities;
	}

	public List<Referent> getReferents() {
		return this.referents;
	}

	public void setReferents(List<Referent> referents) {
		this.referents = referents;
	}

	public IdentificationConfiguration getIdentificationConfiguration() {
		return this.identificationConfiguration;
	}

	public void setIdentificationConfiguration(IdentificationConfiguration identificationConfiguration) {
		this.identificationConfiguration = identificationConfiguration;
	}

	public ContactOutcomeConfiguration getContactOutcomeConfiguration() {
		return this.contactOutcomeConfiguration;
	}

	public void setContactOutcomeConfiguration(ContactOutcomeConfiguration contactOutcomeConfiguration) {
		this.contactOutcomeConfiguration = contactOutcomeConfiguration;
	}

	public ContactAttemptConfiguration getContactAttemptConfiguration() {
		return this.contactAttemptConfiguration;
	}

	public void setContactAttemptConfiguration(ContactAttemptConfiguration contactAttemptConfiguration) {
		this.contactAttemptConfiguration = contactAttemptConfiguration;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
