package fr.insee.pearljam.api.dto.contactattempt;

import fr.insee.pearljam.api.domain.ContactAttempt;
import fr.insee.pearljam.api.domain.Medium;
import fr.insee.pearljam.api.domain.Status;
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
