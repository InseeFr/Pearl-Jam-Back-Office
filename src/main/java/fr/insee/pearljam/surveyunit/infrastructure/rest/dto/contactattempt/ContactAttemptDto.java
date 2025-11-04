package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.contactattempt;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.ContactAttempt;
import fr.insee.pearljam.surveyunit.domain.model.Medium;
import fr.insee.pearljam.surveyunit.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContactAttemptDto {

	private Long date;
	private Status status;
	private Medium medium;

	public ContactAttemptDto(ContactAttempt contactAttempt) {
		super();
		this.date = contactAttempt.getDate();
		this.status = contactAttempt.getStatus();
		this.medium = contactAttempt.getMedium();
	}

	@Override
	public String toString() {
		return "ContactAttemptDto [date=" + date + ", status=" + status + ", medium=" + medium + "]";
	}
	
}
