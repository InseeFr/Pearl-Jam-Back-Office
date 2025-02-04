package fr.insee.pearljam.api.domain;

import java.io.Serial;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.insee.pearljam.api.dto.person.PersonDto;

/**
 * Entity Person : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Person implements Serializable {

	/**
	 * 
	 */
	@Serial
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
		this.favoriteEmail = p.getFavoriteEmail();
		this.privileged = p.getPrivileged();
		this.birthdate = p.getBirthdate();
		this.phoneNumbers = p.getPhoneNumbers().stream().map(pn -> new PhoneNumber(pn, this))
				.collect(Collectors.toSet());
		this.surveyUnit = su;
	}

}
