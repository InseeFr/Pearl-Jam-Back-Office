package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class SurveyUnit implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8615711600423725468L;

	/**
	 * The id of SurveyUnit
	 */
	@Id
	@Column(length = 50)
	private String id;

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

	@ManyToOne(fetch = FetchType.LAZY)
	private OrganizationUnit organizationUnit;

	@OneToMany(fetch = FetchType.LAZY, targetEntity = Person.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<Person> persons = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, targetEntity = ContactAttempt.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<ContactAttempt> contactAttempts = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, targetEntity = Comment.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<Comment> comments = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, targetEntity = State.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<State> states = new HashSet<>();
	
	@OneToMany(fetch = FetchType.LAZY, targetEntity = ClosingCause.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<ClosingCause> closingCause;



	public SurveyUnit() {
	}

	public SurveyUnit(String id, boolean priority, Address address, SampleIdentifier sampleIdentifier,
			Campaign campaign, Interviewer interviewer, OrganizationUnit organizationUnit, Set<Person> persons) {
		super();
		this.id = id;
		this.priority = priority;
		this.address = address;
		this.sampleIdentifier = sampleIdentifier;
		this.campaign = campaign;
		this.interviewer = interviewer;
		this.organizationUnit = organizationUnit;
		this.persons = persons;
	}

	public SurveyUnit(String id, boolean priority, Address address, SampleIdentifier sampleIdentifier,
			Campaign campaign, Interviewer interviewer) {
		super();
		this.id = id;
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
	
	public ClosingCause getClosingCause() {
		return !closingCause.isEmpty() ? closingCause.iterator().next() : null;
	}

	public void setClosingCause(ClosingCause closingCause) {
		this.closingCause.clear();
		this.closingCause.add(closingCause);
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

	public OrganizationUnit getOrganizationUnit() {
		return organizationUnit;
	}

	public void setOrganizationUnit(OrganizationUnit organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	public Set<Person> getPersons() {
		return persons;
	}

	public Set<ContactAttempt> getContactAttempts() {
		return contactAttempts;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public Set<State> getStates() {
		return states;
	}

	public Boolean isAtLeastState(String state) {
		for (State s : this.states) {
			if (s.getType().toString().equals(state)) {
				return true;
			}
		}
		return false;
	}

	public Boolean isLastState(String state) {
		State lastState = new State();
		for (State s : this.states) {
			if (lastState.getDate() == null || lastState.getDate() < s.getDate()) {
				lastState = s;
			}
		}
		return state.equals(lastState.getType().toString());
	}

}
