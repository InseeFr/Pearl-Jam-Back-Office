package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;

/**
 * Entity Interviewer : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table
public class Interviewer implements Serializable {

	/**
	 * 
	 */
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
	private TitleEnum title = TitleEnum.MISTER;

	public Interviewer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Interviewer(String id, String firstName, String lastName, String email, String phoneNumber,
			Set<SurveyUnit> surveyUnits, TitleEnum title) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.surveyUnits = surveyUnits;
		this.title = Optional.ofNullable(title).orElse(TitleEnum.MISTER);
	}

	public Interviewer(InterviewerContextDto interviewerDto) {
		super();
		this.id = interviewerDto.getId();
		this.firstName = interviewerDto.getFirstName();
		this.lastName = interviewerDto.getLastName();
		this.email = interviewerDto.getEmail();
		this.phoneNumber = interviewerDto.getPhoneNumber();
		this.title = Optional.ofNullable(interviewerDto.getTitle()).orElse(TitleEnum.MISTER);
	}

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

	public Set<SurveyUnit> getSurveyUnits() {
		return surveyUnits;
	}

	public TitleEnum getTitle() {
		return title;
	}

	public void setTitle(TitleEnum title) {
		this.title = title;
	}

}
