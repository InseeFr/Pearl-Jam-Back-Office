package fr.insee.pearljam.contracts.surveyunit.dto.surveyunit;

import fr.insee.pearljam.domain.surveyunit.model.Medium;
import fr.insee.pearljam.domain.surveyunit.model.Status;
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

	@Override
	public String toString() {
		return "ContactAttemptDto [date=" + date + ", status=" + status + ", medium=" + medium + "]";
	}
	
}
