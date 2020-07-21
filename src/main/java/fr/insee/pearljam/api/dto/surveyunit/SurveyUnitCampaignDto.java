package fr.insee.pearljam.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.state.StateDto;

public class SurveyUnitCampaignDto {
	private String id;
	
	private Integer ssech;
	
	private String location;
	
	private String city;
	
	@JsonIgnoreProperties(value = { "surveyUnitCount" })
	private InterviewerDto interviewer;
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id
	 * @param ssech
	 * @param interviewer
	 */
	public SurveyUnitCampaignDto(String id, Integer ssech, String location, String city, InterviewerDto interviewer) {
		super();
		this.id = id;
		this.ssech = ssech;
		this.location = location;
		this.city = city;
		this.interviewer = interviewer;
	}
	public SurveyUnitCampaignDto() {
		super();
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the sampleIdentifiers
	 */
	public Integer getSsech() {
		return ssech;
	}
	/**
	 * @param sampleIdentifiers the sampleIdentifiers to set
	 */
	public void setSsech(Integer ssech) {
		this.ssech = ssech;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the interviewer
	 */
	public InterviewerDto getInterviewer() {
		return interviewer;
	}
	/**
	 * @param interviewer the interviewer to set
	 */
	public void setInterviewer(InterviewerDto interviewer) {
		this.interviewer = interviewer;
	}
}
