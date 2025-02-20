package fr.insee.pearljam.api.dto.interviewer;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.Interviewer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterviewerDto {
	private String id;
	private String interviewerFirstName;
	private String interviewerLastName;
	private Long surveyUnitCount;

	public InterviewerDto(String id, String interviewerFirstName, String interviewerLastName) {
		super();
		this.id = id;
		this.interviewerFirstName = interviewerFirstName;
		this.interviewerLastName = interviewerLastName;
	}
	
	public InterviewerDto(Interviewer interviewer) {
		super();
		this.id = interviewer.getId();
		this.interviewerFirstName = interviewer.getFirstName();
		this.interviewerLastName = interviewer.getLastName();
	}

	
}
