package fr.insee.pearljam.api.dto.state;

import java.util.List;

public class SurveyUnitStatesDto {
	private String id;
	
	private List<StateDto> states;
	
	public SurveyUnitStatesDto(String id, List<StateDto> states){
		super();
		this.id = id;
		this.states = states;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<StateDto> getStates() {
		return states;
	}

	public void setStates(List<StateDto> states) {
		this.states = states;
	}

}
