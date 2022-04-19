package fr.insee.pearljam.api.dto.surveyunit;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.ContactOutcomeType;
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
	
	private ClosingCauseType closingCause;
	
	private StateType state;
	
	private Boolean reading;
	
	private Boolean viewed;
	
	private String questionnaireState;
	
	private ContactOutcomeType contactOutcome;
	
	private List<CommentDto> comments;

	@JsonIgnoreProperties(value = { "surveyUnitCount" })
	private InterviewerDto interviewer;
	
	public SurveyUnitCampaignDto(String id, Integer ssech, String location, String city, Long finalizationDate, Boolean reading, Boolean viewed, InterviewerDto interviewer) {
		super();
		this.id = id;
		this.ssech = ssech;
		this.location = location;
		this.city = city;
		this.finalizationDate = finalizationDate;
		this.interviewer = interviewer;
		this.reading = reading;
		this.viewed = viewed;
		
	}
	
	public SurveyUnitCampaignDto(SurveyUnit su) {
		super();
		
		this.id = su.getId();
		this.reading=false;
		this.viewed=su.getViewed();
		if(su.getSampleIdentifier() instanceof InseeSampleIdentifier) {
			this.ssech = ((InseeSampleIdentifier) su.getSampleIdentifier()).getSsech();
		}
	    if(su.getAddress() instanceof InseeAddress
	        && ((InseeAddress)su.getAddress()).getL6() != null
	        && ((InseeAddress)su.getAddress()).getL6().contains(" ")) {
			      String locationAndCity = ((InseeAddress)su.getAddress()).getL6();
				  String[] splittedCityName=locationAndCity.split(" ");
			      this.location = splittedCityName[0];
				  this.city = Arrays.stream(splittedCityName).skip(1).collect(Collectors.joining(" "));
			}
			
			if(su.getInterviewer() !=  null) {
				this.interviewer = new InterviewerDto(su.getInterviewer());
	    }
		State currentState = null;
	    for(State s : su.getStates()) {
	    	if((StateType.FIN.equals(s.getType()) || StateType.CLO.equals(s.getType())) 
	    			&& (this.finalizationDate == null || this.finalizationDate < s.getDate())){
				this.finalizationDate = s.getDate();
			}
			if(StateType.TBR.equals(s.getType())) {
				this.reading=true;
			}
			if(currentState == null || currentState.getDate() < s.getDate()) {
				currentState = s;
			}
		}
	    if(su.getClosingCause() != null && currentState != null && !currentState.getType().equals(StateType.CLO)) {
	    	this.closingCause = su.getClosingCause().getType();
	    }
	    this.state = currentState.getType();
	    this.campaign = su.getCampaign().getLabel();
		this.interviewer = su.getInterviewer() != null ? new InterviewerDto(su.getInterviewer()) : null;
		this.comments = su.getComments().stream()
					.map(c -> new CommentDto(c))
					.collect(Collectors.toList());
		if(su.getContactOucome() != null) {
			this.contactOutcome = su.getContactOucome().getType(); 
		}
	}
	
	public SurveyUnitCampaignDto() {
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
	public StateType getState() {
		return state;
	}
	public void setState(StateType state) {
		this.state = state;
	}
	/**
	 * @return the reading
	 */
	public Boolean getReading() {
		return reading;
	}
	/**
	 * @param reading the reading to set
	 */
	public void setReading(Boolean reading) {
		this.reading = reading;
	}
	/**
	 * @return the viewed
	 */
	public Boolean getViewed() {
		return viewed;
	}
	/**
	 * @param viewed the viewed to set
	 */
	public void setViewed(Boolean viewed) {
		this.viewed = viewed;
	}

	public ClosingCauseType getClosingCause() {
		return closingCause;
	}

	public void setClosingCause(ClosingCauseType closingCause) {
		this.closingCause = closingCause;
	}

	/**
	 * @return the questionnaireState
	 */
	public String getQuestionnaireState() {
		return questionnaireState;
	}

	/**
	 * @param questionnaireState the questionnaireState to set
	 */
	public void setQuestionnaireState(String questionnaireState) {
		if(questionnaireState==null) {
			this.questionnaireState="NULL";
		}else{
			this.questionnaireState = questionnaireState;	
		}
	}

	/**
	 * @return the contatctOutcome
	 */
	public ContactOutcomeType getContactOutcome() {
		return contactOutcome;
	}

	/**
	 * @param contatctOutcome the contatctOutcome to set
	 */
	public void setContactOutcome(ContactOutcomeType contactOutcome) {
		this.contactOutcome = contactOutcome;
	}
}
