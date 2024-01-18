package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.insee.pearljam.api.domain.communication.CommunicationRequest;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;

/**
 * Entity SurveyUnit : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
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
	 * Has been viewed by the LocalUser
	 */
	@Column
	private Boolean viewed;

	/**
	 * If interviewer had to make a move
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

	@OneToMany(fetch = FetchType.LAZY, targetEntity = CommunicationRequest.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<CommunicationRequest> communicationRequests = new HashSet<>();

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

		this.comments = new HashSet<>(
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
