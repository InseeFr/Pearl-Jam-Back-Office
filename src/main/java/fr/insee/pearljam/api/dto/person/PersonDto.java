package fr.insee.pearljam.api.dto.person;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.Person;
import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.api.dto.phonenumber.PhoneNumberDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDto {
	public Long id;
	public Title title;
	public String firstName;
	public String lastName;
	public String email;
	public Long birthdate;

	public Boolean favoriteEmail;
	public List<PhoneNumberDto> phoneNumbers;

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
		this.phoneNumbers = person.getPhoneNumbers().stream()
				.map(phoneNum -> new PhoneNumberDto(phoneNum))
				.collect(Collectors.toList());
	}

	/**
	 * Constructor with all args
	 */
	public PersonDto(Title title, String firstName, String lastName, String email, boolean favoriteEmail) {
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.favoriteEmail = favoriteEmail;
	}


	/**
	 * @return the title
	 */
	public Title getTitle() {
		return title;
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
	public Boolean isFavoriteEmail() {
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
	
	public Long getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Long birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	/**
	 * @param favoriteEmail the favoriteEmail to set
	 */
	public void setFavoriteEmail(Boolean favoriteEmail) {
		this.favoriteEmail = favoriteEmail;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<PhoneNumberDto> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumberDto> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

}

