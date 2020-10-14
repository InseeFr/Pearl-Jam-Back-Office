package fr.insee.pearljam.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
* Entity Interviewer : represent the entity table in DB
* 
* @author Corcaud Samuel
* 
*/
@Entity
@Table
public class Interviewer {
	
	/**
	* The id of Interviewer 
	*/
	@Id
	@Column(length=50)
	private String id;

	/**
	* The first name of the Interviewer 
	*/
	@Column(length=255)
	private String firstName;
	
	/**
	* The last name of the Interviewer 
	*/
	@Column(length=255)
	private String lastName;
	
	/**
	* The email of the Interviewer 
	*/
	@Column(length=255)
	private String email;
	
	/**
	 * The phone number of the Interviewer
	 */
	@Column(length=255)
	private String phoneNumber;
	
	/**
	 * The Organization Unit of the Interviewer
	 */
	@ManyToOne
	private OrganizationUnit organizationUnit;

	/**
	 * @return the id of the Interviewer
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id of the Interviewer
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the first name of the Interviewer
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param id of the Interviewer
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the last name of the Interviewer
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param the last name of the Interviewer
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email of the Interviewer
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param the email of the Interviewer
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone number of the Interviewer
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param the phone number of the Interviewer
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * @return the Organization Unit associated with the Interviewer
	 */
	public OrganizationUnit getOrganizationUnit() {
		return organizationUnit;
	}

	/**
	 * @param Organization Unit of the Interviewer
	 */
	public void setOrganizationUnit(OrganizationUnit organizationUnit) {
		this.organizationUnit = organizationUnit;
	}
	
}
