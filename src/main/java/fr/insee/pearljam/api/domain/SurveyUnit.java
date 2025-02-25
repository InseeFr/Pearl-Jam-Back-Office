package fr.insee.pearljam.api.domain;

import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;
import fr.insee.pearljam.api.surveyunit.dto.identification.IdentificationDto;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.ContactOutcome;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommentDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.ContactOutcomeDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.identification.IdentificationDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entity SurveyUnit : represent the entity table in DB
 *
 * @author Claudel Benjamin
 */

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class SurveyUnit implements Serializable {

	@Serial
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
	 * display name (business id)
	 */
	@Column
	private String displayName;

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
	@OneToOne(fetch = FetchType.LAZY, targetEntity = ContactOutcomeDB.class, cascade = CascadeType.ALL, mappedBy =
			"surveyUnit", orphanRemoval = true)
	private ContactOutcomeDB contactOutcome;

	@OneToOne(fetch = FetchType.LAZY, targetEntity = ClosingCause.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private ClosingCause closingCause;

	@OneToOne(fetch = FetchType.LAZY, targetEntity = IdentificationDB.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private IdentificationDB identification;

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

	@OneToMany(fetch = FetchType.LAZY, targetEntity = CommentDB.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<CommentDB> comments = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, targetEntity = State.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<State> states = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, targetEntity = CommunicationRequestDB.class, cascade = CascadeType.ALL, mappedBy = "surveyUnit", orphanRemoval = true)
	private Set<CommunicationRequestDB> communicationRequests = new HashSet<>();

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

	public SurveyUnit(SurveyUnitContextDto su, OrganizationUnit organizationUnit, Campaign campaign) {
		this.id = su.getId();
		this.displayName = su.getDisplayName();
		//TODO: delete this test when displayName becomes mandatory in creation endpoint
		if (this.displayName == null) {
			this.displayName = this.id;
		}
		this.priority = su.getPriority();
		this.viewed = false;
		this.address = new InseeAddress(su.getAddress());
		this.sampleIdentifier = new InseeSampleIdentifier(su.getSampleIdentifiers());
		this.campaign = campaign;
		this.interviewer = null;

		IdentificationConfiguration identificationType = safeGetIdentificationConfiguration(campaign);
		this.identification = IdentificationDB.fromModel(this, IdentificationDto.toModel(su.getIdentification(),
				identificationType), identificationType);
		this.organizationUnit = organizationUnit;
		this.persons = su.getPersons().stream().map(p -> new Person(p, this)).collect(Collectors.toSet());

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

	public void updateIdentification(Identification identification) {
		if (identification == null) {
			return;
		}

		IdentificationDB identificationDB = getIdentification();
		if (identificationDB == null) {
			IdentificationConfiguration identificationType = safeGetIdentificationConfiguration(campaign);
			identificationDB = IdentificationDB.fromModel(this, identification, identificationType);
			setIdentification(identificationDB);
			return;
		}

		identificationDB.update(identification);
	}

	private IdentificationConfiguration safeGetIdentificationConfiguration(Campaign campaign) {
		if (campaign == null) {
			return null;
		}
		return campaign.getIdentificationConfiguration();
	}

	/**
	 * update a list of comments for a survey unit
	 *
	 * @param commentsToUpdate the comment to update
	 */
	public void updateComments(Set<Comment> commentsToUpdate) {
		Set<CommentDB> existingComments = this.getComments();

		Set<CommentDB> commentsDBToUpdate = commentsToUpdate.stream()
				.map(newComment -> CommentDB.fromModel(this, newComment))
				.collect(Collectors.toSet());

		Set<CommentDB> commentsToDelete = existingComments.stream()
				.filter(existingComment -> commentsDBToUpdate.stream()
						.anyMatch(commentToUpdate -> commentToUpdate.getType() == existingComment.getType())
				)
				.collect(Collectors.toSet());

		existingComments.removeAll(commentsToDelete);
		existingComments.addAll(commentsDBToUpdate);
	}

	public Set<Comment> getModelComments() {
		return CommentDB.toModel(this.getComments());
	}

	public Identification getModelIdentification() {
		return IdentificationDB.toModel(this.getIdentification());
	}

	public Set<CommunicationRequest> getModelCommunicationRequests() {
		return CommunicationRequestDB.toModel(this.getCommunicationRequests());
	}

	public ContactOutcome getModelContactOutcome() {
		return ContactOutcomeDB.toModel(this.getContactOutcome());
	}

	public void updateContactOutcome(ContactOutcome contactOutcome) {
		if (contactOutcome == null) {
			return;
		}
		ContactOutcomeDB currentContactOutcome = getContactOutcome();
		if (currentContactOutcome == null) {

			ContactOutcomeDB newContactOutcome = ContactOutcomeDB.fromModel(this, contactOutcome);
			setContactOutcome(newContactOutcome);
			return;
		}
		currentContactOutcome.updateContactOutcome(contactOutcome);

	}
}
