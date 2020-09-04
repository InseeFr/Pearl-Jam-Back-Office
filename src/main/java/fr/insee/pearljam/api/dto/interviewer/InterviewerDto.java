package fr.insee.pearljam.api.dto.interviewer;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.Interviewer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterviewerDto {
	private String id;
	private String interviewerFirstName;
  private String interviewerLastName;
  private Long surveyUnitCount;

	
	public InterviewerDto(String id, String interviewerFirstName, String interviewerLastName, Long surveyUnitCount) {
		super();
		this.id = id;
		this.interviewerFirstName = interviewerFirstName;
		this.interviewerLastName = interviewerLastName;
		this.surveyUnitCount = surveyUnitCount;
	}
	
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
	
	
	public InterviewerDto() {
		super();
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the interviewerFirstName
	 */
	public String getInterviewerFirstName() {
		return interviewerFirstName;
	}
	/**
	 * @param interviewerFirstName the interviewerFirstName to set
	 */
	public void setInterviewerFirstName(String interviewerFirstName) {
		this.interviewerFirstName = interviewerFirstName;
	}
	/**
	 * @return the interviewerLastName
	 */
	public String getInterviewerLastName() {
		return interviewerLastName;
	}
	/**
	 * @param interviewerLastName the interviewerLastName to set
	 */
	public void setInterviewerLastName(String interviewerLastName) {
		this.interviewerLastName = interviewerLastName;
	}
	/**
	 * @return the collectionEndDate
	 */
	public Long getSurveyUnitCount() {
		return surveyUnitCount;
	}
	/**
	 * @param surveyUnitCount the collectionEndDate to set
	 */
	public void setSurveyUnitCount(Long surveyUnitCount) {
		this.surveyUnitCount = surveyUnitCount;
	}
	
	
	
}
