package fr.insee.pearljam.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitInterviewerLinkDto {
	private String surveyUnitId;
	private String interviewerId;
	public SurveyUnitInterviewerLinkDto(String surveyUnitId, String interviewerId) {
		super();
		this.surveyUnitId = surveyUnitId;
		this.interviewerId = interviewerId;
	}
	public SurveyUnitInterviewerLinkDto() {
		super();
	}
	/**
	 * @return the surveyUnitId
	 */
	public String getSurveyUnitId() {
		return surveyUnitId;
	}
	/**
	 * @param surveyUnitId the surveyUnitId to set
	 */
	public void setSurveyUnitId(String surveyUnitId) {
		this.surveyUnitId = surveyUnitId;
	}
	/**
	 * @return the interviewerId
	 */
	public String getInterviewerId() {
		return interviewerId;
	}
	/**
	 * @param interviewerId the interviewerId to set
	 */
	public void setInterviewerId(String interviewerId) {
		this.interviewerId = interviewerId;
	}
	
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
	public boolean isValid() {
		return this.surveyUnitId!=null && !this.surveyUnitId.isBlank() && this.interviewerId!=null && !this.interviewerId.isBlank();
	}
	
}
