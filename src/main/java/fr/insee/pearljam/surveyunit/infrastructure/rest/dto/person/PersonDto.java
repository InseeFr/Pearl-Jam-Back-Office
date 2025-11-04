package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.person;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.Person;
import fr.insee.pearljam.surveyunit.domain.model.Title;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDto {
	private Long id;
	private Title title;
	private String firstName;
	private String lastName;
	private String email;
	private Long birthdate;

	private Boolean favoriteEmail;
	private Boolean privileged;
	private List<PhoneNumberDto> phoneNumbers;

	/**
	 * Default constructor
	 */
	public PersonDto() {
		super();
	}

	public PersonDto(Person person) {
		this.id = person.getId();
		this.title = person.getTitle();
		this.firstName = person.getFirstName();
		this.lastName = person.getLastName();
		this.email = person.getEmail();
		this.favoriteEmail = person.isFavoriteEmail();
		this.birthdate = person.getBirthdate();
		this.privileged = person.isPrivileged();
		this.phoneNumbers = person.getPhoneNumbers().stream().map(PhoneNumberDto::new).toList();
	}

	/**
	 * Constructor with all args
	 */
	public PersonDto(Title title, String firstName, String lastName, String email, boolean favoriteEmail,
					 boolean privileged, Long birthdate) {
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.favoriteEmail = favoriteEmail;
		this.privileged = privileged;
		this.birthdate = birthdate;
	}

}

