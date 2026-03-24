package fr.insee.pearljam.contracts.surveyunit.dto.interviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;
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

	public static InterviewerDto fromModel(InterviewerCount interviewerCount){
		return  new InterviewerDto(
				interviewerCount.id(),
				interviewerCount.firstName(),
				interviewerCount.lastName(),
				interviewerCount.surveyUnitCount()
		);
	}
	
}
