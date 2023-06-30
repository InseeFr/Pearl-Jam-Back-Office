package fr.insee.pearljam.api.dto.interviewer;

import org.apache.commons.validator.EmailValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.insee.pearljam.api.domain.Interviewer;

public class InterviewerContextDto {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumer;

	public InterviewerContextDto(String id, String firstName, String lastName, String email, String phoneNumer) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumer = phoneNumer;
	}

	public InterviewerContextDto(Interviewer interviewer) {
		super();
		this.id = interviewer.getId();
		this.firstName = interviewer.getFirstName();
		this.lastName = interviewer.getLastName();
		this.email = interviewer.getEmail();
		this.phoneNumer = interviewer.getPhoneNumber();
	}

	public InterviewerContextDto() {
		super();
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
	 * @return the phoneNumer
	 */
	public String getPhoneNumer() {
		return phoneNumer;
	}

	/**
	 * @param phoneNumer the phoneNumer to set
	 */
	public void setPhoneNumer(String phoneNumer) {
		this.phoneNumer = phoneNumer;
	}

	@JsonIgnore
	public boolean isValid() {
		return this.id != null && !this.id.isBlank()
				&& this.firstName != null && !this.firstName.isBlank()
				&& this.lastName != null && !this.lastName.isBlank()
				&& this.email != null && !this.email.isBlank()
				&& this.phoneNumer != null && !this.phoneNumer.isBlank()
				&& EmailValidator.getInstance().isValid(this.email);
	}
}
