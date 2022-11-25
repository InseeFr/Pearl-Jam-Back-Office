package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;

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
	 * The priority of SurveyUnit
	 */
	@Column
	private Boolean viewed;

	/**
	 * The priority of SurveyUnit
	 */
	@Column
	private Boolean move = false;

	/**
	 * The address of SurveyUnit
	 */
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Address address;

	/**
	 * The sampleIdentifier of SurveyUnit
	 */
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private SampleIdentifier sampleIdentifier;

	/**
	 * The contactOutcome of SurveyUnit
	 */
	@OneToOne(fetch = FetchType.LAZY, targetEntity = ContactOutcome.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private ContactOutcome contactOucome;

	@OneToOne(fetch = FetchType.LAZY, targetEntity = ClosingCause.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private ClosingCause closingCause;

	@OneToOne(fetch = FetchType.LAZY, targetEntity = Identification.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Identification identification;

	/**
	 * The Campaign of SurveyUnit
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private Campaign campaign;

	/*
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

	public SurveyUnit() {
		super();
	}

	public SurveyUnit(String id, boolean priority, boolean viewed, Address address, SampleIdentifier sampleIdentifier,
			Campaign campaign, Interviewer interviewer, OrganizationUnit organizationUnit, Set<Person> persons) {
		super();
		this.id = id;
		this.priority = priority;
		this.viewed = viewed;
		this.address = address;
		this.sampleIdentifier = sampleIdentifier;
		this.campaign = campaign;
		this.interviewer = interviewer;
		this.organizationUnit = organizationUnit;
		this.persons = persons;
	}

	public SurveyUnit(String id, boolean priority, boolean viewed, Address address, SampleIdentifier sampleIdentifier,
			Campaign campaign, Interviewer interviewer) {
		super();
		this.id = id;
		this.priority = priority;
		this.viewed = viewed;
		this.address = address;
		this.sampleIdentifier = sampleIdentifier;
		this.campaign = campaign;
		this.interviewer = interviewer;
	}

	public SurveyUnit(SurveyUnitContextDto su, OrganizationUnit organizationUnit, Campaign campaign) {
		this.id = su.getId();
		this.priority = su.getPriority();
		this.viewed = false;
		this.address = new InseeAddress(su.getAddress());
		this.sampleIdentifier = new InseeSampleIdentifier(su.getSampleIdentifiers());
		this.campaign = campaign;
		this.interviewer = null;
		this.identification = new Identification(su.getIdentification(), this);
		this.organizationUnit = organizationUnit;
		this.persons = su.getPersons().stream().map(p -> new Person(p, this)).collect(Collectors.toSet());

		this.comments = new HashSet<Comment>(
				Optional.ofNullable(su.getComments()).orElse(new HashSet<>()).stream()
						.map(comment -> new Comment(comment, this)).collect(Collectors.toList()));

		if (su.getContactOutcome() != null) {
			this.contactOucome = new ContactOutcome(su.getContactOutcome(), this);
		}
		if (su.getContactAttempts() != null && !su.getContactAttempts().isEmpty()) {
			this.contactAttempts = su.getContactAttempts().stream().map(c -> new ContactAttempt(c, this))
					.collect(Collectors.toSet());
		} else {
			this.contactAttempts = new HashSet<>();
		}
		if (su.getStates() != null && !su.getStates().isEmpty()) {
			this.states = su.getStates().stream().map(s -> new State(s, this)).collect(Collectors.toSet());
		} else {
			this.states = Set.of(new State(new Date().getTime(), this, StateType.VIN));
		}
		if (su.getClosingCause() != null) {
			this.closingCause = new ClosingCause(su.getClosingCause(), this);
		} else {
			this.closingCause = null;
		}
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
	 * @return the viewed
	 */
	public Boolean getViewed() {
		return viewed;
	}

	/**
	 * @param viewed the viewed to set
	 */
	public void setViewed(Boolean viewed) {
		this.viewed = viewed;
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
	 * @return the contactOucome
	 */
	public ContactOutcome getContactOucome() {
		return contactOucome;
	}

	/**
	 * @param contactOucome the contactOucome to set
	 */
	public void setContactOucome(ContactOutcome contactOucome) {
		this.contactOucome = contactOucome;
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

	/**
	 * @return the organizationUnit
	 */
	public OrganizationUnit getOrganizationUnit() {
		return organizationUnit;
	}

	/**
	 * @param organizationUnit the organizationUnit to set
	 */
	public void setOrganizationUnit(OrganizationUnit organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	/**
	 * @return the persons
	 */
	public Set<Person> getPersons() {
		return persons;
	}

	/**
	 * @param persons the persons to set
	 */
	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}

	/**
	 * @return the contactAttempts
	 */
	public Set<ContactAttempt> getContactAttempts() {
		return contactAttempts;
	}

	/**
	 * @param contactAttempts the contactAttempts to set
	 */
	public void setContactAttempts(Set<ContactAttempt> contactAttempts) {
		this.contactAttempts = contactAttempts;
	}

	/**
	 * @return the comments
	 */
	public Set<Comment> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	/**
	 * @return the states
	 */
	public Set<State> getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(Set<State> states) {
		this.states = states;
	}

	/**
	 * @return the closingCause
	 */
	public ClosingCause getClosingCause() {
		return closingCause;
	}

	/**
	 * @param closingCause the closingCause to set
	 */
	public void setClosingCause(ClosingCause closingCause) {
		this.closingCause = closingCause;
	}

	public Boolean isAtLeastState(String state) {
		for (State s : this.states) {
			if (s.getType().toString().equals(state)) {
				return true;
			}
		}
		return false;
	}

	public boolean getPriority() {
		return this.priority;
	}

	public Boolean isViewed() {
		return this.viewed;
	}

	public Identification getIdentification() {
		return this.identification;
	}

	public void setIdentification(Identification identification) {
		this.identification = identification;
	}

	public Boolean isMove() {
		return this.move;
	}

	public Boolean getMove() {
		return this.move;
	}

	public void setMove(Boolean move) {
		this.move = move;
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
