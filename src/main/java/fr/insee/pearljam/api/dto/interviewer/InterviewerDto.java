package fr.insee.pearljam.api.dto.interviewer;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.domain.count.model.InterviewerCount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterviewerDto {
	private String id;
	private String interviewerFirstName;
	private String interviewerLastName;
	private Long surveyUnitCount;

	public InterviewerDto(Interviewer interviewer) {
		super();
		this.id = interviewer.getId();
		this.interviewerFirstName = interviewer.getFirstName();
		this.interviewerLastName = interviewer.getLastName();
	}

	public static InterviewerDto fromModel(InterviewerCount interviewerCount){
		return  new InterviewerDto(
				interviewerCount.id(),
				interviewerCount.firstName(),
				interviewerCount.lastName(),
				interviewerCount.surveyUnitCount()
		);
	}
	
}
