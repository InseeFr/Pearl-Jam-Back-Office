package fr.insee.pearljam.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
/**
* Entity SurveyUnit : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@TypeDef(
	    name = "list-array",
	    typeClass = ArrayList.class
	)
@Entity
@Table
public class SurveyUnit {
	/**
	* The id of SurveyUnit 
	*/
	@Id
	private String id;
	
	/**
	 * The firstname of SurveyUnit
	 */
	@Column
	private String firstName;
	
	/**
	 * The lastname of SurveyUnit
	 */
	@Column
	private String lasttName;
	
	/**
	 * The list of phonenumbers of SurveyUnit
	 */
	@Type(type = "list-array")
	@Column(columnDefinition = "character varying[]")
	private List<String> phoneNumbers;
	/**
	 * The priority of SurveyUnit
	 */
	@Column
	private boolean priority;
	/**
	 * The address of SurveyUnit
	 */
	@OneToOne
	private Address address;
	/**
	 * The sampleIdentifier of SurveyUnit
	 */
	@OneToOne
	private SampleIdentifier sampleIdentifier;
	/**
	 * The Campaign of SurveyUnit
	 */
	@ManyToOne
	private Campaign campaign;
	/**
	 * The Interviewer of SurveyUnit
	 */
	@ManyToOne
	private Interviewer interviewer;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lasttName
	 */
	public String getLasttName() {
		return lasttName;
	}
	/**
	 * @param lasttName the lasttName to set
	 */
	public void setLasttName(String lasttName) {
		this.lasttName = lasttName;
	}
	/**
	 * @return the phoneNumbers
	 */
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}
	/**
	 * @param phoneNumbers the phoneNumbers to set
	 */
	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	/**
	 * @return the priority
	 */
	public boolean isPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(boolean priority) {
		this.priority = priority;
	}
	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}
	/**
	 * @return the sampleIdentifier
	 */
	public SampleIdentifier getSampleIdentifier() {
		return sampleIdentifier;
	}
	/**
	 * @param sampleIdentifier the sampleIdentifier to set
	 */
	public void setSampleIdentifier(SampleIdentifier sampleIdentifier) {
		this.sampleIdentifier = sampleIdentifier;
	}
	/**
	 * @return the campaign
	 */
	public Campaign getCampaign() {
		return campaign;
	}
	/**
	 * @param campaign the campaign to set
	 */
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	/**
	 * @return the interviewer
	 */
	public Interviewer getInterviewer() {
		return interviewer;
	}
	/**
	 * @param interviewer the interviewer to set
	 */
	public void setInterviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
	}
}
