package fr.insee.pearljam.api.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;

/**
 * Entity Interviewer : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Interviewer implements Serializable {

	@Serial
	private static final long serialVersionUID = -5488798660579904552L;

	/**
	 * The id of Interviewer
	 */
	@Id
	@Column(length = 50)
	private String id;

	/**
	 * The first name of the Interviewer
	 */
	@Column(length = 255)
	private String firstName;

	/**
	 * The last name of the Interviewer
	 */
	@Column(length = 255)
	private String lastName;

	/**
	 * The email of the Interviewer
	 */
	@Column(length = 255)
	private String email;

	/**
	 * The phone number of the Interviewer
	 */
	@Column(length = 255)
	private String phoneNumber;

	@OneToMany(fetch = FetchType.LAZY, targetEntity = SurveyUnit.class, cascade = CascadeType.ALL, mappedBy = "interviewer", orphanRemoval = true)
	private Set<SurveyUnit> surveyUnits = new HashSet<>();

	@Column(length = 25)
	@Enumerated(EnumType.STRING)
	private Title title = Title.MISTER;

	public Interviewer(InterviewerContextDto interviewerDto) {
		super();
		this.id = interviewerDto.getId();
		this.firstName = interviewerDto.getFirstName();
		this.lastName = interviewerDto.getLastName();
		this.email = interviewerDto.getEmail();
		this.phoneNumber = interviewerDto.getPhoneNumber();
		this.title = Optional.ofNullable(interviewerDto.getTitle()).orElse(Title.MISTER);
	}

}
