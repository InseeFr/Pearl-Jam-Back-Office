package fr.insee.pearljam.api.dto.person;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.Person;
import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.api.dto.phonenumber.PhoneNumberDto;

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
		this.phoneNumbers = person.getPhoneNumbers().stream()
				.map(phoneNum -> new PhoneNumberDto(phoneNum))
				.collect(Collectors.toList());
	}

	/**
	 * Constructor with all args
	 */
	public PersonDto(Title title, String firstName, String lastName, String email, boolean favoriteEmail, boolean privileged, Long birthdate) {
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.favoriteEmail = favoriteEmail;
		this.privileged = privileged;
		this.birthdate = birthdate;
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

	/**
	 * @param title the title to set
	 */
	public void setTitle(Title title) {
		this.title = title;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the birthdate
	 */
	public Long getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(Long birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return the favoriteEmail
	 */
	public Boolean isFavoriteEmail() {
		return favoriteEmail;
	}

	/**
	 * @param favoriteEmail the favoriteEmail to set
	 */
	public void setFavoriteEmail(Boolean favoriteEmail) {
		this.favoriteEmail = favoriteEmail;
	}

	/**
	 * @return the privileged
	 */
	public Boolean isPrivileged() {
		return privileged;
	}

	/**
	 * @param privileged the privileged to set
	 */
	public void setPrivileged(Boolean privileged) {
		this.privileged = privileged;
	}

	/**
	 * @return the phoneNumbers
	 */
	public List<PhoneNumberDto> getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers the phoneNumbers to set
	 */
	public void setPhoneNumbers(List<PhoneNumberDto> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

}

