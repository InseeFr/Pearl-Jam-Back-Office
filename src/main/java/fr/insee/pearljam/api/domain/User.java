package fr.insee.pearljam.api.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
* Entity User : represent the entity table in DB
* 
* @author Corcaud Samuel
* 
*/
@Entity
@Table
public class User {

	/**
	* The id of User 
	*/
	@Id
	public String id;
	
	/**
	* The first name of the User 
	*/
	@Column(length=255)
	public String firstName;
	
	/**
	* The last name of the User 
	*/
	@Column(length=255)
	public String lastName;
	
	/**
	 * The Organization Unit of the User
	 */
	@ManyToOne
	public OrganizationUnit organizationUnit;
	
	/**
	 * The List of campaign for the User
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "preference", joinColumns = { @JoinColumn(name = "id_user") }, inverseJoinColumns = { @JoinColumn(name = "id_campaign") })
	public List<Campaign> campaigns;
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
