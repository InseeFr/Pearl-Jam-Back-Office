package fr.insee.pearljam.api.dto.surveyunit;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.domain.InseeSampleIdentifier;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitCampaignDto {
	private String id;
	
	private Integer ssech;
	
	private String location;
	
	private String city;
	
	private Long finalizationDate;
	
	private String campaign;
	
	private ClosingCauseType state;
	
	private List<CommentDto> comments;
	

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
	public SurveyUnitCampaignDto(String id, Integer ssech, String location, String city, Long finalizationDate, InterviewerDto interviewer) {
		super();
		this.id = id;
		this.ssech = ssech;
		this.location = location;
		this.city = city;
		this.finalizationDate = finalizationDate;
		this.interviewer = interviewer;
		
	}
	
	public SurveyUnitCampaignDto(SurveyUnit su) {
		super();
		this.id = su.getId();
		if(su.getSampleIdentifier() instanceof InseeSampleIdentifier) {
			this.ssech = ((InseeSampleIdentifier) su.getSampleIdentifier()).getSsech();
		}
	    if(su.getAddress() instanceof InseeAddress
	        && ((InseeAddress)su.getAddress()).getL6() != null
	        && ((InseeAddress)su.getAddress()).getL6().contains(" ")) {
			      String locationAndCity = ((InseeAddress)su.getAddress()).getL6();
			      this.location = locationAndCity.split(" ")[0];
				  this.city = locationAndCity.split(" ")[1];
			}
			
			if(su.getInterviewer() !=  null) {
				this.interviewer = new InterviewerDto(su.getInterviewer());
	    }
	    for(State s : su.getStates()) {
			if(StateType.FIN.equals(s.getType()) && (this.finalizationDate == null || this.finalizationDate < s.getDate())){
				this.finalizationDate = s.getDate();
			} 
		}
	    if(su.getClosingCause() != null) {
	    	this.state = su.getClosingCause().getType();
	    }

	    this.campaign = su.getCampaign().getLabel();
		this.interviewer = su.getInterviewer() != null ? new InterviewerDto(su.getInterviewer()) : null;
		
		this.comments = su.getComments().stream()
					.map(c -> new CommentDto(c))
					.collect(Collectors.toList());
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
	 * @return the finalizationDate
	 */
	public Long getFinalizationDate() {
		return finalizationDate;
	}
	/**
	 * @param finalizationDate the finalizationDate to set
	 */
	public void setFinalizationDate(Long finalizationDate) {
		this.finalizationDate = finalizationDate;
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
	public List<CommentDto> getComments() {
		return comments;
	}
	public void setComments(List<CommentDto> comments) {
		this.comments = comments;
	}
	
	public String getCampaign() {
		return campaign;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}
	public ClosingCauseType getState() {
		return state;
	}
	public void setState(ClosingCauseType state) {
		this.state = state;
	}
}
