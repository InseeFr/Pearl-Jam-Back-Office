package fr.insee.pearljam.api.dto.interviewer;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignInterviewerDto {
		private String id;
		private String interviewerFirstName;
		private String interviewerLastName;
		private String email;
		private String phoneNumber;
		private Long surveyUnitCount;
}
