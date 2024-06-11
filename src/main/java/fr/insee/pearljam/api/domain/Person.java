package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import fr.insee.pearljam.api.dto.person.PersonDto;

/**
 * Entity Person : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table
public class Person implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The title of Person
	 */
	private Title title;

	/**
	 * The first name of the Person
	 */
	private String firstName;

	/**
	 * The last name of the Person
	 */
	private String lastName;

	/**
	 * The email of the Person
	 */
	private String email;

	/**
	 * The birthdate of the Person
	 */
	private Long birthdate;

	/**
	 * Is the favorite email of the person
	 */
	private boolean favoriteEmail;

	/**
	 * Is the person privileged
	 */
	private boolean privileged;

	/**
	 * SurveyUnit associated to the person
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private SurveyUnit surveyUnit;

	@OneToMany(fetch = FetchType.LAZY, targetEntity = PhoneNumber.class, cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
	private Set<PhoneNumber> phoneNumbers = new HashSet<>();

	/**
	 * Default constructor
	 */
	public Person() {
		super();
	}

	/**
	 * Constructor with all args
	 */
	public Person(Title title, String firstName, String lastName, String email, boolean favoriteEmail,
			boolean privileged, Long birthdate, SurveyUnit surveyUnit) {
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.favoriteEmail = favoriteEmail;
		this.privileged = privileged;
		this.birthdate = birthdate;
		this.surveyUnit = surveyUnit;
	}

	public Person(PersonDto p, SurveyUnit su) {
		this.title = p.getTitle();
		this.firstName = p.getFirstName();
		this.lastName = p.getLastName();
		this.email = p.getEmail();
		this.favoriteEmail = p.isFavoriteEmail();
		this.privileged = p.isPrivileged();
		this.birthdate = p.getBirthdate();
		this.phoneNumbers = p.getPhoneNumbers().stream().map(pn -> new PhoneNumber(pn, this))
				.collect(Collectors.toSet());
		this.surveyUnit = su;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public Title getTitle() {
		return title;
	}

	public Long getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Long birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the favoriteEmail
	 */
	public boolean isFavoriteEmail() {
		return favoriteEmail;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(Title title) {
		this.title = title;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the privileged
	 */
	public boolean isPrivileged() {
		return privileged;
	}

	/**
	 * @param privileged the privileged to set
	 */
	public void setPrivileged(boolean privileged) {
		this.privileged = privileged;
	}

	/**
	 * @return the surveyUnit
	 */
	public SurveyUnit getSurveyUnit() {
		return surveyUnit;
	}

	/**
	 * @param surveyUnit the surveyUnit to set
	 */
	public void setSurveyUnit(SurveyUnit surveyUnit) {
		this.surveyUnit = surveyUnit;
	}

	/**
	 * @param favoriteEmail the favoriteEmail to set
	 */
	public void setFavoriteEmail(boolean favoriteEmail) {
		this.favoriteEmail = favoriteEmail;
	}

	public Set<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(Set<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

}
