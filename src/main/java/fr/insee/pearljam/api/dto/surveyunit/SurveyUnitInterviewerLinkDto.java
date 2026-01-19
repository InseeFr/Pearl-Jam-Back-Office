package fr.insee.pearljam.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitInterviewerLinkDto {

	private String surveyUnitId;
	private String interviewerId;

	@JsonIgnore
	public String getLink() {
		if(this.surveyUnitId != null && this.interviewerId != null) {
			return this.surveyUnitId+"/"+this.interviewerId;
		}
		else if(this.surveyUnitId != null){
			return this.surveyUnitId + "/null";
		}
		else if(this.interviewerId != null) {
			return "null/" + this.interviewerId;
		}
		else {
			
			return "null/null";
		}
		
	}
	
	@JsonIgnore
	public boolean isValid() {
		return this.surveyUnitId!=null && !this.surveyUnitId.isBlank() && this.interviewerId!=null && !this.interviewerId.isBlank();
	}
	
}
