package fr.insee.pearljam.contracts.surveyunit.dto.interviewer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.EmailValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.insee.pearljam.domain.surveyunit.model.Title;

@NoArgsConstructor
@Getter
@Setter
public class InterviewerContextDto {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private Title title = Title.MISTER;

	public InterviewerContextDto(String id, String firstName, String lastName, String email, String phoneNumber,
			Title title) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
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
