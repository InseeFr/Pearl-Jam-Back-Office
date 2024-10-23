package fr.insee.pearljam.api.dto.surveyunit;


import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.statedata.StateDataDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitOkNokResponseDto {
	String id;
	private StateDataDto stateData;
	
	public SurveyUnitOkNokResponseDto() {
		super();
	}

	public SurveyUnitOkNokResponseDto(String id, StateDataDto stateData) {
		super();
		this.id = id;
		this.stateData = stateData;
	}
	public SurveyUnitOkNokResponseDto(String id) {
		super();
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public StateDataDto getStateData() {
		return stateData;
	}

	public void setStateData(StateDataDto stateData) {
		this.stateData = stateData;
	}
}
