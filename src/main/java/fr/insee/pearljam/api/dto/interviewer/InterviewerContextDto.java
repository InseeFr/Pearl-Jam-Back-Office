package fr.insee.pearljam.api.dto.interviewer;

import org.apache.commons.validator.EmailValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.TitleEnum;

public class InterviewerContextDto {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private TitleEnum title = TitleEnum.MISTER;

	public InterviewerContextDto(String id, String firstName, String lastName, String email, String phoneNumber,
			TitleEnum title) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.title = title;
	}

	public InterviewerContextDto(Interviewer interviewer) {
		super();
		this.id = interviewer.getId();
		this.firstName = interviewer.getFirstName();
		this.lastName = interviewer.getLastName();
		this.email = interviewer.getEmail();
		this.phoneNumber = interviewer.getPhoneNumber();
		this.title = interviewer.getTitle();
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
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public TitleEnum getTitle() {
		return title;
	}

	public void setTitle(TitleEnum title) {
		this.title = title;
	}

	@JsonIgnore
	public boolean isValid() {
		return this.id != null && !this.id.isBlank()
				&& this.firstName != null && !this.firstName.isBlank()
				&& this.lastName != null && !this.lastName.isBlank()
				&& this.email != null && !this.email.isBlank()
				&& this.phoneNumber != null && !this.phoneNumber.isBlank()
				&& EmailValidator.getInstance().isValid(this.email);
	}

}
