package fr.insee.pearljam.api.dto.surveyunit;


import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.statedata.StateDataDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterrogationOkNokResponseDto {
	String id;
	private StateDataDto stateData;
	
	public InterrogationOkNokResponseDto() {
		super();
	}

	public InterrogationOkNokResponseDto(String id, StateDataDto stateData) {
		super();
		this.id = id;
		this.stateData = stateData;
	}
	public InterrogationOkNokResponseDto(String id) {
		super();
		this.id = id;
	}

}
