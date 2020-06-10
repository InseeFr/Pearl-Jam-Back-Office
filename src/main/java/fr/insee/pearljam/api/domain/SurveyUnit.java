package fr.insee.pearljam.api.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
/**
* Entity SurveyUnit : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/

@Entity
@Table
public class SurveyUnit {
	/**
	* The id of SurveyUnit 
	*/
	@Id
	@Column(length=50)
	private String id;
	
	/**
	 * The firstname of SurveyUnit
	 */
	@Column(length=255)
	private String firstName;
	
	/**
	 * The lastname of SurveyUnit
	 */
	@Column(length=255)
	private String lastName;
	
	@ElementCollection(fetch = FetchType.EAGER)
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
	@ManyToOne(fetch = FetchType.LAZY)
	private Campaign campaign;
	/**
	 * The Interviewer of SurveyUnit
	 */
	@ManyToOne
	private Interviewer interviewer;
	
	public SurveyUnit() {
	}
	
	public SurveyUnit(String firstName, String lastName, List<String> phoneNumbers) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumbers = phoneNumbers;
	}
	
	public SurveyUnit(String id, String firstName, String lastName, List<String> phoneNumbers, boolean priority,
			Address address, SampleIdentifier sampleIdentifier, Campaign campaign, Interviewer interviewer) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumbers = phoneNumbers;
		this.priority = priority;
		this.address = address;
		this.sampleIdentifier = sampleIdentifier;
		this.campaign = campaign;
		this.interviewer = interviewer;
	}
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
		return lastName;
	}
	/**
	 * @param lasttName the lasttName to set
	 */
	public void setLasttName(String lasttName) {
		this.lastName = lasttName;
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
