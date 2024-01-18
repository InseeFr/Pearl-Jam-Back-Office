package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity User : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table(name = "user", schema = "public")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3490006766811003946L;

	/**
	 * The id of User
	 */
	@Id
	@Column(length = 50)
	private String id;

	/**
	 * The first name of the User
	 */
	@Column(length = 255)
	private String firstName;

	/**
	 * The last name of the User
	 */
	@Column(length = 255)
	private String lastName;

	/**
	 * The Organization Unit of the User
	 */
	@ManyToOne
	private OrganizationUnit organizationUnit;

	/**
	 * The List of campaign for the User
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "preference", joinColumns = { @JoinColumn(name = "id_user") }, inverseJoinColumns = {
			@JoinColumn(name = "id_campaign") })
	private List<Campaign> campaigns;

	public User() {
		super();
	}

	public User(String id, String firstName, String lastName, OrganizationUnit organizationUnit) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.organizationUnit = organizationUnit;
	}

	/**
	 * @return id of the User
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id of the User
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the first name of the User
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param fisrt name of the User
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the last name of the User
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param last name of the User
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the Organization Unit associated with the User
	 */
	public OrganizationUnit getOrganizationUnit() {
		return organizationUnit;
	}

	/**
	 * @param Organization Unit of the User
	 */
	public void setOrganizationUnit(OrganizationUnit organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	/**
	 * @return the list of campaign for the user
	 */
	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	/**
	 * @param campaigns associated with he User
	 */
	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
}
