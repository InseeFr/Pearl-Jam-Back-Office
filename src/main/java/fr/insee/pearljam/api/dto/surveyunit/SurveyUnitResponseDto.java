package fr.insee.pearljam.api.dto.surveyunit;


import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.statedata.StateDataDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitResponseDto {
	String id;
	private StateDataDto stateData;
	
	public SurveyUnitResponseDto() {
		super();
	}

	public SurveyUnitResponseDto(String id, StateDataDto stateData) {
		super();
		this.id = id;
		this.stateData = stateData;
	}
	public SurveyUnitResponseDto(String id) {
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
