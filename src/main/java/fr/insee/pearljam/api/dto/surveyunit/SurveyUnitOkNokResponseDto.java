package fr.insee.pearljam.api.dto.surveyunit;


import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.statedata.StateDataDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitOkNokResponseDto {
	String id;
	private StateDataDto stateData;

}
